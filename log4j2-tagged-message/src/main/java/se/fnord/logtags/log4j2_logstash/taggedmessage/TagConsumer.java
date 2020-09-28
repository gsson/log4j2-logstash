package se.fnord.logtags.log4j2_logstash.taggedmessage;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Objects;

public interface TagConsumer<T> {
    default void objectTag(CharSequence key, @Nullable Object value, @Nullable T t) {
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
    void textTag(CharSequence key, CharSequence value, @Nullable T t);
    void longTag(CharSequence key, long value, @Nullable T t);
    void booleanTag(CharSequence key, boolean value, @Nullable T t);
    void doubleTag(CharSequence key, double value, @Nullable T t);
    void nullTag(CharSequence key, @Nullable T t);
}
