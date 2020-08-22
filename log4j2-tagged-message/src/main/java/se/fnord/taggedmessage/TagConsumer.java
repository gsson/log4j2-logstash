package se.fnord.taggedmessage;

import java.util.Objects;

public interface TagConsumer<T> {
    default void objectTag(CharSequence key, Object value, T t) {
        if (value == null) {
            nullTag(key, t);
        }
        else if (value instanceof CharSequence) {
            textTag(key, (CharSequence) value, t);
        }
        else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            longTag(key, ((Number) value).longValue(), t);
        }
        else if (value instanceof Float || value instanceof Double) {
            doubleTag(key, ((Number) value).doubleValue(), t);
        }
        else if (value instanceof Boolean) {
            booleanTag(key, (Boolean) value, t);
        }
        else {
            textTag(key, Objects.toString(value), t);
        }
    }
    void textTag(CharSequence key, CharSequence value, T t);
    void longTag(CharSequence key, long value, T t);
    void booleanTag(CharSequence key, boolean value, T t);
    void doubleTag(CharSequence key, double value, T t);
    void nullTag(CharSequence key, T t);
}
