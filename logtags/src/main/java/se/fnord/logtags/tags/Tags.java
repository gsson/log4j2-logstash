package se.fnord.logtags.tags;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("serial")
public interface Tags extends Serializable {
    default <T> void forEach(@Nullable T state, TagConsumer<T> tagConsumer) {
        forEachGroup(g -> g.forEachTagInGroup(state, tagConsumer));
    }

    void forEachGroup(Consumer<Tags> collectTo);
    <T> void forEachTagInGroup(@Nullable T state, TagConsumer<T> tagConsumer);

    default Tags add(String key, @Nullable Object value) {
        return new Tags1(key, TagsFactory.normaliseObjectValue(value), this);
    }

    default Tags add(String key, long value) {
        return new LongTags1(key, value, this);
    }
    default Tags add(String key, double value) {
        return new DoubleTags1(key, value, this);
    }
    default Tags add(String key, boolean value) {
        return new BooleanTags1(key, value, this);
    }

    default Tags add(String key1, @Nullable Object value1, String key2, @Nullable Object value2) {
        return new TagsN(new String[] { key1, key2 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2) }, this);
    }

    default Tags add(String key1, @Nullable Object value1, String key2, @Nullable Object value2, String key3, @Nullable Object value3) {
        return new TagsN(new String[] { key1, key2, key3 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2), TagsFactory.normaliseObjectValue(value3) }, this);
    }

    default Tags add(String key1, @Nullable Object value1, String key2, @Nullable Object value2, String key3, @Nullable Object value3, String key4, @Nullable Object value4) {
        return new TagsN(new String[] { key1, key2, key3, key4 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2), TagsFactory.normaliseObjectValue(value3), TagsFactory.normaliseObjectValue(value4) }, this);
    }

    default Tags add(String[] keys, Object[] values) {
        var tagsLength = Math.min(keys.length, values.length);
        var normalisedValues = new Object[tagsLength];
        for (int i = 0; i < tagsLength; i++) {
            normalisedValues[i] = TagsFactory.normaliseObjectValue(values[i]);
        }
        return new TagsN(Arrays.copyOf(keys, tagsLength), normalisedValues, this);
    }

    default Tags add(Map<String, ?> tags) {
        return TagsFactory.fromMap(tags, this);
    }

    default Tags add(Tags tags) {
        return TagsFactory.compose(tags, this);
    }

    default Tags add(ToTags tags) {
        return TagsFactory.compose(tags.toTags(), this);
    }

    static Tags of(String key, @Nullable Object value) {
        return new Tags1(key, TagsFactory.normaliseObjectValue(value), empty());
    }

    static Tags of(String key, long value) {
        return new LongTags1(key, value, empty());
    }

    static Tags of(String key, double value) {
        return new DoubleTags1(key, value, empty());
    }

    static Tags of(String key, boolean value) {
        return new BooleanTags1(key, value, empty());
    }

    static Tags of(String key1, @Nullable Object value1, String key2, @Nullable Object value2) {
        return new TagsN(new String[] { key1, key2 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2) }, empty());
    }

    static Tags of(String key1, @Nullable Object value1, String key2, @Nullable Object value2, String key3, @Nullable Object value3) {
        return new TagsN(new String[] { key1, key2, key3 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2), TagsFactory.normaliseObjectValue(value3) }, empty());
    }

    static Tags of(String key1, @Nullable Object value1, String key2, @Nullable Object value2, String key3, @Nullable Object value3, String key4, @Nullable Object value4) {
        return new TagsN(new String[] { key1, key2, key3, key4 }, new Object[] { TagsFactory.normaliseObjectValue(value1), TagsFactory.normaliseObjectValue(value2), TagsFactory.normaliseObjectValue(value3), TagsFactory.normaliseObjectValue(value4) }, empty());
    }

    static Tags of(String[] keys, Object[] values) {
        var tagsLength = Math.min(keys.length, values.length);
        var normalisedValues = new Object[tagsLength];
        for (int i = 0; i < tagsLength; i++) {
            normalisedValues[i] = TagsFactory.normaliseObjectValue(values[i]);
        }
        return new TagsN(Arrays.copyOf(keys, tagsLength), normalisedValues, empty());
    }

    static Tags of(Map<String, ?> tags) {
        return TagsFactory.fromMap(tags, empty());
    }

    static Tags empty() {
        return Tags0.EMPTY;
    }
}
