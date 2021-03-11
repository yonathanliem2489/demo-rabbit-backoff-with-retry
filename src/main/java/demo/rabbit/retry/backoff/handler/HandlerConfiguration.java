package demo.rabbit.retry.backoff.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.config.BindingConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfiguration {

  @Bean
  PublishingHandler publisherBackoffHandler(RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper) {
    rabbitTemplate.setDefaultReceiveQueue(BindingConfiguration.QUEUE_NAME);
    rabbitTemplate.setExchange(BindingConfiguration.EXCHANGE_NAME);
    rabbitTemplate.setRoutingKey(BindingConfiguration.ROUTING_KEY_NAME);
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(objectMapper));
    return new DefaultPublishingHandler(rabbitTemplate, objectMapper);
  }
}
