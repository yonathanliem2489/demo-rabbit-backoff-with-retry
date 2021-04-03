package demo.rabbit.retry.backoff.handler;

import reactor.core.publisher.Mono;

public interface PublishingHandler {

  Mono<Void> handle(String id);

  Mono<Void> handleNonBlocking(String id);
}
