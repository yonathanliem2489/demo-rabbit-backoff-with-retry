package demo.rabbit.retry.backoff.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfiguration {

  @Bean
  public SimpleRabbitListenerContainerFactory defaultContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);

    return factory;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory retryQueuesContainerFactory(
      ConnectionFactory connectionFactory, RetryQueuesInterceptor retryInterceptor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);

    Advice[] adviceChain = { retryInterceptor };
    factory.setAdviceChain(adviceChain);

    return factory;
  }

  @Bean
  public RetryQueues retryQueues() {
    return new RetryQueues(5000, 3.0, 7000, 3);
  }

  @Bean
  public RetryQueuesInterceptor retryQueuesInterceptor(RabbitTemplate rabbitTemplate,
      RetryQueues retryQueues, ObjectMapper objectMapper) {
    return new RetryQueuesInterceptor(rabbitTemplate, retryQueues, objectMapper);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    return new RabbitTemplate(connectionFactory);
  }

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  ListenerHandler listenerHandler(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
    return new ListenerHandler(rabbitTemplate, objectMapper);
  }
}
