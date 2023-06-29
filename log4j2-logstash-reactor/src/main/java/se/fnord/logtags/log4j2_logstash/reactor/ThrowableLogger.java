package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.MessageSupplier;
import reactor.core.publisher.Signal;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import java.util.function.Consumer;
import java.util.function.Function;

class ThrowableLogger implements Consumer<Signal<?>> {
  private final Logger logger;
  private final Level level;
  private final Function<Signal<?>, Tags> logTags;

  ThrowableLogger(Logger logger, Level level, Function<Signal<?>, Tags> logTags) {
    this.logger = logger;
    this.level = level;
    this.logTags = logTags;
  }

  private MessageSupplier messageSupplier(Signal<?> signal) {
    return () -> new TaggedMessage(logTags.apply(signal), signal.getThrowable());
  }

  @Override
  public void accept(Signal<?> signal) {
    logger.log(level, messageSupplier(signal));
  }
}
