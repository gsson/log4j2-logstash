package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.MessageSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.TagsUtil;

import java.io.Serial;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.fnord.logtags.log4j2_logstash.reactor.TaggedMessageMatcher.*;
import static se.fnord.logtags.tags.TagsUtil.collectTags;
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

    signalLogger.accept(Signal.error(new IllegalStateException("error")));

    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessageSupplier(IllegalStateException.class, tag("message", "java.lang.IllegalStateException: error"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextLoggerLogsNext(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .onError(Level.ERROR, (t, ex) -> t.add("exception", ex.getMessage()).add("message", "error"))
        .build();

    signalLogger.accept(Signal.next("hello"));

    verify(logger).log(eq(Level.INFO), argThat(
        valueMessageSupplier(tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextLoggerLogsError(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();

    signalLogger.accept(Signal.error(new IllegalStateException("error")));

    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessageSupplier(IllegalStateException.class, tag("message", "java.lang.IllegalStateException: error"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testContextExtractionOnError(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .withContextTags(c -> Tags.of("fromContext", c.get("contextKey")))
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();

    signalLogger.accept(Signal.error(new IllegalStateException("error"), Context.of("contextKey", "contextValue")));

    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessageSupplier(IllegalStateException.class, tag("fromContext", "contextValue"),
            tag("message", "java.lang.IllegalStateException: error"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testContextExtractionOnNext(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .withContextTags(c -> Tags.of("fromContext", c.get("contextKey")))
        .<String>onNext(Level.INFO, (t, v) -> t.add("value", v).add("message", "message"))
        .build();

    signalLogger.accept(Signal.next("hello", Context.of("contextKey", "contextValue")));

    verify(logger).log(eq(Level.INFO), argThat(
        valueMessageSupplier(tag("fromContext", "contextValue"), tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextFilterLogsOnFilterTrue(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, s -> true, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(Level.INFO)).thenReturn(true);

    signalLogger.accept(Signal.next("hello"));

    verify(logger)
        .isEnabled(eq(Level.INFO));
    verify(logger).log(
        eq(Level.INFO),
        argThat(valueMessage(tag("value", "hello"), tag("message", "message"))));

    verifyNoMoreInteractions(logger);
  }

  @Test
  public void testOnNextFilterSkipsOnFilterFalse(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .<String>onNext(Level.INFO, s -> false, (t, v) -> t.add("value", v).add("message", "message"))
        .build();
    when(logger.isEnabled(Level.INFO))
        .thenReturn(true);

    signalLogger.accept(Signal.next("hello"));
    verify(logger)
        .isEnabled(eq(Level.INFO));

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

  private static boolean onErrorFilter(Signal<?> signal) {
    return signal.getThrowable() instanceof LoggedException;
  }

  @Test
  public void testOnErrorFilterSkipsOnFilterFalse(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .onError(Level.ERROR, TestSignalLogger::onErrorFilter, (t, ex) -> t.add("message", ex.getMessage()))
        .build();
    when(logger.isEnabled(Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new UnloggedException()));
    verify(logger)
        .isEnabled(eq(Level.ERROR));

    verifyNoMoreInteractions(logger);
  }


  @Test
  public void testOnErrorFilterLogsOnFilterTrue(@Mock Logger logger) {
    var signalLogger = SignalLoggerBuilder.forLogger(logger)
        .onError(Level.ERROR, TestSignalLogger::onErrorFilter, (t, ex) -> t.add("message", ex.getMessage()))
        .build();
    when(logger.isEnabled(Level.ERROR))
        .thenReturn(true);

    signalLogger.accept(Signal.error(new LoggedException()));
    verify(logger)
        .isEnabled(eq(Level.ERROR));

    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessage(LoggedException.class, tag("message", "log me!"))));

    verifyNoMoreInteractions(logger);
  }
}
