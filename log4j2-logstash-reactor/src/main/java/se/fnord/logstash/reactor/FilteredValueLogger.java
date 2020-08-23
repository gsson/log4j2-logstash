package se.fnord.logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import reactor.core.publisher.Signal;
import se.fnord.taggedmessage.TaggedMessage;
import se.fnord.taggedmessage.Tags;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class FilteredValueLogger<T> implements Consumer<Signal<? extends T>> {
  private final Logger logger;
  private final Level level;
  private final Function<Signal<? extends T>, Tags> logTags;
  private final Predicate<Signal<? extends T>> logFilter;

  FilteredValueLogger(
      Logger logger,
      Level level,
      Function<Signal<? extends T>, Tags> logTags,
      Predicate<Signal<? extends T>> logFilter) {
    this.logger = logger;
    this.level = level;
    this.logTags = logTags;
    this.logFilter = logFilter;
  }

  private Message message(Signal<? extends T> signal) {
    return new TaggedMessage(logTags.apply(signal), null);
  }

  @Override
  public void accept(Signal<? extends T> signal) {
    if (logger.isEnabled(level) && logFilter.test(signal)) {
      logger.log(level, message(signal));
    }
  }
}
