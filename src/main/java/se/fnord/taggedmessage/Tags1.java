package se.fnord.taggedmessage;

import java.util.function.Consumer;

class Tags1 implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence key;
    private final Object value;

    private final Tags next;

    Tags1(CharSequence key, Object value, Tags next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    @Override
    public void forEachGroup(Consumer<Tags> collectTo) {
        next.forEachGroup(collectTo);
        collectTo.accept(this);
    }

    @Override
    public <T> void forEachTagInGroup(T state, TagConsumer<T> tagConsumer) {
        tagConsumer.objectTag(key, value, state);
    }
}
