package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Logger;
import se.fnord.logtags.log4j2_logstash.taggedmessage.Log4jTagLogger;
import se.fnord.logtags.tags.TagLogger;

public class SignalLoggerBuilder {

  public static <T> SignalLogger<T> forLogger(Logger logger) {
    return SignalLogger.<T>forLogger(Log4jTagLogger.forLogger(logger))
        .onError(TagLogger.Level.ERROR, ValueTagsBuilder::valueToString);
  }
}
