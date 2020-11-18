package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleLogger {
  private static final Consumer<?> NO_LOG = s -> {};

  private final Logger logger;
  private final Function<Context, Tags> tagsFromContext;
  private final LogOnError<?> logOnError = new LogOnError<>();

  SimpleLogger(Logger logger, Function<Context, Tags> tagsFromContext) {
    this.logger = logger;
    this.tagsFromContext = tagsFromContext;
  }

  public static SimpleLogger getSimpleLogger() {
    return forClass(StackLocatorUtil.getCallerClass(2));
  }

  public static SimpleLogger forLogger(Logger logger) {
    return new SimpleLogger(logger, ContextTags::tagsFromContext);
  }

  public static SimpleLogger forName(String name) {
    return forLogger(LogManager.getLogger(name));
  }

  public static SimpleLogger forClass(Class<?> clazz) {
    return forLogger(LogManager.getLogger(clazz));
  }

  public SimpleLogger withContextTags(Function<Context, Tags> tagsFromContext) {
    return new SimpleLogger(logger, tagsFromContext);
  }

  private Tags withContextTags(Signal<?> signal, Tags tags) {
    return tagsFromContext.apply(signal.getContext()).add(tags);
  }

  private static Tags messageTag(String value) {
    return Tags.of("message", value);
  }

  private static Tags errorMessageTag(@Nullable Throwable t) {
    return Tags.of("message", Objects.toString(t));
  }

  private TaggedMessage createTaggedMessage(Signal<?> signal, Tags tags) {
    return new TaggedMessage(withContextTags(signal, tags), signal.getThrowable());
  }


  @SuppressWarnings("unchecked")
  private <T> Consumer<Signal<? extends T>> onErrorLogger() {
    return (Consumer<Signal<? extends T>>) logOnError;
  }

  @SuppressWarnings("unchecked")
  private <T> Consumer<Signal<? extends T>> noOpLogger() {
    return (Consumer<Signal<? extends T>>) NO_LOG;
  }


  private class LogOnEmptyTagsSupplier<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Supplier<Tags> onEmptyTags;
    private boolean valueSeen = false;

    private LogOnEmptyTagsSupplier(Level level, Supplier<Tags> onEmptyTags) {
      this.level = level;
      this.onEmptyTags = onEmptyTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      switch (signal.getType()) {
      case ON_ERROR:
      case ON_NEXT:
        valueSeen = true;
        break;
      case ON_COMPLETE:
        if (!valueSeen) {
          logger.log(level,
              createTaggedMessage(signal, onEmptyTags.get()));
        }
        break;
      default:
        // Ignore
      }
    }
  }

  private class LogOnEmptyTags<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Tags onEmptyTags;
    private boolean valueSeen = false;

    private LogOnEmptyTags(Level level, Tags onEmptyTags) {
      this.level = level;
      this.onEmptyTags = onEmptyTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      switch (signal.getType()) {
      case ON_ERROR:
      case ON_NEXT:
        valueSeen = true;
        break;
      case ON_COMPLETE:
        if (!valueSeen) {
          logger.log(level,
              createTaggedMessage(signal, onEmptyTags));
        }
        break;
      default:
        // Ignore
      }
    }
  }

  private class LogOnNextFunctionTags<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Function<T, Tags> onNextTags;

    private LogOnNextFunctionTags(Level level, Function<T, Tags> onNextTags) {
      this.level = level;
      this.onNextTags = onNextTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      if (signal.getType() == SignalType.ON_NEXT) {
        logger.log(level,
            createTaggedMessage(signal, onNextTags.apply(signal.get())));
      }
    }
  }

  private class LogOnNextTags<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Tags onNextTags;

    private LogOnNextTags(Level level, Tags onNextTags) {
      this.level = level;
      this.onNextTags = onNextTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      if (signal.getType() == SignalType.ON_NEXT) {
        logger.log(level,
            createTaggedMessage(signal, onNextTags));
      }
    }
  }

  private class LogOnError<T> implements Consumer<Signal<? extends T>> {
    @Override
    public void accept(Signal<? extends T> signal) {
      if (signal.getType() == SignalType.ON_ERROR) {
        logger.log(Level.ERROR,
            createTaggedMessage(signal, errorMessageTag(signal.getThrowable())));
      }
    }
  }

  private class LogOnNextOrErrorFunctionTags<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Function<T, Tags> onNextTags;

    private LogOnNextOrErrorFunctionTags(Level level, Function<T, Tags> onNextTags) {
      this.level = level;
      this.onNextTags = onNextTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
        switch (signal.getType()) {
        case ON_NEXT:
          logger.log(level,
              createTaggedMessage(signal, onNextTags.apply(signal.get())));
          break;
        case ON_ERROR:
          logger.log(Level.ERROR,
              createTaggedMessage(signal, errorMessageTag(signal.getThrowable())));
          break;
        default:
          // Ignore
        }
    }
  }

  private class LogOnNextOrErrorTags<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Tags onNextTags;

    private LogOnNextOrErrorTags(Level level, Tags onNextTags) {
      this.level = level;
      this.onNextTags = onNextTags;
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      switch (signal.getType()) {
      case ON_NEXT:
        logger.log(level,
            createTaggedMessage(signal, onNextTags));
        break;
      case ON_ERROR:
        logger.log(Level.ERROR,
            createTaggedMessage(signal, errorMessageTag(signal.getThrowable())));
        break;
      default:
        // Ignore
      }
    }
  }

  public Consumer<Signal<?>> logTags(Level level, Tags valueTags) {
    var shouldLogOnNext = logger.isEnabled(level);
    var shouldLogOnError = logger.isEnabled(Level.ERROR);
    if (shouldLogOnNext && shouldLogOnError) {
      return new LogOnNextOrErrorTags<>(level, valueTags);
    } else if (shouldLogOnNext) {
      return new LogOnNextTags<>(level, valueTags);
    } else if (shouldLogOnError) {
      return onErrorLogger();
    } else {
      return noOpLogger();
    }
  }

  public <T> Consumer<Signal<? extends T>> logTags(Level level, Function<T, Tags> valueTags) {
    var shouldLogOnNext = logger.isEnabled(level);
    var shouldLogOnError = logger.isEnabled(Level.ERROR);
    if (shouldLogOnNext && shouldLogOnError) {
      return new LogOnNextOrErrorFunctionTags<>(level, valueTags);
    } else if (shouldLogOnNext) {
      return new LogOnNextFunctionTags<>(level, valueTags);
    } else if (shouldLogOnError) {
      return onErrorLogger();
    } else {
      return noOpLogger();
    }
  }

  public Consumer<Signal<?>> log(Level level, String message) {
    var shouldLogOnNext = logger.isEnabled(level);
    var shouldLogOnError = logger.isEnabled(Level.ERROR);
    if (shouldLogOnNext && shouldLogOnError) {
      return new LogOnNextOrErrorFunctionTags<>(level, v -> messageTag(message));
    } else if (shouldLogOnNext) {
      return new LogOnNextFunctionTags<>(level, v -> messageTag(message));
    } else if (shouldLogOnError) {
      return onErrorLogger();
    } else {
      return noOpLogger();
    }
  }

  public <T> Consumer<Signal<? extends T>> log(Level level, Function<T, String> messageSupplier) {
    var shouldLogOnNext = logger.isEnabled(level);
    var shouldLogOnError = logger.isEnabled(Level.ERROR);
    if (shouldLogOnNext && shouldLogOnError) {
      return new LogOnNextOrErrorFunctionTags<>(level, v -> messageTag(messageSupplier.apply(v)));
    } else if (shouldLogOnNext) {
      return new LogOnNextFunctionTags<>(level, v -> messageTag(messageSupplier.apply(v)));
    } else if (shouldLogOnError) {
      return onErrorLogger();
    } else {
      return noOpLogger();
    }
  }

  public Consumer<Signal<?>> logOnEmptyTags(Level level, Tags tags) {
    if (logger.isEnabled(level)) {
      return new LogOnEmptyTags<>(level, tags);
    } else {
      return noOpLogger();
    }
  }

  public Consumer<Signal<?>> logOnEmptyTags(Level level, Supplier<Tags> tags) {
    if (logger.isEnabled(level)) {
      return new LogOnEmptyTagsSupplier<>(level, tags);
    } else {
      return noOpLogger();
    }
  }

  public Consumer<Signal<?>> logOnEmpty(Level level, String message) {
    if (logger.isEnabled(level)) {
      return new LogOnEmptyTagsSupplier<>(level, () -> messageTag(message));
    } else {
      return noOpLogger();
    }
  }

  public Consumer<Signal<?>> logOnEmpty(Level level, Supplier<String> message) {
    if (logger.isEnabled(level)) {
      return new LogOnEmptyTagsSupplier<>(level, () -> messageTag(message.get()));
    } else {
      return noOpLogger();
    }
  }


  public Consumer<Signal<?>> errorTags(Tags valueTags) {
    return logTags(Level.ERROR, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> errorTags(Function<T, Tags> valueTags) {
    return logTags(Level.ERROR, valueTags);
  }

  public Consumer<Signal<?>> error(String message) {
    return log(Level.ERROR, message);
  }

  public <T> Consumer<Signal<? extends T>> error(Function<T, String> messageSupplier) {
    return log(Level.ERROR, messageSupplier);
  }

  public Consumer<Signal<?>> errorOnEmptyTags(Tags onEmptyTags) {
    return logOnEmptyTags(Level.ERROR, onEmptyTags);
  }

  public Consumer<Signal<?>> errorOnEmptyTags(Supplier<Tags> onEmptyTags) {
    return logOnEmptyTags(Level.ERROR, onEmptyTags);
  }

  public Consumer<Signal<?>> errorOnEmpty(String message) {
    return logOnEmpty(Level.ERROR, message);
  }

  public Consumer<Signal<?>> errorOnEmpty(Supplier<String> messageSupplier) {
    return logOnEmpty(Level.ERROR, messageSupplier);
  }

  public Consumer<Signal<?>> warnTags(Tags valueTags) {
    return logTags(Level.WARN, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> warnTags(Function<T, Tags> valueTags) {
    return logTags(Level.WARN, valueTags);
  }

  public Consumer<Signal<?>> warn(String message) {
    return log(Level.WARN, message);
  }

  public <T> Consumer<Signal<? extends T>> warn(Function<T, String> messageSupplier) {
    return log(Level.WARN, messageSupplier);
  }

  public Consumer<Signal<?>> infoTags(Tags valueTags) {
    return logTags(Level.INFO, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> infoTags(Function<T, Tags> valueTags) {
    return logTags(Level.INFO, valueTags);
  }

  public Consumer<Signal<?>> info(String message) {
    return log(Level.INFO, message);
  }

  public <T> Consumer<Signal<? extends T>> info(Function<T, String> messageSupplier) {
    return log(Level.INFO, messageSupplier);
  }

  public Consumer<Signal<?>> debugTags(Tags tags) {
    return logTags(Level.DEBUG, tags);
  }

  public <T> Consumer<Signal<? extends T>> debugTags(Function<T, Tags> valueTags) {
    return logTags(Level.DEBUG, valueTags);
  }

  public Consumer<Signal<?>> debug(String message) {
    return log(Level.DEBUG, message);
  }

  public <T> Consumer<Signal<? extends T>> debug(Function<T, String> messageSupplier) {
    return log(Level.DEBUG, messageSupplier);
  }

  public Consumer<Signal<?>> traceTags(Tags tags) {
    return logTags(Level.TRACE, tags);
  }

  public <T> Consumer<Signal<? extends T>> traceTags(Function<T, Tags> valueTags) {
    return logTags(Level.TRACE, valueTags);
  }

  public Consumer<Signal<?>> trace(String message) {
    return log(Level.TRACE, message);
  }

  public <T> Consumer<Signal<? extends T>> trace(Function<T, String> messageSupplier) {
    return log(Level.TRACE, messageSupplier);
  }
}
