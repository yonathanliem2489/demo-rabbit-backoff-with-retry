package demo.rabbit.retry.backoff;

import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.handler.HandlerConfiguration;
import demo.rabbit.retry.backoff.handler.PublishingHandler;
import demo.rabbit.retry.backoff.handler.delayed.PublishingDelayedHandler;
import demo.rabbit.retry.backoff.listener.ListenerConfiguration;
import demo.rabbit.retry.backoff.listener.ObservableRejectAndDontRequeueRecoverer;
import demo.rabbit.retry.backoff.listener.RetryQueuesInterceptor;
import demo.rabbit.retry.backoff.listener.delayed.DelayedConfiguration;
import demo.rabbit.retry.backoff.listener.delayed.RetryQueuesDelayedInterceptor;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration({
    RabbitAutoConfiguration.class,
    BindingConfiguration.class,
    HandlerConfiguration.class,
    JacksonAutoConfiguration.class
})
@ContextConfiguration(classes = {DelayedConfiguration.class, ListenerConfiguration.class})
@TestPropertySource(properties = {
    "logging.level.root=INFO"
})
public class DelayedExponentialBackoffLiveTest {

    @Autowired
    private PublishingDelayedHandler publishingDelayedHandler;

    @Autowired
    private RetryQueuesDelayedInterceptor retryQueues;

    @Autowired
    private ObservableRejectAndDontRequeueRecoverer observableRecoverer;

    @Test
    public void whenSendToDelayedQueueAndException_thenAllMessageProcessed() throws Exception {
        int nb = 1;

//        CountDownLatch latch = new CountDownLatch(nb);
//        retryQueues.setObserver(() -> latch.countDown());

        for (int i = 1; i <= nb; i++) {
            log.info("publish message #{}", i);
            publishingDelayedHandler.handle("non-blocking delayed message " + i,
                10000).subscribe();
        }

//        latch.await();
    }

}
