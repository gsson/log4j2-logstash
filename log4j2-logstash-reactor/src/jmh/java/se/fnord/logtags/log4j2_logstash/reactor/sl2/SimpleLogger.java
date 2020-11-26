package se.fnord.logtags.log4j2_logstash.reactor.sl2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.reactor.ContextTags;
import se.fnord.logtags.tags.Tags;

import java.util.function.Function;

class SimpleLogger {
  private final ContextLogger logger;

  public static SimpleLogger getLogger() {
    return getLogger(StackLocatorUtil.getCallerClass(2));
  }

  public static SimpleLogger getLogger(Class<?> forClass) {
    return new SimpleLogger(LogManager.getLogger(forClass), ContextTags::tagsFromContext);
  }

  SimpleLogger(Logger logger, Function<Context, Tags> contextTags) {
    this.logger = new ContextLogger(logger, contextTags);
  }

  public <T> SimpleSignalLogger<T> log() {
    return new SimpleSignalLogger<T>(logger);
  }
}
