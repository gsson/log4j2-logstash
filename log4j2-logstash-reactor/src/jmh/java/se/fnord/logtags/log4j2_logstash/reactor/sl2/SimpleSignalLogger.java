package se.fnord.logtags.log4j2_logstash.reactor.sl2;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.logging.log4j.Level;
import reactor.core.publisher.Signal;
import se.fnord.logtags.tags.Tags;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleSignalLogger<T> implements Consumer<Signal<? extends T>> {
  private final ContextLogger logger;
  private Level onNextLevel = null;
  private Function<? super T, Tags> onNextTags = null;
  private Level onErrorLevel = null;
  private Function<Throwable, Tags> onErrorTags = null;
  private Level onEmptyLevel = null;
  private Supplier<Tags> onEmptyTags = null;

  private boolean valueSeen = false;

  SimpleSignalLogger(ContextLogger logger) {
    this.logger = logger;
  }

  private boolean shouldEnable(@Nullable Level level, @Nullable Object tags) {
    return level != null && level != Level.OFF && logger.isEnabled(level) && tags != null;
  }

  public SimpleSignalLogger<T> onNext(@Nullable Level level, @Nullable Function<? super T, Tags> onNextTags) {
    if (shouldEnable(level, onNextTags)) {
      this.onNextLevel = level;
      this.onNextTags = onNextTags;
    } else {
      this.onNextLevel = null;
      this.onNextTags = null;
    }
    return this;
  }

  public SimpleSignalLogger<T> onNext(@Nullable Function<? super T, Tags> onNextTags) {
    return onNext(Level.INFO, onNextTags);
  }

  public SimpleSignalLogger<T> onError(@Nullable Level level, @Nullable Function<Throwable, Tags> onErrorTags) {
    if (shouldEnable(level, onErrorTags)) {
      this.onErrorLevel = level;
      this.onErrorTags = onErrorTags;
    } else {
      this.onErrorLevel = null;
      this.onErrorTags = null;
    }
    return this;
  }

  public SimpleSignalLogger<T> onEmpty(@Nullable Level level, @Nullable Supplier<Tags> onEmptyTags) {
    if (shouldEnable(level, onEmptyTags)) {
      this.onEmptyLevel = level;
      this.onEmptyTags = onEmptyTags;
    } else {
      this.onEmptyLevel = null;
      this.onEmptyTags = null;
    }
    return this;
  }

  @Override
  public void accept(Signal<? extends T> signal) {
    switch (signal.getType()) {
    case ON_NEXT:
      if (onNextLevel != null) {
        logger.log(onNextLevel, signal.getContextView(), signal.get(), onNextTags, null);
      }
      valueSeen = true;
      break;
    case ON_ERROR:
      if (onErrorLevel != null) {
        logger.log(onErrorLevel, signal.getContextView(), signal.getThrowable(), onErrorTags, signal.getThrowable());
      }
      valueSeen = true;
      break;
    case ON_COMPLETE:
      if (!valueSeen && onEmptyLevel != null) {
        logger.log(onEmptyLevel, signal.getContextView(), onEmptyTags, null);
      }
      break;
    default:
      // Ignore other signals
      break;
    }
  }
}
