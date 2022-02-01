package demo.rabbit.retry.backoff.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueBuilder.Overflow;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

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

  // non blocking binding

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

  // delayed binding

  public static final String QUEUE_DELAYED_NAME = "demo.delayed.queue";
  public static final String EXCHANGE_DELAYED_NAME = "demo.delayed.exchange";
  public static final String ROUTING_KEY_DELAYED_NAME = QUEUE_DELAYED_NAME + ".key";

  public static final String DELAYED_PARKING_QUEUE = QUEUE_DELAYED_NAME + ".parking";
  public static final String DELAYED_PARKING_KEY = DELAYED_PARKING_QUEUE + ".key";

  @Bean
  CustomExchange delayedExchange() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-delayed-type", "direct");
    return new CustomExchange(EXCHANGE_DELAYED_NAME, "x-delayed-message",
        true, false, args);
  }


  @Bean
  public Queue delayedQueue() {
    return QueueBuilder.durable(QUEUE_DELAYED_NAME)
        .build();
  }

  @Bean
  Binding delayedBinding() {
    return new Binding(QUEUE_DELAYED_NAME,
        DestinationType.QUEUE, EXCHANGE_DELAYED_NAME, ROUTING_KEY_DELAYED_NAME,
        new HashMap<>());
  }

  @Bean
  Queue delayedParkingQueue() {
    return QueueBuilder.durable(DELAYED_PARKING_QUEUE)
        .maxLength(10000)
        .overflow(Overflow.dropHead)
        .build();
  }

  @Bean
  Binding delayedParkingBinding() {
    return new Binding(DELAYED_PARKING_QUEUE,
        DestinationType.QUEUE, EXCHANGE_DELAYED_NAME, DELAYED_PARKING_KEY,
        new HashMap<>());
  }

}
