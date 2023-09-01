package se.fnord.logtags.tags;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.function.Consumer;

class Tags1 implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence key;
    @Nullable
    private final Object value;

    private final Tags next;

    Tags1(CharSequence key, @Nullable Object value, Tags next) {
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

    @Override
    public String toString() {
        return TagToString.toString(this);
    }
}
