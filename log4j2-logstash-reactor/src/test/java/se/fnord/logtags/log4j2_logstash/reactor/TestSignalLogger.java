package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.tags.TagLogger.Level;
import se.fnord.logtags.tags.Tags;

import java.io.Serial;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static se.fnord.logtags.log4j2_logstash.reactor.TaggedMessageMatcher.*;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestSignalLogger {

  @Test
  public void testDefaultLoggerIgnoresNext(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .build();

    signalLogger.accept(Signal.next("hello"));

    verifyNoInteractions(logger);
  }

  @Test
  public void testDefaultLoggerLogsErrors(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new IllegalStateException("error")));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verify(logger)
        .log(eq(org.apache.logging.log4j.Level.ERROR), argThat(
        errorMessage(IllegalStateException.class, tag("message", "java.lang.IllegalStateException: error"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextLoggerLogsNext(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .onError(Level.ERROR, b -> b.add("exception", Throwable::getMessage).add("message", "error"))
        .build();

    when(logger.isEnabled(org.apache.logging.log4j.Level.INFO))
        .thenReturn(true);

    signalLogger.accept(Signal.next("hello"));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.INFO));

    verify(logger).log(eq(org.apache.logging.log4j.Level.INFO), argThat(
        valueMessage(tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextLoggerLogsError(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR)).thenReturn(true);

    signalLogger.accept(Signal.error(new IllegalStateException("error")));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verify(logger)
        .log(
            eq(org.apache.logging.log4j.Level.ERROR),
            argThat(errorMessage(IllegalStateException.class, tag("message", "java.lang.IllegalStateException: error")))
        );

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testContextExtractionOnError(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .withContextTags(c -> Tags.of("fromContext", c.get("contextKey")))
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR)).thenReturn(true);

    signalLogger.accept(Signal.error(new IllegalStateException("error"), Context.of("contextKey", "contextValue")));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verify(logger).log(
        eq(org.apache.logging.log4j.Level.ERROR),
        argThat(errorMessage(IllegalStateException.class,
            tag("fromContext", "contextValue"),
            tag("message", "java.lang.IllegalStateException: error"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testContextExtractionOnNext(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .withContextTags(c -> Tags.of("fromContext", c.get("contextKey")))
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.INFO))
        .thenReturn(true);

    signalLogger.accept(Signal.next("hello", Context.of("contextKey", "contextValue")));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.INFO));

    verify(logger).log(eq(org.apache.logging.log4j.Level.INFO), argThat(
        valueMessage(tag("fromContext", "contextValue"), tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextFilterLogsOnFilterTrue(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, s -> true, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.INFO)).thenReturn(true);

    signalLogger.accept(Signal.next("hello"));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.INFO));
    verify(logger).log(
        eq(org.apache.logging.log4j.Level.INFO),
        argThat(valueMessage(tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextFilterSkipsOnFilterFalse(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, s -> false, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.INFO))
        .thenReturn(true);

    signalLogger.accept(Signal.next("hello"));
    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.INFO));

    verifyNoMoreInteractions(logger);
  }

  static class LoggedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1;

    LoggedException() {
      super("log me!");
    }
  }

  static class UnloggedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1;

    UnloggedException() {
      super("skip me!");
    }
  }

  @Test
  public void testOnErrorFilterSkipsOnFilterFalse(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .onError(Level.ERROR, t -> t.when(LoggedException.class).add("message", Throwable::getMessage))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new UnloggedException()));
    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnErrorFilterSkipsOnFilterFalseButLogsFallback(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .onError(Level.ERROR, t -> t.when(LoggedException.class).add("message", Throwable::getMessage)
            .orElse().add("message", "fallback"))
        .build();

    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new UnloggedException()));

    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verify(logger)
        .log(
            eq(org.apache.logging.log4j.Level.ERROR),
            argThat(errorMessage(UnloggedException.class, tag("message", "fallback"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnErrorFilterLogsOnFilterTrue(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .onError(Level.ERROR, t -> t.when(LoggedException.class).add("message", Throwable::getMessage))
        .build();
    when(logger.isEnabled(org.apache.logging.log4j.Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new LoggedException()));
    verify(logger)
        .isEnabled(eq(org.apache.logging.log4j.Level.ERROR));

    verify(logger).log(eq(org.apache.logging.log4j.Level.ERROR), argThat(
        errorMessage(LoggedException.class, tag("message", "log me!"))));

    verifyNoMoreInteractions(logger);
  }
}
