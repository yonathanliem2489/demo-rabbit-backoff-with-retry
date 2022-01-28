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
  public static final String PARKING_QUEUE = QUEUE_NAME + ".parking";
  public static final String PARKING_KEY = PARKING_QUEUE + ".key";

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue queue() {
    return QueueBuilder.durable(QUEUE_NAME)
        .build();
  }

  @Bean
  Queue parkingQueue() {
    return QueueBuilder.durable(PARKING_QUEUE)
        .build();
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(queue()).to(exchange())
        .with(ROUTING_KEY_NAME);
  }

  @Bean
  Binding parkingBinding() {
    return BindingBuilder.bind(parkingQueue()).to(exchange())
        .with(PARKING_KEY);
  }

  // non blocking
  public static final String NON_BLOCKING_QUEUE_NAME = "demo.retry.backoff.non-blocking.queue";
  public static final String NON_BLOCKING_EXCHANGE_NAME = "demo.retry.backoff.non-blocking.exchange";

  public static final String NON_BLOCKING_ROUTING_KEY_NAME = NON_BLOCKING_QUEUE_NAME + ".key";

  public static final String NON_BLOCKING_WAIT_QUEUE = NON_BLOCKING_QUEUE_NAME + ".wait";
  public static final String NON_BLOCKING_ROUTING_WAIT_KEY = NON_BLOCKING_WAIT_QUEUE + ".key";

  public static final String NON_BLOCKING_PARKING_QUEUE = NON_BLOCKING_QUEUE_NAME + ".parking";
  public static final String NON_BLOCKING_PARKING_KEY = NON_BLOCKING_PARKING_QUEUE + ".key";

  @Bean
  TopicExchange nonBlockingExchange() {
    return new TopicExchange(NON_BLOCKING_EXCHANGE_NAME);
  }


  @Bean
  public Queue nonBlockingQueue() {
    return QueueBuilder.durable(NON_BLOCKING_QUEUE_NAME)
        .ttl(1*60*1000)
        .build();
  }

  @Bean
  public Queue nonBlockingWaitQueue() {
    return QueueBuilder.durable(NON_BLOCKING_WAIT_QUEUE)
        .deadLetterExchange(NON_BLOCKING_EXCHANGE_NAME)
        .deadLetterRoutingKey(NON_BLOCKING_ROUTING_KEY_NAME)
        .build();
  }

  @Bean
  Queue nonBlockingParkingQueue() {
    return QueueBuilder.durable(NON_BLOCKING_PARKING_QUEUE)
        .build();
  }

  @Bean
  Binding nonBlockingBinding() {
    return BindingBuilder.bind(nonBlockingQueue()).to(nonBlockingExchange())
        .with(NON_BLOCKING_ROUTING_KEY_NAME);
  }

  @Bean
  Binding nonBlockingWaitBinding() {
    return BindingBuilder.bind(nonBlockingWaitQueue()).to(nonBlockingExchange())
        .with(NON_BLOCKING_ROUTING_WAIT_KEY);
  }

  @Bean
  Binding nonBlockingParkingBinding() {
    return BindingBuilder.bind(nonBlockingParkingQueue()).to(nonBlockingExchange())
        .with(NON_BLOCKING_PARKING_KEY);
  }





}
