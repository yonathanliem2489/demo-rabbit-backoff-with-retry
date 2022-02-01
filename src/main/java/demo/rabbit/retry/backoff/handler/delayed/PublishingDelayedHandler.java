package demo.rabbit.retry.backoff.handler.delayed;

import reactor.core.publisher.Mono;

public interface PublishingDelayedHandler {

  Mono<Void> handle(String id, long delayDuration);
}
