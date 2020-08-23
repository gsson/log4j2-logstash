package se.fnord.logstash.reactor;

import reactor.core.publisher.Signal;

import java.util.function.Consumer;

class OnNextSignalLogger<T> implements Consumer<Signal<? extends T>> {
  private final Consumer<Signal<? extends T>> valueLogger;
  private final Consumer<Signal<?>> throwableLogger;

  OnNextSignalLogger(Consumer<Signal<? extends T>> valueLogger, Consumer<Signal<?>> throwableLogger) {
    this.valueLogger = valueLogger;
    this.throwableLogger = throwableLogger;
  }

  public void accept(Signal<? extends T> signal) {
    switch (signal.getType()) {
    case ON_NEXT:
      valueLogger.accept(signal);
      break;
    case ON_ERROR:
      throwableLogger.accept(signal);
      break;
    default:
      break;
    }
  }
}
