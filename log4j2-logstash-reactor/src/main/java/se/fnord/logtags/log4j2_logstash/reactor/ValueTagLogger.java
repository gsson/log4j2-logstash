package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.TagLogger.Level;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public record ValueTagLogger<T>(Level level, @Nullable BiFunction<Tags, ? super T, ? extends ToTags> getTags) {
  private boolean isEnabled(TagLogger logger) {
    return getTags != null && logger.isEnabled(level);
  }

  private void log(TagLogger logger, Tags contextTags, T value, @Nullable Throwable throwable) {
    var toTags = ValueTagOps.apply(getTags, contextTags, value);
    var tags = toTags == null ? null : toTags.toTags();
    if (tags != null) {
      logger.log(level, tags, throwable);
    }
  }

  public void handle(TagLogger logger, Tags contextTags, T value, @Nullable Throwable throwable) {
    if (isEnabled(logger)) {
      log(logger, contextTags, value, throwable);
    }
  }

  public <S> void handleSignal(TagLogger logger, Signal<? extends S> signal, Function<ContextView, Tags> tagsFromContext,
      Function<? super Signal<? extends S>, T> getValue) {
    if (isEnabled(logger)) {
      var value = getValue.apply(signal);
      var contextTags = TagOps.tagsFromContext(tagsFromContext, signal.getContextView());
      log(logger, contextTags, value, signal.getThrowable());
    }
  }

  public ValueTagLogger<T> withLevel(Level level) {
    if (level == this.level) {
      return this;
    }
    return new ValueTagLogger<>(level, getTags);
  }

  private static <U> BiFunction<Tags, ? super U, ? extends ToTags> buildTagsFunction(Function<ValueTagsBuilder<U>, ValueTagsBuilder<U>> t) {
    return t.apply(ValueTagsBuilder.create()).build();
  }

  public <U> ValueTagLogger<U> withTags(Function<ValueTagsBuilder<U>, ValueTagsBuilder<U>> getTags) {
    return withTags(buildTagsFunction(getTags));
  }

  @SuppressWarnings("unchecked")
  public <U> ValueTagLogger<U> withTags(@Nullable BiFunction<Tags, ? super U, ? extends ToTags> getTags) {
    if (getTags == this.getTags) {
      return (ValueTagLogger<U>) this;
    }

    return new ValueTagLogger<>(level, getTags);
  }

  @SuppressWarnings("unchecked")
  public <U> ValueTagLogger<U> with(Level level, Function<ValueTagsBuilder<U>, ValueTagsBuilder<U>> getTags) {
    return with(level, buildTagsFunction(getTags));
  }

  @SuppressWarnings("unchecked")
  public <U> ValueTagLogger<U> with(Level level, @Nullable BiFunction<Tags, ? super U, ? extends ToTags> getTags) {
    if (level == this.level && getTags == this.getTags) {
      return (ValueTagLogger<U>) this;
    }

    return new ValueTagLogger<>(level, getTags);
  }
}
