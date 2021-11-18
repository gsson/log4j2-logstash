package se.fnord.logtags.log4j2_logstash.reactor;

import javax.annotation.Nullable;

@FunctionalInterface
public interface LongObjFunction<U, R> {
  R apply(long t, @Nullable U u);
}
