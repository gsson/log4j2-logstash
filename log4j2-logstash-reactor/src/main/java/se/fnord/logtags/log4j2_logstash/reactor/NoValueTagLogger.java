package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.function.Function;

public record NoValueTagLogger(TagLogger.Level level, @Nullable Function<Tags, ? extends ToTags> getTags) {
  private boolean isEnabled(TagLogger logger) {
    return getTags != null && logger.isEnabled(level);
  }

  private void log(TagLogger logger, Tags contextTags, @Nullable Throwable throwable) {
    var toTags = NoValueTagOps.apply(getTags, contextTags);
    var tags = toTags == null ? null : toTags.toTags();
    if (tags != null) {
      logger.log(level, tags, throwable);
    }
  }

  public void handle(TagLogger logger, Tags contextTags, @Nullable Throwable throwable) {
    if (isEnabled(logger)) {
      log(logger, contextTags, throwable);
    }
  }

  public <S> void handleSignal(TagLogger logger, Signal<? extends S> signal, @Nullable Function<ContextView, Tags> tagsFromContext) {
    if (isEnabled(logger)) {
      var contextTags = TagOps.tagsFromContext(tagsFromContext, signal.getContextView());
      log(logger, contextTags, signal.getThrowable());
    }
  }

  public static NoValueTagLogger empty() {
    return new NoValueTagLogger(TagLogger.Level.TRACE, null);
  }

  public NoValueTagLogger withLevel(TagLogger.Level level) {
    if (level == this.level) {
      return this;
    }
    return new NoValueTagLogger(level, getTags);
  }

  public NoValueTagLogger updateTags(Function<Function<Tags, ? extends ToTags>, Function<Tags, ? extends ToTags>> getTags) {
    return withTags(getTags.apply(this.getTags));
  }

  public NoValueTagLogger withTags(@Nullable Function<Tags, ? extends ToTags> getTags) {
    if (getTags == this.getTags) {
      return this;
    }
    return new NoValueTagLogger(level, getTags);
  }

  public NoValueTagLogger with(TagLogger.Level level, Function<Tags, ? extends ToTags> getTags) {
    if (level == this.level && getTags == this.getTags) {
      return this;
    }
    return new NoValueTagLogger(level, getTags);
  }
}
