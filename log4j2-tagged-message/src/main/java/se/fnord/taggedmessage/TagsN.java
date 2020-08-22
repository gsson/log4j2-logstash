package se.fnord.taggedmessage;

import java.util.function.Consumer;

class TagsN implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence[] keys;
    private final Object[] values;

    private final Tags next;

    TagsN(CharSequence[] keys, Object[] values, Tags next) {
        this.keys = keys;
        this.values = values;
        this.next = next;
    }

    @Override
    public void forEachGroup(Consumer<Tags> collectTo) {
        next.forEachGroup(collectTo);
        collectTo.accept(this);
    }

    @Override
    public <T> void forEachTagInGroup(T state, TagConsumer<T> tagConsumer) {
        for (int i = 0; i < keys.length; i++) {
            tagConsumer.objectTag(keys[i], values[i], state);
        }
    }
}
