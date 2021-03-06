package demo.rabbit.retry.backoff.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.model.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultPublishingHandler implements PublishingHandler {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public DefaultPublishingHandler(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public Mono<Void> handle(String id) {
    Request request = Request.builder()
        .key(id)
        .build();

    log.info("publish message");
    return Mono.fromRunnable(() ->
        rabbitTemplate.convertAndSend(request, processor -> {
          processor.getMessageProperties().setExpiration("10000");
          return processor;
        })
    ).then();
  }
}
