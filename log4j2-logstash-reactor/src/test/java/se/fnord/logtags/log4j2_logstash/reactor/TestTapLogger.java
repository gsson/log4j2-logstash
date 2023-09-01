package se.fnord.logtags.log4j2_logstash.reactor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import se.fnord.logtags.tags.TagLogger;
import se.fnord.logtags.tags.Tags;

import static org.mockito.ArgumentMatchers.*;
import static se.fnord.logtags.tags.TagsMatcher.tagsEq;
import static se.fnord.logtags.tags.TagsUtil.tag;

@ExtendWith(MockitoExtension.class)
public class TestTapLogger {
  @Test
  void testDefaultLogger(@Mock TagLogger tagLogger) {
    var tapLogger = TapLogger.<String>forLogger(tagLogger);

    var mono = Mono.just("value")
        .tap(tapLogger)
        .contextWrite(Context.of(Tags.class, Tags.of("contextKey", "contextValue")));

    StepVerifier.create(mono)
        .expectNext("value")
        .verifyComplete();

    Mockito.verifyNoInteractions(tagLogger);
  }

  @Test
  void testDisabledOnNextLogger(@Mock TagLogger tagLogger) {
    var tapLogger = TapLogger.forLogger(tagLogger)
        .<String>onNext(TagLogger.Level.INFO, t -> t.message("message"));

    Mockito.when(tagLogger.isEnabled(TagLogger.Level.INFO))
        .thenReturn(false);

    var mono = Mono.just("value")
        .tap(tapLogger)
        .contextWrite(Context.of(Tags.class, Tags.of("contextKey", "contextValue")));

    StepVerifier.create(mono)
        .expectNext("value")
        .verifyComplete();

    Mockito.verify(tagLogger)
        .isEnabled(TagLogger.Level.INFO);
    Mockito.verifyNoMoreInteractions(tagLogger);
  }

  @Test
  void testEnabledOnNextLogger(@Mock TagLogger tagLogger) {
    var tapLogger = TapLogger.forLogger(tagLogger)
        .<String>onNext(TagLogger.Level.INFO, t -> t.message("message"));

    Mockito.when(tagLogger.isEnabled(TagLogger.Level.INFO))
        .thenReturn(true);

    var mono = Mono.just("value")
        .tap(tapLogger)
        .contextWrite(Context.of(Tags.class, Tags.of("contextKey", "contextValue")));

    StepVerifier.create(mono)
        .expectNext("value")
        .verifyComplete();

    Mockito.verify(tagLogger)
        .isEnabled(TagLogger.Level.INFO);
    Mockito.verify(tagLogger)
        .log(
            eq(TagLogger.Level.INFO),
            tagsEq(
                tag("contextKey", "contextValue"),
                tag("message", "message")),
            isNull(Throwable.class));

    Mockito.verifyNoMoreInteractions(tagLogger);
  }
}
