package se.fnord.logtags.log4j2_logstash.reactor;

import org.junit.jupiter.api.Test;
import reactor.util.context.Context;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.fnord.logtags.log4j2_logstash.taggedmessage.TagsUtil.collectTags;

public class TestElapsedTime {
  @Test
  public void testGetElapsedMillis() {
    var i = new AtomicInteger(0);
    var times = List.of(1_000_000_000L, 2_000_000_000L);
    var elapsedMillis = ElapsedTime.create(() -> times.get(i.getAndIncrement()));

    assertEquals(1000L, elapsedMillis.getElapsedMillis());
  }

  @Test
  public void testTagsFromContext() {
    var context = ElapsedTime.addToContext(Context.empty());
    var tags = ElapsedTime.tagsFromContext(context);

    var collectedTags = collectTags(tags);
    assertEquals(1, collectedTags.size());

    var elapsedMillisTag  = collectedTags.get(0);
    assertEquals("elapsed_time", elapsedMillisTag.key());
    assertTrue(elapsedMillisTag.value() instanceof Long);
  }
}
