package se.fnord.logtags.log4j2_logstash.reactor.sl2;

import org.apache.logging.log4j.Level;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.reactor.BlackholeLogger;
import se.fnord.logtags.tags.Tags;

import java.util.function.Function;
import java.util.function.Supplier;

@State(Scope.Thread)
public class SimpleLogger2Benchmark {
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
    this.logger = new SimpleLogger(new BlackholeLogger(blackhole, Level.INFO), c -> c.getOrDefault(Tags.class, Tags.empty()));
  }

  @Benchmark
  public void logNothingFunction(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.DEBUG, state.onNextTagsFunction)
        .onError(Level.ERROR, state.onErrorTagsFunction)
        .onEmpty(Level.DEBUG, state.onEmptyTagsSupplier);

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNothingCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.DEBUG, s -> state.onNextTags)
        .onError(Level.ERROR, t -> state.onErrorTags)
        .onEmpty(Level.DEBUG, () -> state.onEmptyTags);

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNothingNonCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.DEBUG, s -> Tags.of("message", s))
        .onError(Level.ERROR, t -> Tags.of("message", t))
        .onEmpty(Level.DEBUG, () -> Tags.of("message", "empty"));

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextFunction(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.INFO, state.onNextTagsFunction)
        .onError(Level.ERROR, state.onErrorTagsFunction)
        .onEmpty(Level.INFO, state.onEmptyTagsSupplier);

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.INFO, state.onNextTagsFunction)
        .onError(Level.ERROR, state.onErrorTagsFunction)
        .onEmpty(Level.INFO, state.onEmptyTagsSupplier);

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }

  @Benchmark
  public void logNextOnNextNonCapturingLambdas(Blackhole blackhole, BenchmarkState state) {
    var log = logger.<String>log()
        .onNext(Level.INFO, s -> Tags.of("message", s))
        .onError(Level.ERROR, t -> Tags.of("message", t))
        .onEmpty(Level.INFO, () -> Tags.of("message", "empty"));

    log.accept(state.onNextSignal);
    log.accept(state.onCompleteSignal);
  }
}
