package se.fnord.taggedmessage;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.fail;
import static se.fnord.taggedmessage.TagsUtil.assertForEach;
import static se.fnord.taggedmessage.TagsUtil.tag;
import static se.fnord.taggedmessage.TagsUtil.wrapConsumer;

public class TestTags {
    @Test
    public void testForEach() {
        Tags.empty()
                .forEach(null, wrapConsumer((k, v, s) -> fail("Empty tags should not invoke function")));

        assertForEach(Tags.of("a", "b")
                .add("c", "d")
                .add( "e", "f", "g", "h"),
                tag("a", "b"), tag("c", "d"), tag("e", "f"), tag("g", "h"));
    }

    @Test
    public void testOf() {
        assertForEach(Tags.of("a", 1), tag("a", 1L));
        assertForEach(Tags.of("a", true), tag("a", true));
        assertForEach(Tags.of("a", 1.0), tag("a", 1.0));
        assertForEach(Tags.of("a", "b"), tag("a", "b"));
        assertForEach(Tags.of("a", "b", "c", "d"), tag("a", "b"), tag("c", "d"));
        assertForEach(Tags.of("a", "b", "c", "d", "e", "f"), tag("a", "b"), tag("c", "d"), tag("e", "f"));
        assertForEach(Tags.of("a", "b", "c", "d", "e", "f", "g", "h"), tag("a", "b"), tag("c", "d"), tag("e", "f"), tag("g", "h"));
    }

    @Test
    public void testAdd() {
        assertForEach(Tags.empty().add("a", 1), tag("a", 1L));
        assertForEach(Tags.empty().add("a", true), tag("a", true));
        assertForEach(Tags.empty().add("a", 1.0), tag("a", 1.0));
        assertForEach(Tags.empty().add("a", "b"), tag("a", "b"));
        assertForEach(Tags.empty().add("a", "b"), tag("a", "b"));
        assertForEach(Tags.empty().add("a", "b", "c", "d"), tag("a", "b"), tag("c", "d"));
        assertForEach(Tags.empty().add("a", "b", "c", "d", "e", "f"), tag("a", "b"), tag("c", "d"), tag("e", "f"));
        assertForEach(Tags.empty().add("a", "b", "c", "d", "e", "f", "g", "h"), tag("a", "b"), tag("c", "d"), tag("e", "f"), tag("g", "h"));
    }

    @Test
    public void testAddComposite() {
        assertForEach(Tags.of("a", 1, "b", 2).add(Tags.of("c", 3, "d", 4)),
            tag("a", 1L), tag("b", 2L), tag("c", 3L), tag("d", 4L));
    }

    @Test
    public void testNormalisation() {
        assertForEach(Tags.empty().add("a", singletonList(32)), tag("a", "[32]"));
        assertForEach(Tags.of("a", singletonList(32)), tag("a", "[32]"));

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("a", singletonList(32));

        assertForEach(Tags.of(m), tag("a", "[32]"));
    }

    @Test
    public void testTagsFromMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("a", 1);
        m.put("b", "c");

        assertForEach(Tags.empty().add(m), tag("a", 1L), tag("b", "c"));
        assertForEach(Tags.of(m), tag("a", 1L), tag("b", "c"));
    }
}
