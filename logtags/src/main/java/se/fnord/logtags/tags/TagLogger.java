package se.fnord.logtags.tags;

import javax.annotation.Nullable;

public interface TagLogger {
  boolean isEnabled(Level level);
  default void log(Level level, Tags tags) {
    log(level, tags, null);
  }

  void log(Level level, Tags tags, @Nullable Throwable throwable);

  enum Level {
    DEBUG,
    ERROR,
    INFO,
    TRACE,
    WARN
  }
}
