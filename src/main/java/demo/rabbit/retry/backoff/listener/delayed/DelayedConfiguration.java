package demo.rabbit.retry.backoff.listener.delayed;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rabbit.retry.backoff.listener.RetryQueues;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class DelayedConfiguration {

  @Bean
  ListenerDelayedHandler listenerDelayedHandler(ObjectMapper objectMapper) {
    return new ListenerDelayedHandler(objectMapper);
  }

  // delayed configuration
  @Bean
  public SimpleRabbitListenerContainerFactory retryQueuesDelayedContainerFactory(
      ConnectionFactory connectionFactory, RetryQueuesDelayedInterceptor retryQueuesDelayedInterceptor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);

    Advice[] adviceChain = { retryQueuesDelayedInterceptor };
    factory.setAdviceChain(adviceChain);

    return factory;
  }

  @Bean
  public RetryQueues retryQueuesDelayed() {
    return new RetryQueues(10000, 3.0, 20000, 3);
  }

  @Bean
  public RetryQueuesDelayedInterceptor retryQueuesDelayedInterceptor(RabbitTemplate rabbitTemplate,
      RetryQueues retryQueuesDelayed, ObjectMapper objectMapper) {
    return new RetryQueuesDelayedInterceptor(rabbitTemplate, retryQueuesDelayed, objectMapper);
  }
}
