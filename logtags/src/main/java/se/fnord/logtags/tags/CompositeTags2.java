package se.fnord.logtags.tags;

import java.util.function.Consumer;

class CompositeTags2 implements Tags {
    private static final long serialVersionUID = 1L;
    private final Tags first;
    private final Tags second;

    CompositeTags2(Tags first, Tags second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void forEachGroup(Consumer<Tags> collectTo) {
        second.forEachGroup(collectTo);
        first.forEachGroup(collectTo);
    }

    @Override
    public <T> void forEachTagInGroup(T state, TagConsumer<T> tagConsumer) {
    }
}
