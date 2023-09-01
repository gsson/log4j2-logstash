package se.fnord.logtags.log4j2_logstash.reactor;

import org.reactivestreams.Publisher;
import reactor.core.observability.DefaultSignalListener;
import reactor.core.observability.SignalListener;
import reactor.core.observability.SignalListenerFactory;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.TagLogger.Level;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TapLogger<T> implements SignalListenerFactory<T, TapLogHandler<T>> {

  private final Function<ContextView, Tags> tagsFromContext;
  private final TapLogHandler<T> logHandler;

  public TapLogger(TagLogger logger, Function<ContextView, Tags> tagsFromContext,
      Level onErrorLevel, BiFunction<Tags, ? super Throwable, ? extends ToTags> onError,
      Level onNextLevel, BiFunction<Tags, ? super T, ? extends ToTags> onNext,
      Level onCompleteLevel, Function<Tags, ? extends ToTags> onComplete,
      Level onCancelLevel, Function<Tags, ? extends ToTags> onCancel
  ) {
    this(tagsFromContext, new TapLogHandler<>(logger,
        new ValueTagLogger<>(onNextLevel, onNext),
        new ValueTagLogger<>(onErrorLevel, onError),
        new NoValueTagLogger(onCancelLevel, onCancel),
        new NoValueTagLogger(onCompleteLevel, onComplete)));
  }

  public TapLogger(Function<ContextView, Tags> tagsFromContext,
      TapLogHandler<T> logHandler
  ) {
    this.tagsFromContext = tagsFromContext;
    this.logHandler = logHandler;
  }

  public static <T> TapLogger<T> forLogger(TagLogger logger) {
    return new TapLogger<>(ContextTags::tagsFromContext, TapLogHandler.forLogger(logger));
  }

  public TapLogger<T> withContextTags(Function<ContextView, Tags> tagsFromContext) {
    return new TapLogger<>(tagsFromContext, logHandler);
  }

  private <U> TapLogger<U> updateHandler(Function<TapLogHandler<T>, TapLogHandler<U>> update) {
    return withHandler(update.apply(logHandler));
  }

  @SuppressWarnings("unchecked")
  private <U> TapLogger<U> withHandler(TapLogHandler<U> handler) {
    if (Objects.equals(this.logHandler, handler)) {
      return (TapLogger<U>) this;
    }
    return new TapLogger<>(tagsFromContext, handler);
  }

  private <U> TapLogger<U> updateOnNext(Function<ValueTagLogger<T>, ValueTagLogger<U>> update) {
    return this.updateHandler(h -> h.updateOnNext(update));
  }

  public <U> TapLogger<U> onNext(Level level, Function<ValueTagsBuilder<U>, ValueTagsBuilder<U>> onNext) {
    return updateOnNext(n -> n.with(level, onNext));
  }

  private TapLogger<T> updateOnError(Function<ValueTagLogger<Throwable>, ValueTagLogger<Throwable>> update) {
    return updateHandler(h -> h.updateOnError(update));
  }

  public TapLogger<T> onError(Level level, Function<ValueTagsBuilder<Throwable>, ValueTagsBuilder<Throwable>> onError) {
    return updateOnError(n -> n.with(level, onError));
  }

  private TapLogger<T> updateOnComplete(Function<NoValueTagLogger, NoValueTagLogger> update) {
    return updateHandler(h -> h.updateOnComplete(update));
  }

  public TapLogger<T> onComplete(Level level, Function<Tags, ? extends ToTags> tags) {
    return updateOnComplete(n -> n.with(level, tags));
  }


  private TapLogger<T> updateOnCancel(Function<NoValueTagLogger, NoValueTagLogger> update) {
    return updateHandler(h -> h.updateOnCancel(update));
  }

  public TapLogger<T> onCancel(Level level, Function<Tags, ? extends ToTags> tags) {
    return updateOnCancel(n -> n.with(level, tags));
  }

  private static class TapLoggerSignalListener<T> extends DefaultSignalListener<T> {
    private final Tags tags;
    private final TapLogHandler<T> handler;

    private TapLoggerSignalListener(Tags tags, TapLogHandler<T> handler) {
      this.tags = tags;
      this.handler = handler;
    }

    @Override
    public void doOnCancel() {
      handler.logOnCancel(tags);
    }

    @Override
    public void doOnNext(T value) {
      handler.logOnNext(tags, value);
    }

    @Override
    public void doOnComplete() {
      handler.logOnComplete(tags);
    }

    @Override
    public void doOnError(Throwable error) {
      handler.logOnError(tags, error);
    }
  }

  @Override
  public TapLogHandler<T> initializePublisherState(Publisher<? extends T> source) {
    return logHandler;
  }

  @Override
  public SignalListener<T> createListener(
      Publisher<? extends T> source,
      ContextView listenerContext,
      TapLogHandler<T> handler) {
    var tags = TagOps.tagsFromContext(tagsFromContext, listenerContext);
    return new TapLoggerSignalListener<>(tags, handler);
  }
}
