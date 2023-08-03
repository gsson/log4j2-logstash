package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.message.ParameterizedMessage;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TagOps {
  static Tags tagsFromContext(@Nullable Function<ContextView, Tags> tagsFromContext, ContextView contextView) {
    if (tagsFromContext != null) {
      try {
        var tags = tagsFromContext.apply(contextView);
        if (tags != null) {
          return tags;
        }
      } catch (RuntimeException e) {
        // Ignore exceptions
      }
    }
    return Tags.empty();
  }

  static String formatString(String message, Object... args) {
    return ParameterizedMessage.format(message, args);
  }
}
