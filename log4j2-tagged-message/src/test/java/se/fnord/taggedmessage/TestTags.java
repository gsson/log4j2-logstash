package se.fnord.taggedmessage;

import org.apache.logging.log4j.util.TriConsumer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestTags {
    static <T> TagConsumer<T> wrapConsumer(TriConsumer<CharSequence, Object, T> wrapped) {
        return new TagConsumer<T>() {
            @Override
            public void textTag(CharSequence key, CharSequence value, T t) {
                wrapped.accept(key, value, t);
            }

            @Override
            public void longTag(CharSequence key, long value, T t) {
                wrapped.accept(key, value, t);
            }

            @Override
            public void booleanTag(CharSequence key, boolean value, T t) {
                wrapped.accept(key, value, t);
            }

            @Override
            public void doubleTag(CharSequence key, double value, T t) {
                wrapped.accept(key, value, t);
            }

            @Override
            public void nullTag(CharSequence key, T t) {
                wrapped.accept(key, null, t);
            }
        };
    }
    static class Tag {
        private final CharSequence key;
        private final Object value;

        Tag(CharSequence key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tag tag = (Tag) o;
            return Objects.equals(key, tag.key) &&
                    Objects.equals(value, tag.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return String.format("<%s: \"%s\">", key, value);
        }
    }
    private static Tag tag(String key, Object value) {
        return new Tag(key, value);
    }

    private static List<Tag> collectTags(Tags tags) {
        List<Tag> tagList = new ArrayList<>();
        tags.forEach(tagList, wrapConsumer((k, v, l) -> l.add(new Tag(k, v))));
        return tagList;
    }

    private static void assertForEach(Tags tags, Tag ... expected) {
        List<Tag> tagList = collectTags(tags);

        assertIterableEquals(asList(expected), tagList);
    }

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
