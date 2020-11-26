package se.fnord.logtags.log4j2_logstash.reactor.sl2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ContextLogger {
  private final Logger logger;
  private final Function<Context, Tags> contextTags;

  ContextLogger(Logger logger, Function<Context, Tags> contextTags) {
    this.logger = Objects.requireNonNull(logger);
    this.contextTags = Objects.requireNonNull(contextTags);
  }

  public void log(Level level, Context context, Tags tags, Throwable throwable) {
    if (logger.isEnabled(level)) {
      var allTags = contextTags.apply(context).add(tags);
      var message = new TaggedMessage(allTags, throwable);
      logger.log(level, message);
    }
  }

  public void log(Level level, Context context, Supplier<Tags> tags, Throwable throwable) {
    if (logger.isEnabled(level)) {
      var allTags = contextTags.apply(context).add(tags.get());
      var message = new TaggedMessage(allTags, throwable);
      logger.log(level, message);
    }
  }

  public <T> void log(Level level, Context context, T value, Function<? super T, Tags> tags, Throwable throwable) {
    if (logger.isEnabled(level)) {
      var allTags = contextTags.apply(context).add(tags.apply(value));
      var message = new TaggedMessage(allTags, throwable);
      logger.log(level, message);
    }
  }

  public boolean isEnabled(Level level) {
    return logger.isEnabled(level);
  }
}
