package se.fnord.logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.taggedmessage.Tags;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SignalLoggerBuilder {
  public interface OnNextLoggerBuilder<T> {
    OnNextLoggerBuilder<T> withContextTags(Function<Context, Tags> contextTags);

    <U> OnNextLoggerBuilder<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext);
    <U> OnNextLoggerBuilder<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter, BiFunction<Tags, U, Tags> logOnNext);

    OnNextLoggerBuilder<T> onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError);

    Consumer<Signal<? extends T>> build();
  }

  public interface OnErrorLoggerBuilder {
    OnErrorLoggerBuilder withContextTags(Function<Context, Tags> contextTags);

    <U> OnNextLoggerBuilder<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext);
    <U> OnNextLoggerBuilder<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter, BiFunction<Tags, U, Tags> logOnNext);

    OnErrorLoggerBuilder onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError);

    Consumer<Signal<?>> build();
  }

  public static OnErrorLoggerBuilder forLogger(Logger logger) {
    return new OnErrorLoggerBuilderImpl(logger,
        c -> Tags.empty(), Level.ERROR, TagDecorators.errorMessage());
  }
}

class OnErrorLoggerBuilderImpl implements SignalLoggerBuilder.OnErrorLoggerBuilder {
  private final Logger logger;
  private final Function<Context, Tags> contextTags;
  private final Level onErrorLevel;
  private final BiFunction<Tags, Throwable, Tags> logOnError;

  OnErrorLoggerBuilderImpl(Logger logger,
      Function<Context, Tags> contextTags,
      Level onErrorLevel,
      BiFunction<Tags, Throwable, Tags> logOnError) {
    this.logger = logger;
    this.contextTags = contextTags;
    this.onErrorLevel = onErrorLevel;
    this.logOnError = logOnError;
  }

  @Override
  public OnErrorLoggerBuilderImpl withContextTags(Function<Context, Tags> contextTags) {
    return new OnErrorLoggerBuilderImpl(logger, contextTags, onErrorLevel, logOnError);
  }

  @Override
  public <U> OnNextLoggerBuilderImpl<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext) {
    return new OnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, level, logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter,
      BiFunction<Tags, U, Tags> logOnNext) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, level, onNextFilter, logOnNext);
  }

  @Override
  public OnErrorLoggerBuilderImpl onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError) {
    return new OnErrorLoggerBuilderImpl(logger, contextTags, onErrorLevel, logOnError);
  }

  @Override
  public Consumer<Signal<?>> build() {
    var tags = new SignalLogTags<>(contextTags, (t, v) -> t, logOnError);
    var throwableLogger = new ThrowableLogger(logger, onErrorLevel, tags);
    return new OnErrorSignalLogger(throwableLogger);
  }
}

class OnNextLoggerBuilderImpl<T> implements SignalLoggerBuilder.OnNextLoggerBuilder<T> {
  private final Logger logger;
  private final Function<Context, Tags> contextTags;
  private final Level onErrorLevel;
  private final BiFunction<Tags, Throwable, Tags> logOnError;
  private final Level onNextLevel;
  private final BiFunction<Tags, T, Tags> logOnNext;

  OnNextLoggerBuilderImpl(
      Logger logger,
      Function<Context, Tags> contextTags,
      Level onErrorLevel, BiFunction<Tags, Throwable, Tags> logOnError,
      Level onNextLevel,
      BiFunction<Tags, T, Tags> logOnNext) {
    this.logger = logger;
    this.contextTags = contextTags;
    this.onErrorLevel = onErrorLevel;
    this.logOnError = logOnError;
    this.onNextLevel = onNextLevel;
    this.logOnNext = logOnNext;
  }

  @Override
  public SignalLoggerBuilder.OnNextLoggerBuilder<T> withContextTags(Function<Context, Tags> contextTags) {
    return new OnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, logOnNext);
  }

  @Override
  public <U> OnNextLoggerBuilderImpl<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext) {
    return new OnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter,
      BiFunction<Tags, U, Tags> logOnNext) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public OnNextLoggerBuilderImpl<T> onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError) {
    return new OnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, logOnNext);
  }

  @Override
  public Consumer<Signal<? extends T>> build() {
    var tags = new SignalLogTags<>(contextTags, logOnNext, logOnError);
    var throwableLogger = new ThrowableLogger(logger, onErrorLevel, tags);
    var valueLogger = new ValueLogger<>(logger, onNextLevel, tags);
    return new OnNextSignalLogger<>(valueLogger, throwableLogger);
  }
}

class FilteredOnNextLoggerBuilderImpl<T> implements SignalLoggerBuilder.OnNextLoggerBuilder<T> {
  private final Logger logger;
  private final Function<Context, Tags> contextTags;
  private final Level onErrorLevel;
  private final BiFunction<Tags, Throwable, Tags> logOnError;
  private final Level onNextLevel;
  private final Predicate<Signal<? extends T>> onNextFilter;
  private final BiFunction<Tags, T, Tags> logOnNext;

  FilteredOnNextLoggerBuilderImpl(
      Logger logger,
      Function<Context, Tags> contextTags,
      Level onErrorLevel, BiFunction<Tags, Throwable, Tags> logOnError,
      Level onNextLevel, Predicate<Signal<? extends T>> onNextFilter,
      BiFunction<Tags, T, Tags> logOnNext) {
    this.logger = logger;
    this.contextTags = contextTags;
    this.onErrorLevel = onErrorLevel;
    this.logOnError = logOnError;
    this.onNextLevel = onNextLevel;
    this.onNextFilter = onNextFilter;
    this.logOnNext = logOnNext;
  }

  @Override
  public FilteredOnNextLoggerBuilderImpl<T> withContextTags(Function<Context, Tags> contextTags) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public <U> OnNextLoggerBuilderImpl<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext) {
    return new OnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter,
      BiFunction<Tags, U, Tags> logOnNext) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public FilteredOnNextLoggerBuilderImpl<T> onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, logOnError, onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public Consumer<Signal<? extends T>> build() {
    var tags = new SignalLogTags<>(contextTags, logOnNext, logOnError);
    var throwableLogger = new ThrowableLogger(logger, onErrorLevel, tags);
    var valueLogger = new FilteredValueLogger<>(logger, onNextLevel, tags, onNextFilter);
    return new OnNextSignalLogger<>(valueLogger, throwableLogger);
  }
}
