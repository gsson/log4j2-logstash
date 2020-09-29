package se.fnord.logtags.tags;

import java.util.function.Consumer;

class Tags0 implements Tags {
    private static final long serialVersionUID = 1L;
    static final Tags0 EMPTY = new Tags0();

    @Override
    public void forEachGroup(Consumer<Tags> collectTo) {
    }

    @Override
    public <T> void forEachTagInGroup(T state, TagConsumer<T> tagConsumer) {
    }
}
