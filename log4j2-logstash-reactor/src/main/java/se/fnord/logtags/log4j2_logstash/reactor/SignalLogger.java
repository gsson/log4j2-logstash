package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static se.fnord.logtags.log4j2_logstash.reactor.ValueTagOps.when;

public class SignalLogger<T> implements Consumer<Signal<? extends T>> {
  private final TagLogger logger;
  private final Function<ContextView, Tags> tagsFromContext;
  private final ValueTagLogger<Throwable> onError;
  private final ValueTagLogger<T> onNext;
  private final NoValueTagLogger onComplete;

  private SignalLogger(TagLogger logger, Function<ContextView, Tags> tagsFromContext,
      ValueTagLogger<Throwable> onError,
      ValueTagLogger<T> onNext,
      NoValueTagLogger onComplete
  ) {
    this.logger = logger;
    this.tagsFromContext = tagsFromContext;
    this.onError = onError;
    this.onNext = onNext;
    this.onComplete = onComplete;
  }

  public void accept(Signal<? extends T> signal) {
    switch (signal.getType()) {
    case ON_NEXT -> onNext.handleSignal(logger, signal, tagsFromContext, Signal::get);
    case ON_ERROR -> onError.handleSignal(logger, signal, tagsFromContext, Signal::getThrowable);
    case ON_COMPLETE -> onComplete.handleSignal(logger, signal, tagsFromContext);
    default -> {}
    }
  }


  public static <T> SignalLogger<T> forLogger(TagLogger logger) {
    return new SignalLogger<>(logger, ContextTags::tagsFromContext,
        new ValueTagLogger<>(TagLogger.Level.ERROR, null),
        new ValueTagLogger<>(TagLogger.Level.INFO, null),
        new NoValueTagLogger(TagLogger.Level.INFO, null));
  }

  public SignalLogger<T> withContextTags(Function<ContextView, Tags> tagsFromContext) {
    return new SignalLogger<>(logger, tagsFromContext, onError, onNext, onComplete);
  }

  public Consumer<Signal<? extends T>> build() {
    return this;
  }

  public <U> SignalLogger<U> onNext(TagLogger.Level level, Function<ValueTagsBuilder<U>, ValueTagsBuilder<U>> onNext) {
    return this.withOnNext(this.onNext.with(level, onNext));
  }

  public <U> SignalLogger<U> onNext(TagLogger.Level level, @Nullable BiFunction<Tags, ? super U, ? extends ToTags> onNext) {
    return this.withOnNext(this.onNext.with(level, onNext));
  }

  public <U> SignalLogger<U> onNext(TagLogger.Level level, Predicate<? super U> predicate, BiFunction<Tags, ? super U, ? extends ToTags> onNext) {
    return this.withOnNext(this.onNext.with(level, when(predicate, ValueTagsFunction.from(onNext))));
  }

  @SuppressWarnings("unchecked")
  private <U> SignalLogger<U> withOnNext(ValueTagLogger<U> onNext) {
    if (this.onNext == onNext) {
      return (SignalLogger<U>) this;
    }
    return new SignalLogger<>(logger, tagsFromContext, onError, onNext, onComplete);
  }

  private SignalLogger<T> withOnComplete(NoValueTagLogger onComplete) {
    if (this.onComplete == onComplete) {
      return this;
    }

    return new SignalLogger<>(logger, tagsFromContext, onError, onNext, onComplete);
  }

  public SignalLogger<T> onComplete(TagLogger.Level level, Function<Tags, ? extends ToTags> onComplete) {
    return withOnComplete(this.onComplete.with(level, onComplete));
  }

  private SignalLogger<T> withOnError(ValueTagLogger<Throwable> onError) {
    if (this.onError == onError) {
      return this;
    }
    return new SignalLogger<>(logger, tagsFromContext, onError, onNext, onComplete);
  }

  public SignalLogger<T> onError(TagLogger.Level onErrorLevel, Function<ValueTagsBuilder<Throwable>, ValueTagsBuilder<Throwable>> onError) {
    var tags = onError.apply(ValueTagsBuilder.create()).build();

    return this.withOnError(this.onError.with(onErrorLevel, tags));
  }

  public SignalLogger<T> onError(TagLogger.Level onErrorLevel, BiFunction<Tags, ? super Throwable, ? extends ToTags> onError) {
    return this.withOnError(this.onError.with(onErrorLevel, onError));
  }

  public SignalLogger<T> onError(TagLogger.Level onErrorLevel, Predicate<? super Throwable> predicate, BiFunction<Tags, ? super Throwable, ? extends ToTags> onError) {
    return this.withOnError(this.onError.with(onErrorLevel, when(predicate, ValueTagsFunction.from(onError))));
  }
}


