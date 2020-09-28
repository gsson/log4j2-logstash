package se.fnord.logtags.log4j2_logstash.taggedmessage;

import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TagsUtil {
    public static <T> TagConsumer<T> wrapConsumer(TriConsumer<CharSequence, Object, T> wrapped) {
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
    public static class Tag {
        private final CharSequence key;
        private final Object value;

        Tag(CharSequence key, Object value) {
            this.key = key;
            this.value = value;
        }

        public CharSequence key() {
            return key;
        }

        public Object value() {
            return value;
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
    public static Tag tag(String key, Object value) {
        return new Tag(key, value);
    }

    public static List<Tag> collectTags(Tags tags) {
        List<Tag> tagList = new ArrayList<>();
        tags.forEach(tagList, wrapConsumer((k, v, l) -> l.add(new Tag(k, v))));
        return tagList;
    }

    public static void assertForEach(Tags tags, Tag ... expected) {
        List<Tag> tagList = collectTags(tags);

        assertIterableEquals(asList(expected), tagList);
    }
}
