package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import reactor.core.publisher.Signal;
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

  private <T> TaggedMessage createMessage(Signal<? extends T> signal, Function<Signal<? extends T>, Tags> tags) {
    return new TaggedMessage(withContextTags(signal, tags.apply(signal)),
        signal.hasError() ? signal.getThrowable() : null);
  }

  private TaggedMessage createMessage(Signal<?> signal, Tags tags) {
    return new TaggedMessage(withContextTags(signal, tags), signal.hasError() ? signal.getThrowable() : null);
  }

  private <T> Supplier<TaggedMessage> messageSupplier(Signal<? extends T> signal,
      Function<Signal<? extends T>, Tags> tags) {
    return () -> createMessage(signal, tags);
  }

  private static Level levelForSignal(Signal<?> signal, Level valueLevel) {
    return signal.hasError() ? Level.ERROR : valueLevel;
  }

  private static Tags messageTag(String value) {
    return Tags.of("message", value);
  }

  private static Tags errorMessageTag(@Nullable Throwable t) {
    return Tags.of("message", Objects.toString(t));
  }

  public static <T> Function<Signal<? extends T>, Tags> signalTags(Function<T, Tags> valueTags,
      Function<Throwable, Tags> errorTags) {
    return signal -> {
      if (signal.isOnNext()) {
        return valueTags.apply(signal.get());
      }
      if (signal.isOnError()) {
        return errorTags.apply(signal.getThrowable());
      }
      return Tags.empty();
    };
  }

  public static Tags signalTags(Signal<?> signal, Tags valueTags) {
    if (signal.isOnNext()) {
      return valueTags;
    }
    if (signal.isOnError()) {
      return errorMessageTag(signal.getThrowable());
    }
    return Tags.empty();
  }

  private <T> void doLog(Signal<T> signal, Level level, Function<Signal<T>, TaggedMessage> messageFromSignal) {
    var actualLevel = levelForSignal(signal, level);
    if (logger.isEnabled(actualLevel)) {
      switch (signal.getType()) {
      case ON_NEXT:
        logger.log(actualLevel, messageFromSignal.apply(signal));
        break;
      case ON_ERROR:
        logger.log(actualLevel,
            createMessage(signal, errorMessageTag(signal.getThrowable())));
        break;
      default:
        // Ignore
      }
    }
  }

  public <T> Consumer<Signal<? extends T>> logTags(Level level, Tags valueTags) {
    return signal -> doLog(signal, level, s -> createMessage(signal, signalTags(s, valueTags)));
  }

  public <T> Consumer<Signal<? extends T>> logTags(Level level, Function<T, Tags> valueTags) {
    return signal -> doLog(signal, level, s -> createMessage(signal, signalTags(s, valueTags.apply(s.get()))));
  }

  public <T> Consumer<Signal<? extends T>> log(Level level, Function<T, String> messageSupplier) {
    return signal -> doLog(signal, level, s -> createMessage(s, signalTags(s, messageTag(messageSupplier.apply(s.get())))));
  }

  public <T> Consumer<Signal<? extends T>> log(Level level, String message) {
    return signal -> doLog(signal, level, s -> createMessage(s, signalTags(s, messageTag(message))));
  }


  public <T> Consumer<Signal<? extends T>> infoTags(Function<T, Tags> valueTags) {
    return logTags(Level.INFO, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> infoTags(Tags valueTags) {
    return logTags(Level.INFO, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> info(Function<T, String> messageSupplier) {
    return log(Level.INFO, messageSupplier);
  }

  public <T> Consumer<Signal<? extends T>> info(String message) {
    return log(Level.INFO, message);
  }

  public <T> Consumer<Signal<? extends T>> debugTags(Function<T, Tags> valueTags) {
    return logTags(Level.DEBUG, valueTags);
  }

  public <T> Consumer<Signal<? extends T>> debugTags(Tags tags) {
    return logTags(Level.DEBUG, tags);
  }

  public <T> Consumer<Signal<? extends T>> debug(Function<T, String> messageSupplier) {
    return log(Level.DEBUG, messageSupplier);
  }

  public <T> Consumer<Signal<? extends T>> debug(String message) {
    return log(Level.DEBUG, message);
  }
}
