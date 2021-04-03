package demo.rabbit.retry.backoff;

import demo.rabbit.retry.backoff.config.BindingConfiguration;
import demo.rabbit.retry.backoff.handler.HandlerConfiguration;
import demo.rabbit.retry.backoff.handler.PublishingHandler;
import demo.rabbit.retry.backoff.listener.ListenerConfiguration;
import demo.rabbit.retry.backoff.listener.ObservableRejectAndDontRequeueRecoverer;
import demo.rabbit.retry.backoff.listener.RetryQueuesInterceptor;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration({
    RabbitAutoConfiguration.class,
    BindingConfiguration.class,
    HandlerConfiguration.class
})
@ContextConfiguration(classes = { ListenerConfiguration.class })
@TestPropertySource(properties = {
    "logging.level.root=INFO"
})
public class ExponentialBackoffLiveTest {

    @Autowired
    private PublishingHandler publishingHandler;

    @Autowired
    private RetryQueuesInterceptor retryQueues;

    @Autowired
    private ObservableRejectAndDontRequeueRecoverer observableRecoverer;


    @Test
    public void whenSendToBlockingQueueAndException_thenAllMessageProcessed() throws Exception {
        int nb = 2;

        CountDownLatch latch = new CountDownLatch(nb);
        observableRecoverer.setObserver(() -> latch.countDown());

        for (int i = 1; i <= nb; i++) {
            publishingHandler.handle("blocking message " + i).block();
        }

        latch.await();
    }

    @Test
    public void whenSendToNonBlockingQueueAndException_thenAllMessageProcessed() throws Exception {
        int nb = 3;

        CountDownLatch latch = new CountDownLatch(nb);
        retryQueues.setObserver(() -> latch.countDown());

        for (int i = 1; i <= nb; i++) {
            publishingHandler.handleNonBlocking("non-blocking message " + i).block();
        }

        latch.await();
    }

}
