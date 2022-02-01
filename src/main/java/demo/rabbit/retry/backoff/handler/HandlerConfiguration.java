package demo.rabbit.retry.backoff.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.handler.delayed.DefaultPublishingDelayedHandler;
import demo.rabbit.retry.backoff.handler.delayed.PublishingDelayedHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

  @Bean
  PublishingHandler publisherBackoffHandler(RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper) {
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
    return new DefaultPublishingHandler(rabbitTemplate, objectMapper);
  }

  @Bean
  PublishingDelayedHandler publishingDelayedHandler(RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper) {
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
    return new DefaultPublishingDelayedHandler(rabbitTemplate, objectMapper);
  }
}
