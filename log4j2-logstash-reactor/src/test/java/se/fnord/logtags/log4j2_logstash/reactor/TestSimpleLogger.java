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
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.fnord.logtags.tags.TagsUtil.collectTags;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestSimpleLogger {
  private static final Context TEST_CONTEXT = Context.of(Tags.class, Tags.of("test", "tag"));

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

  @Test
  public void testSimpleLoggerInfoString(@Mock Logger logger) {
    var simpleLogger = SimpleLogger.forLogger(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    simpleLogger.info("hello")
        .accept(Signal.next(
            "value",
            TEST_CONTEXT));

    verify(logger).isEnabled(Level.INFO);
    verify(logger).log(eq(Level.INFO), argThat(
        valueMessage(tag("test", "tag"), tag("message", "hello"))));

  }

  @Test
  public void testSimpleLoggerInfoStringError(@Mock Logger logger) {
    var simpleLogger = SimpleLogger.forLogger(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    simpleLogger.info("hello")
        .accept(Signal.error(
            new IllegalStateException("error"),
            TEST_CONTEXT));

    verify(logger).isEnabled(Level.ERROR);
    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessage(IllegalStateException.class, tag("test", "tag"), tag("message", "java.lang.IllegalStateException: error"))));
  }

  @Test
  public void testSimpleLoggerInfoFunction(@Mock Logger logger) {
    var simpleLogger = SimpleLogger.forLogger(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    simpleLogger.info(v -> "hello " + v)
        .accept(Signal.next(
            "world",
            TEST_CONTEXT));

    verify(logger).isEnabled(Level.INFO);
    verify(logger).log(eq(Level.INFO), argThat(
        valueMessage(tag("test", "tag"), tag("message", "hello world"))));

  }

  @Test
  public void testSimpleLoggerInfoFunctionError(@Mock Logger logger) {
    var simpleLogger = SimpleLogger.forLogger(logger);
    when(logger.isEnabled(any(Level.class))).thenReturn(true);

    simpleLogger.info(v -> "hello " + v)
        .accept(Signal.error(
            new IllegalStateException("error"),
            TEST_CONTEXT));

    verify(logger).isEnabled(Level.ERROR);
    verify(logger).log(eq(Level.ERROR), argThat(
        errorMessage(IllegalStateException.class, tag("test", "tag"), tag("message", "java.lang.IllegalStateException: error"))));
  }
}
