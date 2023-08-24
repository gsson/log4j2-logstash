package se.fnord.logtags.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TagsUtil {
    @FunctionalInterface
    public interface TriConsumer<T> {
        void accept(CharSequence tag, Object value, T context);
    }

    public static <T> TagConsumer<T> wrapConsumer(TriConsumer<T> wrapped) {
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

    public static TagConsumer<List<Tag>> toListConsumer() {
        return new ToListConsumer();
    }

    public static class ToListConsumer implements TagConsumer<List<Tag>>{

        @Override
        public void textTag(CharSequence key, CharSequence value, List<Tag> tags) {
            tags.add(new Tag(key, value));
        }

        @Override
        public void longTag(CharSequence key, long value, List<Tag> tags) {
            tags.add(new Tag(key, value));
        }

        @Override
        public void booleanTag(CharSequence key, boolean value, List<Tag> tags) {
            tags.add(new Tag(key, value));
        }

        @Override
        public void doubleTag(CharSequence key, double value, List<Tag> tags) {
            tags.add(new Tag(key, value));
        }

        @Override
        public void nullTag(CharSequence key, List<Tag> tags) {
            tags.add(new Tag(key, null));
        }
    }

    public static List<Tag> collectTags(ToTags tags) {
        List<Tag> tagList = new ArrayList<>();
        tags.toTags().forEach(tagList, toListConsumer());
        return tagList;
    }

    public static void assertForEach(ToTags tags, Tag ... expected) {
        List<Tag> tagList = collectTags(tags);

        assertIterableEquals(asList(expected), tagList);
    }
}
