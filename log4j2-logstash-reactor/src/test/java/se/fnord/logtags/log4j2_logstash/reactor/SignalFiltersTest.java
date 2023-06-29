package se.fnord.logtags.log4j2_logstash.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Signal;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SignalFiltersTest {
  @Test
  public void testErrorInstanceOf() {
    var filter = SignalFilters.errorInstanceOf(RuntimeException.class);
    // Match type and sub-types
    assertTrue(filter.test(Signal.error(new RuntimeException())));
    assertTrue(filter.test(Signal.error(new IllegalArgumentException())));

    // Don't match siblings and super-types
    assertFalse(filter.test(Signal.error(new Throwable())));
    assertFalse(filter.test(Signal.error(new IOException())));

    // Don't match Next signals (even if they contain an exception)
    assertFalse(filter.test(Signal.next(new RuntimeException())));

  }

  @Test
  public void testErrorFilter() {
    var filter = SignalFilters.errorFilter(t -> "match me!".equals(t.getMessage()));

    assertTrue(filter.test(Signal.error(new RuntimeException("match me!"))));

    assertFalse(filter.test(Signal.error(new RuntimeException("don't match me?"))));

    // Don't match Next signals (even if they contain an exception)
    assertFalse(filter.test(Signal.next(new RuntimeException("match me!"))));
  }

  @Test
  public void testValueFilter() {
    var filter = SignalFilters.valueFilter("match me!"::equals);

    assertTrue(filter.test(Signal.next("match me!")));

    assertFalse(filter.test(Signal.next("don't match me?")));

    // Don't match Error signals
    var throwableValueFilter = SignalFilters.<Throwable>valueFilter(t -> "match me!".equals(t.getMessage()));

    assertFalse(throwableValueFilter.test(Signal.error(new RuntimeException("match me!"))));
  }

}