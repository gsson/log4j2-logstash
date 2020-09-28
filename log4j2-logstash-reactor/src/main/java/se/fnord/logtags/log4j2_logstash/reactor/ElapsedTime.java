package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.Tags;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public class ElapsedTime {
  private final LongSupplier nanoTime;
  private final long startNanoTime;

  ElapsedTime(LongSupplier nanoTime, long startNanoTime) {
    this.nanoTime = nanoTime;
    this.startNanoTime = startNanoTime;
  }

  static ElapsedTime create(LongSupplier nanoTime) {
    return new ElapsedTime(nanoTime, nanoTime.getAsLong());
  }

  public long getElapsedMillis() {
    return TimeUnit.NANOSECONDS.toMillis(nanoTime.getAsLong() - startNanoTime);
  }

  public static Context addToContext(Context context) {
    return context.put(ElapsedTime.class, create(System::nanoTime));
  }

  public static Tags tagsFromContext(Context context) {
    return context.<ElapsedTime>getOrEmpty(ElapsedTime.class)
        .map(t -> Tags.of("elapsed_time", t.getElapsedMillis()))
        .orElse(Tags.empty());
  }
}
