package se.fnord.logtags.log4j2_logstash.reactor;

import org.junit.jupiter.api.Test;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import se.fnord.logtags.tags.Tags;

import static org.junit.jupiter.api.Assertions.*;

class TagOpsTest {
  private static final Tags T = Tags.of("t", "v");
  private static final ContextView C = Context.of(Tags.class, T);
  private static Tags tagsOrEmpty(ContextView view) {
    return view.getOrDefault(Tags.class, Tags.empty());
  }
  private static Tags throwing(ContextView view) {
    throw new IllegalStateException();
  }



  @Test
  void testTagsFromContext() {
    assertEquals(Tags.empty(), TagOps.tagsFromContext(TagOpsTest::tagsOrEmpty, Context.empty()));

    assertEquals(T, TagOps.tagsFromContext(TagOpsTest::tagsOrEmpty, C));

    assertEquals(Tags.empty(), TagOps.tagsFromContext(TagOpsTest::throwing, C));
    assertEquals(Tags.empty(), TagOps.tagsFromContext(null, C));
    assertEquals(Tags.empty(), TagOps.tagsFromContext(TagOpsTest::tagsOrEmpty, null));
    assertEquals(Tags.empty(), TagOps.tagsFromContext(c -> null, C));
  }

  @Test
  void testFormatString() {
    assertEquals("format me", TagOps.formatString("format {}", "me"));
  }
}