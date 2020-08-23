package se.fnord.logstash.reactor;

import reactor.core.publisher.Signal;

import java.util.function.Consumer;

class OnErrorSignalLogger implements Consumer<Signal<?>> {
  private final Consumer<Signal<?>> throwableLogger;

  OnErrorSignalLogger(Consumer<Signal<?>> throwableLogger) {
    this.throwableLogger = throwableLogger;
  }

  public void accept(Signal<?> signal) {
    switch (signal.getType()) {
    case ON_ERROR:
      throwableLogger.accept(signal);
      break;
    default:
      break;
    }
  }
}
