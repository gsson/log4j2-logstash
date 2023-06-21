package se.fnord.logtags.log4j2_logstash.examples.webflux;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import se.fnord.logtags.log4j2_logstash.reactor.ContextTags;
import se.fnord.logtags.log4j2_logstash.reactor.SignalLoggerBuilder;
import se.fnord.logtags.log4j2_logstash.reactor.SimpleLogger;
import se.fnord.logtags.tags.Tags;

import java.util.function.Consumer;

import static org.apache.logging.log4j.Level.INFO;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Main {
  private static final Consumer<Signal<? extends ServerRequest>> REQUEST_LOGGER = SignalLoggerBuilder
      .forLogger(LogManager.getLogger())
      .withContextTags(ServerLogging::tagsFromContext)
      .<ServerRequest>onNext(INFO, (tags, request) -> tags.add("message", "Request received"))
      .build();

  private static final Consumer<Signal<? extends ServerResponse>> RESPONSE_LOGGER = SignalLoggerBuilder
      .forLogger(LogManager.getLogger())
      .withContextTags(ServerLogging::tagsFromContext)
      .<ServerResponse>onNext(INFO, (tags, response) -> ServerLogging.addResponseTags(tags, response).add("message", "Request completed"))
      .build();

  private static final SimpleLogger log = SimpleLogger.getSimpleLogger()
      .withContextTags(ServerLogging::tagsFromContext);

  private static Mono<ServerResponse> contextFilter(ServerRequest request, HandlerFunction<ServerResponse> handler) {
    return handler.handle(request)
        .contextWrite(ServerLogging.updateContext(request));
  }

  private static Mono<ServerResponse> logFilter(ServerRequest request, HandlerFunction<ServerResponse> handler) {
    return Mono.just(request)
        .doOnEach(REQUEST_LOGGER)
        .flatMap(handler::handle)
        .doOnEach(RESPONSE_LOGGER);
  }

  public Mono<ServerResponse> logString(ServerRequest request) {
    return ServerResponse.ok()
        .bodyValue("hello world!")
        .doOnEach(log.info("This happened"))
        .contextWrite(ContextTags.addTag("another_tag", "value"));
  }

  public Mono<ServerResponse> returnError(ServerRequest _request) {
    return Mono.error(new IllegalArgumentException("This is an error"));
  }

  public Mono<ServerResponse> logStringFunction(ServerRequest request) {
    return ServerResponse.ok()
        .bodyValue("hello world!")
        .doOnEach(log.info(s -> "Have a look: " + s.statusCode().value()))
        .contextWrite(ContextTags.addTag("another_tag", "value"));
  }

  public Mono<ServerResponse> logTags(ServerRequest request) {
    return ServerResponse.ok()
        .bodyValue("hello world!")
        .doOnEach(log.infoTags(Tags.of(
            "meaning", 42,
            "message", "This happened")))
        .contextWrite(ContextTags.addTag("another_tag", "value"));
  }

  public Mono<ServerResponse> logTagsFunction(ServerRequest request) {
    return ServerResponse.ok()
        .bodyValue("hello world!")
        .doOnEach(log.infoTags(s -> Tags.of(
            "test_status", s.statusCode().value(),
            "message", "Have a look: " + s.statusCode().value())))
        .contextWrite(ContextTags.addTag("another_tag", "value"));
  }

  @Bean
  public RouterFunction<ServerResponse> routes() {
    return RouterFunctions
        .route(RequestPredicates.GET("/logString"), this::logString)
        .andRoute(RequestPredicates.GET("/returnError"), this::returnError)
        .andRoute(RequestPredicates.GET("/logStringFunction"), this::logStringFunction)
        .andRoute(RequestPredicates.GET("/logTags"), this::logTags)
        .andRoute(RequestPredicates.GET("/logTagsFunction"), this::logTagsFunction)
        .filter(Main::logFilter)
        .filter(Main::contextFilter);
  }

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
