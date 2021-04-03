package demo.rabbit.retry.backoff.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.model.Request;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class ListenerHandler {

  private final ObjectMapper objectMapper;
  private final RabbitTemplate rabbitTemplate;

  public ListenerHandler(RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.rabbitTemplate = rabbitTemplate;
  }

  @RabbitListener(queues = BindingConfiguration.QUEUE_NAME, containerFactory = "retryContainerFactory")
  public void consumeBlocking(Message message) throws IOException {
    Request payload = objectMapper.readValue(message.getBody(), Request.class);
    log.info("Processing message from demo queue: {}", payload.getKey());

    Mono.error(new IllegalAccessException("Error occured!")).block();
  }

  @RabbitListener(queues = BindingConfiguration.NON_BLOCKING_QUEUE_NAME,
      containerFactory = "retryQueuesContainerFactory",
      ackMode = "MANUAL")
  public void consumeNonBlocking(Message message) throws IOException {
    Request payload = objectMapper.readValue(message.getBody(), Request.class);
    log.info("Processing non-blocking message from demo queue: {}", payload.getKey());

    Mono.error(new IllegalStateException("Error occured!")).block();
  }

}
