package demo.rabbit.retry.backoff;

import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.handler.HandlerConfiguration;
import demo.rabbit.retry.backoff.handler.PublishingHandler;
import demo.rabbit.retry.backoff.listener.ListenerConfiguration;
import demo.rabbit.retry.backoff.listener.RetryQueuesInterceptor;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This live test requires:
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration({
    RabbitAutoConfiguration.class,
    BindingConfiguration.class,
    HandlerConfiguration.class
})
@ContextConfiguration(classes = { ListenerConfiguration.class })
public class ExponentialBackoffLiveTest {

    @Autowired
    private PublishingHandler publishingHandler;

    @Autowired
    private RetryQueuesInterceptor retryQueues;

    @Test
    public void whenSendToNonBlockingQueueAndException_thenAllMessageProcessed() throws Exception {
        int nb = 1;

        CountDownLatch latch = new CountDownLatch(nb);
        retryQueues.setObserver(() -> latch.countDown());

        for (int i = 1; i <= nb; i++) {
            publishingHandler.handle("message " + i).block();
        }

        latch.await();
    }

}
