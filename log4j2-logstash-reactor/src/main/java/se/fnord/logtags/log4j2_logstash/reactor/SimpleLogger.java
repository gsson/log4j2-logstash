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
  private final Logger logger;
  private final Function<Context, Tags> tagsFromContext;

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

  private static Level levelForSignal(Signal<?> signal, Level valueLevel) {
    return signal.getType() == SignalType.ON_ERROR ? Level.ERROR : valueLevel;
  }

  private class LogOnEmpty<T> implements Consumer<Signal<? extends T>> {
    private final Level level;
    private final Supplier<Tags> onEmptyTags;
    private boolean valueSeen = false;

    private LogOnEmpty(Level level, Supplier<Tags> onEmptyTags) {
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
        if (!valueSeen && logger.isEnabled(level)) {
          logger.log(level,
              createTaggedMessage(signal, onEmptyTags.get()));
        }
      }
    }
  }

  private <T> void doLog(Signal<T> signal, Level level, Function<T, Tags> valueTags) {
    var actualLevel = levelForSignal(signal, level);
    if (logger.isEnabled(actualLevel)) {
      switch (signal.getType()) {
      case ON_NEXT:
        logger.log(actualLevel,
            createTaggedMessage(signal, valueTags.apply(signal.get())));
        break;
      case ON_ERROR:
        logger.log(actualLevel,
            createTaggedMessage(signal, errorMessageTag(signal.getThrowable())));
        break;
      default:
        // Ignore
      }
    }
  }

  private <T> void doLog(Signal<T> signal, Level level, Tags valueTags) {
    var actualLevel = levelForSignal(signal, level);
    if (logger.isEnabled(actualLevel)) {
      switch (signal.getType()) {
      case ON_NEXT:
        logger.log(actualLevel,
            createTaggedMessage(signal, valueTags));
        break;
      case ON_ERROR:
        logger.log(actualLevel,
            createTaggedMessage(signal, errorMessageTag(signal.getThrowable())));
        break;
      default:
        // Ignore
      }
    }
  }

  public Consumer<Signal<?>> logTags(Level level, Tags valueTags) {
    return signal -> doLog(signal, level, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> logTags(Level level, Function<T, Tags> valueTags) {
    return signal -> doLog(signal, level, valueTags::apply);
  }

  public Consumer<Signal<?>> log(Level level, String message) {
    return signal -> doLog(signal, level, v -> messageTag(message));
  }

  public <T> Consumer<Signal<? extends T>> log(Level level, Function<T, String> messageSupplier) {
    return signal -> doLog(signal, level, v -> messageTag(messageSupplier.apply(v)));
  }

  public Consumer<Signal<?>> logOnEmptyTags(Level level, Tags tags) {
    return new LogOnEmpty<>(level, () -> tags);
  }

  public Consumer<Signal<?>> logOnEmptyTags(Level level, Supplier<Tags> tags) {
    return new LogOnEmpty<>(level, tags);
  }

  public Consumer<Signal<?>> logOnEmpty(Level level, String message) {
    return new LogOnEmpty<>(level, () -> messageTag(message));
  }

  public Consumer<Signal<?>> logOnEmpty(Level level, Supplier<String> message) {
    return new LogOnEmpty<>(level, () -> messageTag(message.get()));
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
