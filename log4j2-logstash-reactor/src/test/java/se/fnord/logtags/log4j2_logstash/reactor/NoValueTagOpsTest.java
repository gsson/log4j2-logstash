package se.fnord.logtags.log4j2_logstash.reactor;

import org.junit.jupiter.api.Test;
import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.TagsUtil;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static se.fnord.logtags.tags.TagsUtil.tag;

class NoValueTagOpsTest {
  private static final Tags T = Tags.of("t", "v");

  @Test
  public void testMessage() {
    var a = NoValueTagOps.message("message");
    TagsUtil.assertForEach(a.apply(T), tag("t", "v"), tag( "message", "message"));
  }

  @Test
  public void testMessageFormat() {
    var a = NoValueTagOps.message("message {}", 32);
    TagsUtil.assertForEach(a.apply(T), tag("t", "v"), tag( "message", "message 32"));
  }

  @Test
  public void testWhen() {
    var a = NoValueTagOps.when(() -> true, Function.identity());
    assertEquals(T, a.apply(T));

    a = NoValueTagOps.when(() -> false, Function.identity());
    assertNull(a.apply(T));

    a = NoValueTagOps.when(() -> true, t -> null);
    assertNull(a.apply(T));

    a = NoValueTagOps.when(() -> { throw new IllegalStateException(); }, Function.identity());
    assertNull(a.apply(T));

    a = NoValueTagOps.when(() -> true, t -> { throw new IllegalStateException(); });
    assertNull(a.apply(T));
  }

}