package se.fnord.logtags.log4j2_logstash.taggedmessage;

import org.apache.logging.log4j.Logger;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.Tags;

import javax.annotation.Nullable;
import java.util.Objects;

public class Log4jTagLogger implements TagLogger {
  private final Logger logger;

  private Log4jTagLogger(Logger logger) {
    this.logger = Objects.requireNonNull(logger);
  }

  public static Log4jTagLogger forLogger(Logger logger) {
    return new Log4jTagLogger(logger);
  }

  private static org.apache.logging.log4j.Level toLog4jLevel(Level level) {
    return switch (level) {
      case DEBUG -> org.apache.logging.log4j.Level.DEBUG;
      case ERROR -> org.apache.logging.log4j.Level.ERROR;
      case INFO -> org.apache.logging.log4j.Level.INFO;
      case TRACE -> org.apache.logging.log4j.Level.TRACE;
      case WARN -> org.apache.logging.log4j.Level.WARN;
    };
  }

  @Override
  public boolean isEnabled(Level level) {
    return logger.isEnabled(toLog4jLevel(level));
  }

  @Override
  public void log(Level level, Tags tags, @Nullable Throwable throwable) {
    log(level, new TaggedMessage(tags, throwable));
  }

  public void log(Level level, TaggedMessage message) {
    logger.log(toLog4jLevel(level), message);
  }
}
