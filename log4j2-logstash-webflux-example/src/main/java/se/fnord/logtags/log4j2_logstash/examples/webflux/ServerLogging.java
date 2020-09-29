package se.fnord.logtags.log4j2_logstash.examples.webflux;

import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.reactor.ContextTags;
import se.fnord.logtags.log4j2_logstash.reactor.ElapsedTime;
import se.fnord.logtags.tags.Tags;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Function;

public class ServerLogging {
  public static Tags requestTags(ServerRequest request) {
    var requestId = request.exchange().getRequest().getId();

    var method = request.methodName();
    var path = request.attribute(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE)
        .map(Object::toString)
        .orElse(request.path());

    var remoteHost = request.remoteAddress()
        .map(InetSocketAddress::getHostString)
        .orElse(null);

    return Tags.of("request_id", requestId, "remote_host", remoteHost, "method", method, "requested_uri", path);
  }

  public static Tags responseTags(ServerResponse response) {
    return Tags.of("status_code", Objects.toString(response.rawStatusCode()));
  }

  public static Tags addResponseTags(Tags tags, ServerResponse response) {
    return tags.add(responseTags(response));
  }

  public static Tags tagsFromContext(Context context) {
    return ContextTags.tagsFromContext(context).add(ElapsedTime.tagsFromContext(context));
  }

  private static Function<Context, Context> addRequestTagsToContext(ServerRequest request) {
    return ContextTags.addTags(requestTags(request));
  }

  public static Function<Context, Context> updateContext(ServerRequest request) {
    return addRequestTagsToContext(request)
        .andThen(ElapsedTime::addToContext);
  }
}
