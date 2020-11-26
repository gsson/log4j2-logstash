package se.fnord.logtags.log4j2_logstash.reactor.sl1;

import org.apache.logging.log4j.Level;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.reactor.BlackholeLogger;
import se.fnord.logtags.log4j2_logstash.reactor.SimpleLogger;
import se.fnord.logtags.tags.Tags;

import java.util.function.Function;
import java.util.function.Supplier;

@State(Scope.Thread)
public class SimpleLoggerBenchmark {
  private SimpleLogger logger;

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    final Context context = Context.of(Tags.class, Tags.of("context", "context"));
    final Tags onEmptyTags = Tags.of("message", "onEmpty");
    final Tags onNextTags = Tags.of("message", "onNext");
    final Tags onErrorTags = Tags.of("message", "onError");
    final Function<String, Tags> onNextTagsFunction = s -> onNextTags;
    final Function<Throwable, Tags> onErrorTagsFunction = s -> onErrorTags;
    final Supplier<Tags> onEmptyTagsSupplier = () -> onEmptyTags;
    final Signal<String> onNextSignal = Signal.next("onNext", context);
    final Signal<String> onErrorSignal = Signal.error(new IllegalStateException(), context);
    final Signal<String> onCompleteSignal = Signal.complete();
  }

  @Setup
  public void setup(Blackhole blackhole) {
    this.logger = SimpleLogger
        .forLogger(new BlackholeLogger(blackhole, Level.INFO))
        .withContextTags(c -> c.getOrDefault(Tags.class, Tags.empty()));
  }

  @Benchmark
  public void logNothingFunction(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.DEBUG, state.onNextTagsFunction);
    var emptyLog = logger.logOnEmptyTags(Level.DEBUG, state.onEmptyTagsSupplier);

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNothingCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.DEBUG, s -> state.onNextTags);
    var emptyLog = logger.logOnEmptyTags(Level.DEBUG, () -> state.onEmptyTags);

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNothingNonCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.DEBUG, s -> Tags.of("message", s));
    var emptyLog = logger.logOnEmptyTags(Level.DEBUG, () -> Tags.of("message", "empty"));

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextFunction(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.INFO, state.onNextTagsFunction);
    var emptyLog = logger.logOnEmptyTags(Level.INFO, state.onEmptyTagsSupplier);

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.INFO, s -> state.onNextTags);
    var emptyLog = logger.logOnEmptyTags(Level.INFO, () -> state.onEmptyTags);

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextNonCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.logTags(Level.INFO, s -> state.onNextTags);
    var emptyLog = logger.logOnEmptyTags(Level.INFO, () -> state.onEmptyTags);

    log.accept(state.onNextSignal);
    emptyLog.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
    emptyLog.accept(state.onCompleteSignal);
  }
}
