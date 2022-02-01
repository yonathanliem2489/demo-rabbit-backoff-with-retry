package demo.rabbit.retry.backoff.listener.delayed;


import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.model.Request;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class ListenerDelayedHandler {

  private final ObjectMapper objectMapper;

  public ListenerDelayedHandler(
      ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @RabbitListener(queues = BindingConfiguration.QUEUE_DELAYED_NAME,
      containerFactory = "retryQueuesDelayedContainerFactory"
  )
  public void consumeDelayed(Message message) throws IOException {
    Request payload = objectMapper.readValue(message.getBody(), Request.class);
    log.info("Processing message from demo delayed queue: {}", payload.getKey());

    Mono.error(new IllegalAccessException("Error occurred!")).block();
  }

}
