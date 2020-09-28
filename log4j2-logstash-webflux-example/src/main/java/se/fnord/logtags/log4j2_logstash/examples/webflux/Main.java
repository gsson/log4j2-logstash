package se.fnord.logtags.log4j2_logstash.examples.webflux;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import se.fnord.logtags.log4j2_logstash.reactor.SignalLoggerBuilder;

import java.util.function.Consumer;

import static org.apache.logging.log4j.Level.INFO;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Main {
  private static final Consumer<Signal<? extends ServerRequest>> REQUEST_LOGGER = SignalLoggerBuilder
      .forLogger(LogManager.getLogger())
      .withContextTags(ServerLogging::tagsFromContext)
      .<ServerRequest>onNext(INFO, (t, r) -> t.add("message", "Request received"))
      .build();

  private static final Consumer<Signal<? extends ServerResponse>> RESPONSE_LOGGER = SignalLoggerBuilder
      .forLogger(LogManager.getLogger())
      .withContextTags(ServerLogging::tagsFromContext)
      .<ServerResponse>onNext(INFO, (t, r) -> ServerLogging.addResponseTags(t, r).add("message", "Request completed"))
      .build();

  private static Mono<ServerResponse> contextFilter(ServerRequest request, HandlerFunction<ServerResponse> handler) {
    return handler.handle(request)
        .subscriberContext(ServerLogging.updateContext(request));
  }

  private static Mono<ServerResponse> logFilter(ServerRequest request, HandlerFunction<ServerResponse> handler) {
    return Mono.just(request)
        .doOnEach(REQUEST_LOGGER)
        .flatMap(handler::handle)
        .doOnEach(RESPONSE_LOGGER);
  }

  @Bean
  public RouterFunction<ServerResponse> routes() {
    return RouterFunctions.route(RequestPredicates.GET("/"), r -> ServerResponse.ok().bodyValue("hello world!"))
        .filter(Main::logFilter)
        .filter(Main::contextFilter);
  }

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
