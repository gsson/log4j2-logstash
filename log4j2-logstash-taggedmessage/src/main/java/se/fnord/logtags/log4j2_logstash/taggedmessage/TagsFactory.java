package se.fnord.logtags.log4j2_logstash.taggedmessage;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

class TagsFactory {
    private static final Map<Class<?>, Boolean> SAFE_TYPES;

    static {
        IdentityHashMap<Class<?>, Boolean> safeTypes = new IdentityHashMap<>();
        safeTypes.put(String.class, Boolean.TRUE);
        safeTypes.put(Long.class, Boolean.TRUE);
        safeTypes.put(Integer.class, Boolean.TRUE);
        safeTypes.put(Short.class, Boolean.TRUE);
        safeTypes.put(Byte.class, Boolean.TRUE);
        safeTypes.put(Float.class, Boolean.TRUE);
        safeTypes.put(Double.class, Boolean.TRUE);
        safeTypes.put(Boolean.class, Boolean.TRUE);
        SAFE_TYPES = safeTypes;
    }

    static Tags fromMap(Map<? extends CharSequence, ?> map, Tags next) {
        if (map.isEmpty()) {
            return next;
        }

        int size = map.size();
        if (size == 1) {
            Map.Entry<? extends CharSequence, ?> e = map.entrySet().iterator().next();
            return new Tags1(e.getKey(), normaliseObjectValue(e.getValue()), next);
        }

        CharSequence[] tagNames = new CharSequence[size];
        Object[] tagValues = new Object[size];
        int i = 0;
        for (Map.Entry<? extends CharSequence, ?> e : map.entrySet()) {
            tagNames[i] = e.getKey();
            tagValues[i++] = normaliseObjectValue(e.getValue());
        }
        return new TagsN(tagNames, tagValues, next);
    }

    @Nullable
    static Object normaliseObjectValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        return SAFE_TYPES.containsKey(value.getClass()) ? value : value.toString();
    }

    public static Tags compose(Tags first, Tags second) {
        if (first instanceof Tags0) {
            return second;
        }
        if (second instanceof Tags0) {
            return first;
        }
        return new CompositeTags2(first, second);
    }
}