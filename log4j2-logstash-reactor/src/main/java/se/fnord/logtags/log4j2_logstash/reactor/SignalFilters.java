package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.core.publisher.Signal;

import java.util.function.Predicate;

public class SignalFilters {
  public static Predicate<Signal<?>> errorInstanceOf(Class<? extends Throwable> throwableType) {
    return errorFilter(throwableType::isInstance);
  }

  public static Predicate<Signal<?>> errorFilter(Predicate<? super Throwable> errorFilter) {
    return signal -> signal.hasError() && errorFilter.test(signal.getThrowable());
  }

  public static <T> Predicate<Signal<? extends T>> valueFilter(Predicate<? super T> valueFilter) {
    return signal -> signal.hasValue() && valueFilter.test(signal.get());
  }

}
