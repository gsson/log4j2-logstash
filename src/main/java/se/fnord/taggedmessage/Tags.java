package se.fnord.taggedmessage;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

import static se.fnord.taggedmessage.TagsFactory.normaliseObjectValue;

@SuppressWarnings("serial")
public interface Tags extends Serializable {
    default <T> void forEach(T state, TagConsumer<T> tagConsumer) {
        forEachGroup(g -> g.forEachTagInGroup(state, tagConsumer));
    }

    void forEachGroup(Consumer<Tags> collectTo);
    <T> void forEachTagInGroup(T state, TagConsumer<T> tagConsumer);

    default Tags add(String key, Object value) {
        return new Tags1(key, normaliseObjectValue(value), this);
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

    default Tags add(String key1, Object value1, String key2, Object value2) {
        return new TagsN(new String[] { key1, key2 }, new Object[] { normaliseObjectValue(value1), normaliseObjectValue(value2) }, this);
    }

    default Tags add(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return new TagsN(new String[] { key1, key2, key3 }, new Object[] { normaliseObjectValue(value1), normaliseObjectValue(value2), normaliseObjectValue(value3) }, this);
    }

    default Tags add(Map<String, ?> tags) {
        return TagsFactory.fromMap(tags, this);
    }

    static Tags of(String key, Object value) {
        return new Tags1(key, normaliseObjectValue(value), empty());
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

    static Tags of(String key1, Object value1, String key2, Object value2) {
        return new TagsN(new String[] { key1, key2 }, new Object[] { normaliseObjectValue(value1), normaliseObjectValue(value2) }, empty());
    }

    static Tags of(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        return new TagsN(new String[] { key1, key2, key3 }, new Object[] { normaliseObjectValue(value1), normaliseObjectValue(value2), normaliseObjectValue(value3) }, empty());
    }

    static Tags of(Map<String, ?> tags) {
        return TagsFactory.fromMap(tags, empty());
    }

    static Tags empty() {
        return Tags0.EMPTY;
    }
}
