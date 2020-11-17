package se.fnord.logtags.log4j2_logstash.reactor;

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
import se.fnord.logtags.tags.TagsUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.fnord.logtags.tags.TagsUtil.collectTags;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestSimpleLogger {
  private static final Context TEST_CONTEXT = Context.of(Tags.class, Tags.of("test", "tag"));
  private @Mock Logger logger;

  static ArgumentMatcher<TaggedMessage> errorMessage(Class<? extends Throwable> t, TagsUtil.Tag... tags) {
    return message -> {
      var isExpectedExceptionType = t.isInstance(message.getThrowable());
      var tagsAreEqual = collectTags(message.getTags()).equals(List.of(tags));

      return isExpectedExceptionType && tagsAreEqual;
    };
  }

  static ArgumentMatcher<TaggedMessage> valueMessage(TagsUtil.Tag... tags) {
    return message -> {
      var messageTags = collectTags(message.getTags());
      var tagsAreEqual = messageTags.equals(List.of(tags));

      return message.getThrowable() == null && tagsAreEqual;
    };
  }


  private void verifyLoggerOnNext(Consumer<Signal<?>> signalLogger, Level expectedLevel, ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.next(
            "world",
            TEST_CONTEXT));
    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));

    verify(logger).isEnabled(expectedLevel);
    verify(logger).log(eq(expectedLevel), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnNext(Consumer<Signal<?>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.next(
            "world",
            TEST_CONTEXT));
    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));

    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerOnError(Consumer<Signal<?>> signalLogger, Level expectedLevel, ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.error(
            new IllegalStateException("error"),
            TEST_CONTEXT));
    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));

    verify(logger).isEnabled(expectedLevel);
    verify(logger).log(eq(expectedLevel), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnError(Consumer<Signal<?>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.error(
            new IllegalStateException("error"),
            TEST_CONTEXT));
    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));

    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerNotLoggingOnEmpty(Consumer<Signal<?>> signalLogger) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));
    verifyNoMoreInteractions(logger);
  }

  private void verifyLoggerOnEmpty(Consumer<Signal<?>> signalLogger, Level expectedLevel, ArgumentMatcher<TaggedMessage> expectedMessage) {
    reset(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    signalLogger
        .accept(Signal.complete(TEST_CONTEXT));

    verify(logger).isEnabled(expectedLevel);
    verify(logger).log(eq(expectedLevel), argThat(expectedMessage));
    verifyNoMoreInteractions(logger);
  }

  private void verifyHelloWorldMessageLogger(Consumer<Signal<?>> signalLogger, Level expectedLevel) {
    verifyLoggerOnNext(signalLogger,
        expectedLevel, valueMessage(
            tag("test", "tag"),
            tag("message", "hello world")));

    verifyLoggerOnError(signalLogger,
        Level.ERROR, errorMessage(IllegalStateException.class,
            tag("test", "tag"),
            tag("message", "java.lang.IllegalStateException: error")));

    verifyLoggerNotLoggingOnEmpty(signalLogger);
  }

  private void verifyHelloWorldMessageOnEmptyLogger(Supplier<Consumer<Signal<?>>> signalLogger, Level expectedLevel) {
    verifyLoggerNotLoggingOnNext(signalLogger.get());

    verifyLoggerNotLoggingOnError(signalLogger.get());

    verifyLoggerOnEmpty(signalLogger.get(),
        expectedLevel, valueMessage(
            tag("test", "tag"),
            tag("message", "hello world")));
  }

  @Test
  public void testSimpleLoggerErrorString() {
    var l = SimpleLogger.forLogger(logger)
        .error("hello world");

    verifyHelloWorldMessageLogger(l, Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorStringFunction() {
    var l = SimpleLogger.forLogger(logger)
        .error(v -> "hello " + v);

    verifyHelloWorldMessageLogger(l, Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorTags() {
    var l = SimpleLogger.forLogger(logger)
        .errorTags(Tags.of("message", "hello world"));

    verifyHelloWorldMessageLogger(l, Level.ERROR);
  }

  @Test
  public void testSimpleLoggerErrorTagsFunction() {
    var l = SimpleLogger.forLogger(logger)
        .errorTags(v -> Tags.of("message", "hello " + v));

    verifyHelloWorldMessageLogger(l, Level.ERROR);
  }


  @Test
  public void testSimpleLoggerWarnString() {
    var l = SimpleLogger.forLogger(logger)
        .warn("hello world");

    verifyHelloWorldMessageLogger(l, Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnStringFunction() {
    var l = SimpleLogger.forLogger(logger)
        .warn(v -> "hello " + v);

    verifyHelloWorldMessageLogger(l, Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnTags() {
    var l = SimpleLogger.forLogger(logger)
        .warnTags(Tags.of("message", "hello world"));

    verifyHelloWorldMessageLogger(l, Level.WARN);
  }

  @Test
  public void testSimpleLoggerWarnTagsFunction() {
    var l = SimpleLogger.forLogger(logger)
        .warnTags(v -> Tags.of("message", "hello " + v));

    verifyHelloWorldMessageLogger(l, Level.WARN);
  }

  
  @Test
  public void testSimpleLoggerInfoString() {
    var l = SimpleLogger.forLogger(logger)
        .info("hello world");

    verifyHelloWorldMessageLogger(l, Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoStringFunction() {
    var l = SimpleLogger.forLogger(logger)
        .info(v -> "hello " + v);

    verifyHelloWorldMessageLogger(l, Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoTags() {
    var l = SimpleLogger.forLogger(logger)
        .infoTags(Tags.of("message", "hello world"));

    verifyHelloWorldMessageLogger(l, Level.INFO);
  }

  @Test
  public void testSimpleLoggerInfoTagsFunction() {
    var l = SimpleLogger.forLogger(logger)
        .infoTags(v -> Tags.of("message", "hello " + v));

    verifyHelloWorldMessageLogger(l, Level.INFO);
  }


  @Test
  public void testSimpleLoggerDebugString() {
    var l = SimpleLogger.forLogger(logger)
        .debug("hello world");

    verifyHelloWorldMessageLogger(l, Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugStringFunction() {
    var l = SimpleLogger.forLogger(logger)
        .debug(v -> "hello " + v);

    verifyHelloWorldMessageLogger(l, Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugTags() {
    var l = SimpleLogger.forLogger(logger)
        .debugTags(Tags.of("message", "hello world"));

    verifyHelloWorldMessageLogger(l, Level.DEBUG);
  }

  @Test
  public void testSimpleLoggerDebugTagsFunction() {
    var l = SimpleLogger.forLogger(logger)
        .debugTags(v -> Tags.of("message", "hello " + v));

    verifyHelloWorldMessageLogger(l, Level.DEBUG);
  }
  

  @Test
  public void testSimpleLoggerTraceString() {
    var l = SimpleLogger.forLogger(logger)
        .trace("hello world");

    verifyHelloWorldMessageLogger(l, Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceStringFunction() {
    var l = SimpleLogger.forLogger(logger)
        .trace(v -> "hello " + v);

    verifyHelloWorldMessageLogger(l, Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceTags() {
    var l = SimpleLogger.forLogger(logger)
        .traceTags(Tags.of("message", "hello world"));

    verifyHelloWorldMessageLogger(l, Level.TRACE);
  }

  @Test
  public void testSimpleLoggerTraceTagsFunction() {
    var l = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageLogger(l.traceTags(v -> Tags.of("message", "hello " + v)), Level.TRACE);
  }

  @Test
  public void testSimpleLoggerOnEmptyTagsFunction() {
    var simpleLogger = SimpleLogger.forLogger(logger);

    verifyHelloWorldMessageOnEmptyLogger(
        () -> simpleLogger.logOnEmptyTags(Level.TRACE, () -> Tags.of("message", "hello world")),
        Level.TRACE);
  }

}
