package se.fnord.logstash.reactor;

import org.junit.jupiter.api.Test;
import reactor.util.context.Context;
import se.fnord.taggedmessage.Tags;

import static se.fnord.taggedmessage.TagsUtil.assertForEach;
import static se.fnord.taggedmessage.TagsUtil.tag;

public class TestContextTags {
  @Test
  public void testUpdateTagsWithEmptyContext() {
    var context = ContextTags.updateTags(
        Context.empty(),
        t -> t.add("a", "b"));
    assertForEach(context.get(Tags.class), tag("a", "b"));
  }

  @Test
  public void testUpdateTagsWithNonEmptyContext() {
    var context = ContextTags.updateTags(
        Context.of(Tags.class, Tags.of("c", "d")),
        t -> t.add("a", "b"));
    assertForEach(context.get(Tags.class), tag("c", "d"), tag("a", "b"));
  }

  @Test
  public void testLazyUpdateTagsWithEmptyContext() {
    var context = ContextTags.updateTags(t -> t.add("a", "b"))
        .apply(Context.empty());
    assertForEach(context.get(Tags.class), tag("a", "b"));
  }

  @Test
  public void testLazyUpdateTagsWithNonEmptyContext() {
    var context = ContextTags.updateTags(t -> t.add("a", "b"))
        .apply(Context.of(Tags.class, Tags.of("c", "d")));
    assertForEach(context.get(Tags.class), tag("c", "d"), tag("a", "b"));
  }

  @Test
  public void testAddTagsWithEmptyContext() {
    var context = ContextTags.addTags(
        Context.empty(),
        Tags.of("a", "b"));
    assertForEach(context.get(Tags.class), tag("a", "b"));
  }

  @Test
  public void testAddTagsWithNonEmptyContext() {
    var context = ContextTags.addTags(
        Context.of(Tags.class, Tags.of("c", "d")),
        Tags.of("a", "b"));
    assertForEach(context.get(Tags.class), tag("c", "d"), tag("a", "b"));
  }

  @Test
  public void testLazyAddTagsWithEmptyContext() {
    var context = ContextTags.addTags(Tags.of("a", "b"))
        .apply(Context.empty());
    assertForEach(context.get(Tags.class), tag("a", "b"));
  }

  @Test
  public void testLazyAddTagsWithNonEmptyContext() {
    var context = ContextTags.addTags(Tags.of("a", "b"))
        .apply(Context.of(Tags.class, Tags.of("c", "d")));
    assertForEach(context.get(Tags.class), tag("c", "d"), tag("a", "b"));
  }

  @Test
  public void testAddTagWithEmptyContext() {
    var context = ContextTags.addTag("a", "b")
        .apply(Context.empty());
    assertForEach(context.get(Tags.class), tag("a", "b"));
  }

  @Test
  public void testAddTagWithNonEmptyContext() {
    var context = ContextTags.addTag("a", "b")
        .apply(Context.of(Tags.class, Tags.of("c", "d")));
    assertForEach(context.get(Tags.class), tag("c", "d"), tag("a", "b"));
  }
}
