package demo.rabbit.retry.backoff;

import demo.rabbit.retry.backoff.handler.PublishingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RestConfiguration {

  @Bean
  RouterFunction<ServerResponse> publishBackoffQueue(PublishingHandler publishingHandler) {
    RequestPredicate requestPredicate = RequestPredicates
        .GET("send-backoff-queue/{id}");
    HandlerFunction<ServerResponse> handlerFunction = serverRequest ->
        publishingHandler.handle(serverRequest.pathVariable("id"))
            .then(ServerResponse.ok().build());
    return RouterFunctions.route(requestPredicate, handlerFunction);
  }
}
