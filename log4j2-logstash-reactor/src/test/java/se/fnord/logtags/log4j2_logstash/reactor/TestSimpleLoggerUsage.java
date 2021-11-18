package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.TagsUtil;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static se.fnord.logtags.tags.TagsUtil.assertForEach;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestSimpleLoggerUsage {
  @Test
  public void simpleMono(@Mock Logger l) {
    var logger = SimpleLogger.forLogger(l);

    Mono.just("value")
        .doOnEach(logger.logCount(Level.INFO, "operation complete"))
        .block();

    var message = verifySingleTaggedMessage(l);
    assertMessage(message, null,
        tag("message", "operation complete"),
        tag("count", 1L));
  }

  @Test
  public void failedMono(@Mock Logger l) {
    var logger = SimpleLogger.forLogger(l);
    assertThrows(IllegalStateException.class, () ->
      Mono.error(new IllegalStateException("oops"))
          .doOnEach(logger.logCount(Level.INFO, "operation complete"))
          .block()
    );

    var message = verifySingleTaggedMessage(l);
    assertMessage(message, IllegalStateException.class,
        tag("message", "Exception after 0 published items: java.lang.IllegalStateException: oops"),
        tag("count", 0L));
  }

  @Test
  public void simpleFlux(@Mock Logger l) {
    var logger = SimpleLogger.forLogger(l);

    Flux.just("value1", "value2")
        .doOnEach(logger.logCount(Level.INFO, "operation complete"))
        .blockLast();

    var message = verifySingleTaggedMessage(l);
    assertMessage(message, null,
        tag("message", "operation complete"),
        tag("count", 2L));
  }

  @Test
  public void failedFlux(@Mock Logger l) {
    var logger = SimpleLogger.forLogger(l);

    assertThrows(IllegalStateException.class, () ->
      Flux.error(new IllegalStateException("oops"))
          .doOnEach(logger.logCount(Level.INFO, "howMany", "operation complete"))
          .blockLast()
    );

    var message = verifySingleTaggedMessage(l);
    assertMessage(message, IllegalStateException.class,
        tag("message", "Exception after 0 published items: java.lang.IllegalStateException: oops"),
        tag("howMany", 0L));
  }

  @Test
  public void partiallyFailedFlux(@Mock Logger logger) {
    var simpleLogger = SimpleLogger.forLogger(logger);

    assertThrows(IllegalStateException.class, () ->
        Flux.concat(Flux.just("value"), Flux.error(new IllegalStateException("oops")))
            .doOnEach(simpleLogger.logCountTags(
                Level.INFO, n -> Tags.of("message", "operation complete", "count", n),
                Level.ERROR, (n, t) -> Tags.of("message", "operation failed", "count", n)
            ))
            .blockLast()
    );

    var message = verifySingleTaggedMessage(logger);
    assertMessage(message, IllegalStateException.class,
        tag("message", "operation failed"),
        tag("count", 1L));
  }

  private static TaggedMessage verifySingleTaggedMessage(Logger logger) {
    var message = ArgumentCaptor.forClass(TaggedMessage.class);

    verify(logger).log(gt(Level.OFF), message.capture());
    verifyNoMoreInteractions(logger);


    var logged = message.getAllValues();
    assertEquals(1, logged.size());
    return logged.get(0);
  }

  private static void assertMessage(TaggedMessage message, @Nullable Class<? extends Throwable> throwable, TagsUtil.Tag... tags) {
    if (throwable != null) {
      assertInstanceOf(throwable, message.getThrowable());
    } else {
      assertNull(message.getThrowable());
    }

    assertForEach(message.getTags(), tags);
  }
}


