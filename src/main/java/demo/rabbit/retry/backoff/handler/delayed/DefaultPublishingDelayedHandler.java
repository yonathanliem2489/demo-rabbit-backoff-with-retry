package demo.rabbit.retry.backoff.handler.delayed;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.handler.PublishingHandler;
import demo.rabbit.retry.backoff.model.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultPublishingDelayedHandler implements PublishingDelayedHandler {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public DefaultPublishingDelayedHandler(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public Mono<Void> handle(String id, long delayDuration) {
    Request request = Request.builder()
        .key(id)
        .build();

    log.info("publish delayed message");
    return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(
        BindingConfiguration.EXCHANGE_DELAYED_NAME,
        BindingConfiguration.ROUTING_KEY_DELAYED_NAME,
          request, processor -> {
            MessageProperties props = processor.getMessageProperties();
            props.setExpiration("10000");
            props.setHeader("x-delay", String.valueOf(delayDuration));

          return processor;
      })
    ).then();
  }
}
