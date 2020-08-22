package se.fnord.taggedmessage;

import java.util.function.Consumer;

class BooleanTags1 implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence key;
    private final boolean value;

    private final Tags next;

    BooleanTags1(CharSequence key, boolean value, Tags next) {
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
        tagConsumer.booleanTag(key, value, state);
    }
}
