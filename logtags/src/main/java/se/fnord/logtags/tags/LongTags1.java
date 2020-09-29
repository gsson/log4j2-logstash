package se.fnord.logtags.tags;

import java.util.function.Consumer;

class LongTags1 implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence key;
    private final long value;

    private final Tags next;

    LongTags1(CharSequence key, long value, Tags next) {
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
        tagConsumer.longTag(key, value, state);
    }
}
