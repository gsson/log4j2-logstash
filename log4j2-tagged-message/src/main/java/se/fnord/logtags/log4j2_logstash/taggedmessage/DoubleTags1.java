package se.fnord.logtags.log4j2_logstash.taggedmessage;

import java.util.function.Consumer;

class DoubleTags1 implements Tags {
    private static final long serialVersionUID = 1L;
    private final CharSequence key;
    private final double value;

    private final Tags next;

    DoubleTags1(CharSequence key, double value, Tags next) {
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
        tagConsumer.doubleTag(key, value, state);
    }
}
