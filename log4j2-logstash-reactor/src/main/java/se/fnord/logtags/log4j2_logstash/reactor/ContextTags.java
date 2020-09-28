package se.fnord.logtags.log4j2_logstash.reactor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.Tags;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ContextTags {
  // Warning disabled since the return value of context.getOrDefault() is marked @Nullable,
  // but it can only return null if the supplied default value is null.
  @SuppressFBWarnings({ "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE" })
  public static Tags tagsFromContext(Context context) {
    return context.getOrDefault(Tags.class, Tags.empty());
  }

  public static Function<Context, Context> updateTags(UnaryOperator<Tags> logTags) {
    return c -> updateTags(c, logTags);
  }

  public static Context updateTags(Context context, UnaryOperator<Tags> logTags) {
    var oldTags = tagsFromContext(context);
    var newTags = logTags.apply(oldTags);
    return context.put(Tags.class, newTags);
  }

  public static Function<Context, Context> addTags(Tags logTags) {
    return c -> addTags(c, logTags);
  }

  public static Context addTags(Context context, Tags logTags) {
    var oldTags = tagsFromContext(context);
    var newTags = oldTags.add(logTags);
    return context.put(Tags.class, newTags);
  }

  public static Function<Context, Context> addTag(String key, String value) {
    return updateTags(tags -> tags.add(key, value));
  }
}
