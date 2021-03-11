package demo.rabbit.retry.backoff.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BindingConfiguration {
  public static final String QUEUE_NAME = "demo.retry.backoff.queue";
  public static final String EXCHANGE_NAME = "demo.retry.backoff.exchange";

  public static final String ROUTING_KEY_NAME = QUEUE_NAME + ".key";

  public static final String WAIT_QUEUE = QUEUE_NAME + ".wait";
  public static final String ROUTING_WAIT_KEY = WAIT_QUEUE + ".key";

  public static final String PARKING_QUEUE = QUEUE_NAME + ".parking";
  public static final String PARKING_KEY = PARKING_QUEUE + ".key";

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue queue() {
    return QueueBuilder.nonDurable(QUEUE_NAME)
        .build();
  }

  @Bean
  public Queue waitQueue() {
    return QueueBuilder.nonDurable(WAIT_QUEUE)
        .deadLetterExchange(EXCHANGE_NAME)
        .deadLetterRoutingKey(ROUTING_KEY_NAME)
        .build();
  }

  @Bean
  Queue parkingQueue() {
    return new Queue(PARKING_QUEUE);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(queue()).to(exchange())
        .with(ROUTING_KEY_NAME);
  }

  @Bean
  Binding waitBinding() {
    return BindingBuilder.bind(waitQueue()).to(exchange())
        .with(ROUTING_WAIT_KEY);
  }

  @Bean
  Binding parkingBinding() {
    return BindingBuilder.bind(parkingQueue()).to(exchange())
        .with(PARKING_KEY);
  }
}
