package se.fnord.logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.MessageSupplier;
import reactor.core.publisher.Signal;
import se.fnord.taggedmessage.TaggedMessage;
import se.fnord.taggedmessage.Tags;

import java.util.function.Consumer;
import java.util.function.Function;

class ValueLogger<T> implements Consumer<Signal<? extends T>> {
  private final Logger logger;
  private final Level level;
  private final Function<Signal<? extends T>, Tags> logTags;

  ValueLogger(Logger logger, Level level, Function<Signal<? extends T>, Tags> logTags) {
    this.logger = logger;
    this.level = level;
    this.logTags = logTags;
  }

  private MessageSupplier messageSupplier(Signal<? extends T> signal) {
    return () -> new TaggedMessage(logTags.apply(signal), null);
  }

  @Override
  public void accept(Signal<? extends T> signal) {
    logger.log(level, messageSupplier(signal));
  }
}
