package se.fnord.logtags.log4j2_logstash.reactor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.fnord.logtags.log4j2_logstash.reactor.TestSignalLogger.valueMessage;
import static se.fnord.logtags.log4j2_logstash.reactor.TaggedMessageMatcher.errorMessage;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestSimpleLogger {
  private static final Context TEST_CONTEXT = Context.of(Tags.class, Tags.of("test", "tag"));
  @SuppressFBWarnings(value = "NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "This value is injected by Mockito")
  private @Mock
  Logger logger;

  private void verifyLoggerOnNext(Supplier<Consumer<Signal<?>>> signalLogger, Level expectedLevel,
      ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.next(
        "world",
        TEST_CONTEXT));
    l.accept(Signal.complete(TEST_CONTEXT));

    if (expectedLevel == Level.ERROR) {
      verify(logger, times(2)).isEnabled(Level.ERROR);
    } else {
      verify(logger).isEnabled(expectedLevel);
      verify(logger).isEnabled(Level.ERROR);
    }

    verify(logger).log(eq(expectedLevel), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnNext(Supplier<Consumer<Signal<?>>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.next(
        "world",
        TEST_CONTEXT));
    l.accept(Signal.complete(TEST_CONTEXT));

    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerOnError(Supplier<Consumer<Signal<?>>> signalLogger,
      ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.error(
        new IllegalStateException("error"),
        TEST_CONTEXT));
    l.accept(Signal.complete(TEST_CONTEXT));

    verify(logger, times(2)).isEnabled(any(Level.class));

    verify(logger).log(eq(Level.ERROR), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnError(Supplier<Consumer<Signal<?>>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.error(
        new IllegalStateException("error"),
        TEST_CONTEXT));
    l.accept(Signal.complete(TEST_CONTEXT));

    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnEmpty(Supplier<Consumer<Signal<?>>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.complete(TEST_CONTEXT));

    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerOnEmpty(Supplier<Consumer<Signal<?>>> signalLogger, Level expectedLevel,
      ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    var l = signalLogger.get();
    l.accept(Signal.complete(TEST_CONTEXT));

    verify(logger).isEnabled(expectedLevel);
    verify(logger).log(eq(expectedLevel), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyHelloWorldMessageLogger(Supplier<Consumer<Signal<?>>> signalLogger, Level expectedLevel) {
    verifyLoggerOnNext(signalLogger,
        expectedLevel, valueMessage(
            tag("test", "tag"),
            tag("message", "hello world")));

    verifyLoggerOnError(signalLogger,
        errorMessage(IllegalStateException.class,
            tag("test", "tag"),
            tag("message", "java.lang.IllegalStateException: error")));

    verifyLoggerNotLoggingOnEmpty(signalLogger);
  }

  private void verifyHelloWorldMessageOnEmptyLogger(Supplier<Consumer<Signal<?>>> signalLogger, Level expectedLevel) {
    verifyLoggerNotLoggingOnNext(signalLogger);

    verifyLoggerNotLoggingOnError(signalLogger);

    verifyLoggerOnEmpty(signalLogger,
        expectedLevel, valueMessage(
            tag("test", "tag"),
            tag("message", "hello world")));
  }

  @Test
  public void testSimpleLoggerErrorString() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.error("hello world"), Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorStringFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.error(v -> "hello " + v), Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorTags() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.errorTags(Tags.of("message", "hello world")), Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.errorTags(v -> Tags.of("message", "hello " + v)), Level.ERROR);
  }

  @Test
  public void testSimpleLoggerWarnString() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.warn("hello world"), Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnStringFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.warn(v -> "hello " + v), Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnTags() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.warnTags(Tags.of("message", "hello world")), Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.warnTags(v -> Tags.of("message", "hello " + v)), Level.WARN);
  }

  @Test
  public void testSimpleLoggerInfoString() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.info("hello world"), Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoStringFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.info(v -> "hello " + v), Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoTags() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.infoTags(Tags.of("message", "hello world")), Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.infoTags(v -> Tags.of("message", "hello " + v)), Level.INFO);
  }

  @Test
  public void testSimpleLoggerDebugString() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.debug("hello world"), Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugStringFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.debug(v -> "hello " + v), Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugTags() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.debugTags(Tags.of("message", "hello world")), Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.debugTags(v -> Tags.of("message", "hello " + v)), Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerTraceString() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.trace("hello world"), Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceStringFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.trace(v -> "hello " + v), Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceTags() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.traceTags(Tags.of("message", "hello world")), Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(() -> l.traceTags(v -> Tags.of("message", "hello " + v)), Level.TRACE);
  }

  @Test
  public void testSimpleLoggerOnEmptyTagsFunction() {
    var simpleLogger = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageOnEmptyLogger(
        () -> simpleLogger.logOnEmptyTags(Level.TRACE, () -> Tags.of("message", "hello world")),
        Level.TRACE);
  }

}
