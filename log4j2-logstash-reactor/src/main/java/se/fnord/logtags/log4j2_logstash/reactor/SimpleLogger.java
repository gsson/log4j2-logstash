package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.ContextView;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;

public class SimpleLogger {
  private static final Consumer<?> NO_LOG = s -> {};
  private static final LongFunction<Tags> NO_COUNT_SUCCESS_TAGS = n -> Tags.empty();
  private static final LongObjFunction<Throwable, Tags> NO_COUNT_ERROR_TAGS = (n, t) -> Tags.empty();

  private final Logger logger;
  private final Function<ContextView, Tags> tagsFromContext;
  private final LogOnError<?> logOnError = new LogOnError<>();

  SimpleLogger(Logger logger, Function<ContextView, Tags> tagsFromContext) {
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

  public SimpleLogger withContextTags(Function<ContextView, Tags> tagsFromContext) {
    return new SimpleLogger(logger, tagsFromContext);
  }

  private Tags withContextTags(Signal<?> signal, Tags tags) {
    return tagsFromContext.apply(signal.getContextView()).add(tags);
  }

  private static Tags messageTag(String value) {
    return Tags.of("message", value);
  }

  private static Tags errorMessageTag(@Nullable Throwable t) {
    return Tags.of("message", Objects.toString(t));
  }

  private static Tags errorMessageTag(long n, @Nullable Throwable t) {
    return Tags.of("message", "Exception after " + n + " published items: " + t);
  }

  private TaggedMessage createTaggedMessage(Signal<?> signal, Tags tags) {
    return createTaggedMessage(signal, signal.getThrowable(), tags);
  }

  private TaggedMessage createTaggedMessage(Signal<?> signal, @Nullable Throwable throwable, Tags tags) {
    return new TaggedMessage(withContextTags(signal, tags), throwable);
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

  private static boolean levelIsOff(Level level) {
    return Level.OFF.isLessSpecificThan(level);
  }

  private class LogCountFunctionTags<T> implements Consumer<Signal<? extends T>> {
    private final Level successLevel;
    private final LongFunction<Tags> successTags;
    private final Level errorLevel;
    private final LongObjFunction<Throwable, Tags> errorTags;
    private long valuesSeen = 0;

    private LogCountFunctionTags(Level successLevel, @Nullable LongFunction<Tags> successTags, Level errorLevel, @Nullable LongObjFunction<Throwable, Tags> errorTags) {
      this.successLevel = levelIsOff(successLevel) ? Level.OFF : successLevel;
      this.successTags = levelIsOff(successLevel) ? NO_COUNT_SUCCESS_TAGS : Objects.requireNonNull(successTags);
      this.errorLevel = levelIsOff(errorLevel) ? Level.OFF : errorLevel;
      this.errorTags = levelIsOff(errorLevel) ? NO_COUNT_ERROR_TAGS : Objects.requireNonNull(errorTags);
    }

    @Override
    public void accept(Signal<? extends T> signal) {
      switch (signal.getType()) {
      case ON_ERROR:
        var error = signal.getThrowable();
        if (!levelIsOff(errorLevel)) {
          logger.log(errorLevel,
              createTaggedMessage(signal, error, errorTags.apply(valuesSeen, error)));
        }

        break;
      case ON_NEXT:
        valuesSeen ++;
        break;
      case ON_COMPLETE:
        if (!levelIsOff(successLevel)) {
          logger.log(successLevel,
              createTaggedMessage(signal, successTags.apply(valuesSeen)));
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

  public Consumer<Signal<?>> logCount(Level level, String message) {
    return logCount(level, "count", message);
  }

  public Consumer<Signal<?>> logCount(Level level, String countTagName, String message) {
    return logCountTags(
        level, n -> messageTag(message).add(countTagName, n),
        Level.ERROR, (n, t) -> errorMessageTag(n, t).add(countTagName, n));
  }

  public Consumer<Signal<?>> logCount(Level level, LongFunction<String> message) {
    return logCountTags(level, n -> messageTag(message.apply(n)));
  }

  public Consumer<Signal<?>> logCountTags(Level successLevel, LongFunction<Tags> successTags) {
    return logCountTags(
        successLevel, successTags,
        Level.ERROR, SimpleLogger::errorMessageTag);
  }

  public Consumer<Signal<?>> logCountTags(Level successLevel, LongFunction<Tags> successTags, Level errorLevel, LongObjFunction<Throwable, Tags> errorTags) {
    return new LogCountFunctionTags<>(successLevel, successTags, errorLevel, errorTags);
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

  public Consumer<Signal<?>> errorCount(String countTagName, String message) {
    return logCount(Level.ERROR, countTagName, message);
  }

  public Consumer<Signal<?>> errorCount(LongFunction<String> messageSupplier) {
    return logCount(Level.ERROR, messageSupplier);
  }

  public Consumer<Signal<?>> errorCountTags(LongFunction<Tags> successTags) {
    return logCountTags(Level.ERROR, successTags);
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
  
  public Consumer<Signal<?>> infoOnEmptyTags(Tags onEmptyTags) {
    return logOnEmptyTags(Level.INFO, onEmptyTags);
  }

  public Consumer<Signal<?>> infoOnEmptyTags(Supplier<Tags> onEmptyTags) {
    return logOnEmptyTags(Level.INFO, onEmptyTags);
  }

  public Consumer<Signal<?>> infoOnEmpty(String message) {
    return logOnEmpty(Level.INFO, message);
  }

  public Consumer<Signal<?>> infoOnEmpty(Supplier<String> messageSupplier) {
    return logOnEmpty(Level.INFO, messageSupplier);
  }

  public Consumer<Signal<?>> infoCount(String countTagName, String message) {
    return logCount(Level.INFO, countTagName, message);
  }

  public Consumer<Signal<?>> infoCount(LongFunction<String> messageSupplier) {
    return logCount(Level.INFO, messageSupplier);
  }

  public Consumer<Signal<?>> infoCountTags(LongFunction<Tags> successTags) {
    return logCountTags(Level.INFO, successTags);
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
