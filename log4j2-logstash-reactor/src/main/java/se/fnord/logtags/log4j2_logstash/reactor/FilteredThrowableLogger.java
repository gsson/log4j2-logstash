package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import reactor.core.publisher.Signal;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class FilteredThrowableLogger implements Consumer<Signal<?>> {
  private final Logger logger;
  private final Level level;
  private final Function<Signal<?>, Tags> logTags;
  private final Predicate<Signal<?>> logFilter;


  FilteredThrowableLogger(Logger logger, Level level, Function<Signal<?>, Tags> logTags,
      Predicate<Signal<?>> logFilter) {
    this.logger = logger;
    this.level = level;
    this.logTags = logTags;
    this.logFilter = logFilter;
  }

  private Message message(Signal<?> signal) {
    return new TaggedMessage(logTags.apply(signal), signal.getThrowable());
  }

  @Override
  public void accept(Signal<?> signal) {
    if (logger.isEnabled(level) && logFilter.test(signal)) {
      logger.log(level, message(signal));
    }
  }
}
