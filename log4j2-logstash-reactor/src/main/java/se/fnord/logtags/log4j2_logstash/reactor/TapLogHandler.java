package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.Tags;

import java.util.Objects;
import java.util.function.Function;

public record TapLogHandler<T>(TagLogger logger, ValueTagLogger<T> onNext, ValueTagLogger<Throwable> onError, NoValueTagLogger onComplete, NoValueTagLogger onCancel) {
  public static <T> TapLogHandler<T> forLogger(TagLogger logger) {
    return new TapLogHandler<>(logger,
        new ValueTagLogger<>(TagLogger.Level.INFO, null),
        new ValueTagLogger<>(TagLogger.Level.ERROR, null),
        new NoValueTagLogger(TagLogger.Level.ERROR, null),
        new NoValueTagLogger(TagLogger.Level.INFO, null));
  }

  <U> TapLogHandler<U> updateOnNext(Function<ValueTagLogger<T>, ValueTagLogger<U>> update) {
    return this.withOnNext(update.apply(onNext));
  }

  @SuppressWarnings("unchecked")
  <U> TapLogHandler<U> withOnNext(ValueTagLogger<U> onNext) {
    if (Objects.equals(this.onNext, onNext)) {
      return (TapLogHandler<U>) this;
    }
    return new TapLogHandler<>(logger, onNext, onError, onComplete, onCancel);
  }

  TapLogHandler<T> updateOnError(Function<ValueTagLogger<Throwable>, ValueTagLogger<Throwable>> update) {
    return withOnError(update.apply(onError));
  }

  TapLogHandler<T> withOnError(ValueTagLogger<Throwable> onError) {
    if (Objects.equals(this.onError, onError)) {
      return this;
    }
    return new TapLogHandler<>(logger, onNext, onError, onComplete, onCancel);
  }

  TapLogHandler<T> updateOnComplete(Function<NoValueTagLogger, NoValueTagLogger> update) {
    return withOnComplete(update.apply(onComplete));
  }

  TapLogHandler<T> withOnComplete(NoValueTagLogger onComplete) {
    if (Objects.equals(this.onComplete, onComplete)) {
      return this;
    }
    return new TapLogHandler<>(logger, onNext, onError, onComplete, onCancel);
  }

  TapLogHandler<T> updateOnCancel(Function<NoValueTagLogger, NoValueTagLogger> update) {
    return withOnCancel(update.apply(onCancel));
  }

  TapLogHandler<T> withOnCancel(NoValueTagLogger onCancel) {
    if (Objects.equals(this.onCancel, onCancel)) {
      return this;
    }
    return new TapLogHandler<>(logger, onNext, onError, onComplete, onCancel);
  }

  public void logOnNext(Tags tags, T value) {
    onNext.handle(logger, tags, value, null);
  }

  public void logOnError(Tags tags, Throwable error) {
    onError.handle(logger, tags, error, error);
  }

  public void logOnComplete(Tags tags) {
    onComplete.handle(logger, tags, null);
  }

  public void logOnCancel(Tags tags) {
    onCancel.handle(logger, tags, null);
  }
}

