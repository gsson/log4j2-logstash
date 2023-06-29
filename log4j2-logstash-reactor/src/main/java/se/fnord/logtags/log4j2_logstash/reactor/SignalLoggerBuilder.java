package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.Tags;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SignalLoggerBuilder {
  private static final Predicate<Signal<?>> NO_FILTER = s -> { throw new IllegalStateException(); };
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <T> Predicate<Signal<? extends T>> noFilter() {
    return (Predicate<Signal<? extends T>>) (Predicate) NO_FILTER;
  }

  static boolean isNoFilter(Predicate<?> filter) {
    return filter == NO_FILTER;
  }

  public interface OnNextLoggerBuilder<T> {
    OnNextLoggerBuilder<T> withContextTags(Function<ContextView, Tags> contextTags);

    <U> OnNextLoggerBuilder<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext);
    <U> OnNextLoggerBuilder<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter, BiFunction<Tags, U, Tags> logOnNext);

    OnNextLoggerBuilder<T> onError(Level level, Predicate<Signal<?>> onErrorFilter, BiFunction<Tags, Throwable, Tags> logOnError);
    OnNextLoggerBuilder<T> onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError);

    Consumer<Signal<? extends T>> build();
  }

  public interface OnErrorLoggerBuilder {
    OnErrorLoggerBuilder withContextTags(Function<ContextView, Tags> contextTags);

    <U> OnNextLoggerBuilder<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext);
    <U> OnNextLoggerBuilder<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter, BiFunction<Tags, U, Tags> logOnNext);

    OnErrorLoggerBuilder onError(Level level, Predicate<Signal<?>> onErrorFilter, BiFunction<Tags, Throwable, Tags> logOnError);
    OnErrorLoggerBuilder onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError);

    Consumer<Signal<?>> build();
  }

  public static OnErrorLoggerBuilder forLogger(Logger logger) {
    return new OnErrorLoggerBuilderImpl(logger,
        c -> Tags.empty(), Level.ERROR, noFilter(), TagDecorators.errorMessage());
  }
}

class OnErrorLoggerBuilderImpl implements SignalLoggerBuilder.OnErrorLoggerBuilder {
  private final Logger logger;
  private final Function<ContextView, Tags> contextTags;
  private final Level onErrorLevel;
  private final Predicate<Signal<?>> onErrorFilter;
  private final BiFunction<Tags, Throwable, Tags> logOnError;

  OnErrorLoggerBuilderImpl(Logger logger,
      Function<ContextView, Tags> contextTags,
      Level onErrorLevel,
      Predicate<Signal<?>> onErrorFilter,
      BiFunction<Tags, Throwable, Tags> logOnError) {
    this.logger = logger;
    this.contextTags = contextTags;
    this.onErrorLevel = onErrorLevel;
    this.onErrorFilter = onErrorFilter;
    this.logOnError = logOnError;
  }

  @Override
  public OnErrorLoggerBuilderImpl withContextTags(Function<ContextView, Tags> contextTags) {
    return new OnErrorLoggerBuilderImpl(logger, contextTags, onErrorLevel, onErrorFilter, logOnError);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext) {
    return onNext(level, SignalLoggerBuilder.noFilter(), logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter,
      BiFunction<Tags, U, Tags> logOnNext) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags,
        onErrorLevel, onErrorFilter, logOnError,
        level, onNextFilter, logOnNext);
  }

  @Override
  public OnErrorLoggerBuilderImpl onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError) {
    return onError(level, SignalLoggerBuilder.noFilter(), logOnError);
  }

  @Override
  public OnErrorLoggerBuilderImpl onError(Level level, Predicate<Signal<?>> onErrorFilter, BiFunction<Tags, Throwable, Tags> logOnError) {
    return new OnErrorLoggerBuilderImpl(logger, contextTags, level, onErrorFilter, logOnError);
  }

  @Override
  public Consumer<Signal<?>> build() {
    var tags = new SignalLogTags<>(contextTags, (t, v) -> t, logOnError);

    var throwableLogger = SignalLoggerBuilder.isNoFilter(onErrorFilter) ? new ThrowableLogger(logger, onErrorLevel, tags) : new FilteredThrowableLogger(logger, onErrorLevel, tags, onErrorFilter);
    return new OnErrorSignalLogger(throwableLogger);
  }
}

class FilteredOnNextLoggerBuilderImpl<T> implements SignalLoggerBuilder.OnNextLoggerBuilder<T> {
  private final Logger logger;
  private final Function<ContextView, Tags> contextTags;
  private final Level onErrorLevel;
  private final Predicate<Signal<?>> onErrorFilter;
  private final BiFunction<Tags, Throwable, Tags> logOnError;
  private final Level onNextLevel;
  private final Predicate<Signal<? extends T>> onNextFilter;
  private final BiFunction<Tags, T, Tags> logOnNext;

  FilteredOnNextLoggerBuilderImpl(
      Logger logger,
      Function<ContextView, Tags> contextTags,
      Level onErrorLevel, Predicate<Signal<?>> onErrorFilter,
      BiFunction<Tags, Throwable, Tags> logOnError,
      Level onNextLevel, Predicate<Signal<? extends T>> onNextFilter,
      BiFunction<Tags, T, Tags> logOnNext) {
    this.logger = logger;
    this.contextTags = contextTags;
    this.onErrorLevel = onErrorLevel;
    this.onErrorFilter = onErrorFilter;
    this.logOnError = logOnError;
    this.onNextLevel = onNextLevel;
    this.onNextFilter = onNextFilter;
    this.logOnNext = logOnNext;
  }

  @Override
  public FilteredOnNextLoggerBuilderImpl<T> withContextTags(Function<ContextView, Tags> contextTags) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags, onErrorLevel, onErrorFilter, logOnError, onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, BiFunction<Tags, U, Tags> logOnNext) {
    return onNext(level, SignalLoggerBuilder.noFilter(), logOnNext);
  }

  @Override
  public <U> FilteredOnNextLoggerBuilderImpl<U> onNext(Level level, Predicate<Signal<? extends U>> onNextFilter,
      BiFunction<Tags, U, Tags> logOnNext) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags,
        onErrorLevel, onErrorFilter, logOnError,
        level, onNextFilter, logOnNext);
  }

  @Override
  public FilteredOnNextLoggerBuilderImpl<T> onError(Level level, BiFunction<Tags, Throwable, Tags> logOnError) {
    return onError(level, SignalLoggerBuilder.noFilter(), logOnError);
  }

  @Override
  public FilteredOnNextLoggerBuilderImpl<T> onError(Level level, Predicate<Signal<?>> onErrorFilter, BiFunction<Tags, Throwable, Tags> logOnError) {
    return new FilteredOnNextLoggerBuilderImpl<>(logger, contextTags,
        level, onErrorFilter, logOnError,
        onNextLevel, onNextFilter, logOnNext);
  }

  @Override
  public Consumer<Signal<? extends T>> build() {
    var tags = new SignalLogTags<>(contextTags, logOnNext, logOnError);
    var throwableLogger = SignalLoggerBuilder.isNoFilter(onErrorFilter) ?
        new ThrowableLogger(logger, onErrorLevel, tags.eraseSignalType()) :
        new FilteredThrowableLogger(logger, onErrorLevel, tags.eraseSignalType(), onErrorFilter);
    var valueLogger = SignalLoggerBuilder.isNoFilter(onNextFilter) ?
        new ValueLogger<>(logger, onNextLevel, tags) :
        new FilteredValueLogger<>(logger, onNextLevel, tags, onNextFilter);

    return new OnNextSignalLogger<>(valueLogger, throwableLogger);
  }
}
