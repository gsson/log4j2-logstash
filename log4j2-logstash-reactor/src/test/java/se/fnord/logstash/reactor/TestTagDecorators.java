package se.fnord.logstash.reactor;

import org.junit.jupiter.api.Test;
import se.fnord.taggedmessage.Tags;

import static se.fnord.taggedmessage.TagsUtil.assertForEach;
import static se.fnord.taggedmessage.TagsUtil.tag;

public class TestTagDecorators {
  @Test
  public void testWithMessageString() {
    var tags = TagDecorators.withMessage("hello")
        .apply(Tags.of("a", "b"), "c");
    assertForEach(tags, tag("a", "b"), tag("message", "hello"));
  }

  @Test
  public void testWithMessageSupplier() {
    var tags = TagDecorators.withMessage(() -> "hello")
        .apply(Tags.of("a", "b"), "c");
    assertForEach(tags, tag("a", "b"), tag("message", "hello"));
  }

  @Test
  public void testWithMessageFunction() {
    var tags = TagDecorators.withMessage(v -> "hello " + v)
        .apply(Tags.of("a", "b"), "c");
    assertForEach(tags, tag("a", "b"), tag("message", "hello c"));
  }

  @Test
  public void testErrorMessage() {
    var tags = TagDecorators.errorMessage()
        .apply(Tags.of("a", "b"), new IllegalStateException("c"));
    assertForEach(tags, tag("a", "b"), tag("message", "java.lang.IllegalStateException: c"));
  }
}
