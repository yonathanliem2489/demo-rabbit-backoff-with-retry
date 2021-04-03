package demo.rabbit.retry.backoff.listener;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.model.Request;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RetryQueuesInterceptor implements MethodInterceptor {

    private RabbitTemplate rabbitTemplate;

    private RetryQueues retryQueues;

    private Runnable observer;

    private final ObjectMapper mapper;

    public RetryQueuesInterceptor(RabbitTemplate rabbitTemplate, RetryQueues retryQueues,
        ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryQueues = retryQueues;
        this.mapper = objectMapper;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return tryConsume(invocation, this::ack, (mac, e) -> {
            try {
                int retryCount = tryGetRetryCountOrFail(mac, e);
                sendToNextRetryQueue(mac, retryCount);
            } catch (Throwable t) {
                if (observer != null) {
                    observer.run();
                }

                log.error("retry exhausted caused {}", t.getMessage());
                publishToParkingLotQueue(t.getCause().getMessage(), mac.message);
                throw new RuntimeException(t);
            }
        });
    }

    public void setObserver(Runnable observer) {
        this.observer = observer;
    }

    private Object tryConsume(MethodInvocation invocation,
        Consumer<MessageAndChannel> successHandler,
        BiConsumer<MessageAndChannel, Throwable> errorHandler) throws Throwable {

        MessageAndChannel mac = new MessageAndChannel((Message) invocation.getArguments()[1],
            (Channel) invocation.getArguments()[0]);
        Object ret = null;
        try {
            ret = invocation.proceed();
            successHandler.accept(mac);
        } catch (Throwable e) {
            errorHandler.accept(mac, e);
        }
        return ret;
    }

    private void ack(MessageAndChannel mac) {
        try {
            mac.channel.basicAck(mac.message.getMessageProperties()
                .getDeliveryTag(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int tryGetRetryCountOrFail(MessageAndChannel mac, Throwable originalError) throws Throwable {
        MessageProperties props = mac.message.getMessageProperties();

        String xRetriedCountHeader = (String) props.getHeader("x-retried-count");
        final int xRetriedCount = xRetriedCountHeader == null ? 0 : Integer.valueOf(xRetriedCountHeader);

        if (retryQueues.retriesExhausted(xRetriedCount)) {
            mac.channel.basicReject(props.getDeliveryTag(), false);

            throw originalError;
        }

        return xRetriedCount;
    }

    private void sendToNextRetryQueue(MessageAndChannel mac, int retryCount) throws Exception {
        rabbitTemplate.convertAndSend(BindingConfiguration.NON_BLOCKING_EXCHANGE_NAME,
            BindingConfiguration.NON_BLOCKING_ROUTING_WAIT_KEY, mac.message, m -> {
            MessageProperties props = m.getMessageProperties();
            props.setExpiration(String.valueOf(retryQueues.getTimeToWait(retryCount)));
            props.setHeader("x-retried-count", String.valueOf(retryCount + 1));
            props.setHeader("x-original-exchange", props.getReceivedExchange());
            props.setHeader("x-original-routing-key", props.getReceivedRoutingKey());

            return m;
        });

        mac.channel.basicReject(mac.message.getMessageProperties()
            .getDeliveryTag(), false);
    }

    private static class MessageAndChannel {
        private final Message message;
        private final Channel channel;

        private MessageAndChannel(Message message, Channel channel) {
            this.message = message;
            this.channel = channel;
        }
    }

    private void publishToParkingLotQueue(String problem,
        Message message) {
        try {
            rabbitTemplate.convertAndSend(BindingConfiguration.NON_BLOCKING_EXCHANGE_NAME,
                BindingConfiguration.NON_BLOCKING_PARKING_KEY, ParkingLot.builder()
                    .problem(problem)
                    .request(mapper.readValue(message.getBody(), Request.class).toString())
                    .build(), processor -> {

                    // you can set failed information in header properties
                    processor.getMessageProperties().setHeader("total-retried",
                        message.getMessageProperties().getHeader("x-retried-count"));
                    return processor;
                });
        } catch (IOException e) {
            log.error("error parse {}", e.getMessage());
        }
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    private static class ParkingLot implements Serializable {

        private static final long serialVersionUID = 5510074191983722944L;
        private final String problem;
        private final String request;

        @JsonCreator
        @lombok.Builder(builderClassName = "Builder")
        ParkingLot(@JsonProperty String problem, @JsonProperty String request) {
            this.problem = problem;
            this.request = request;
        }
    }
}