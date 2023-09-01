package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface ValueTagsFunction<T> extends BiFunction<Tags, T, ToTags> {

  static <U> ValueTagsFunction<U> from(BiFunction<? super Tags, ? super U, ? extends ToTags> tagsFunction) {
    return tagsFunction::apply;
  }

  static <U> @Nullable ValueTagsFunction<U> fromNullable(@Nullable BiFunction<? super Tags, ? super U, ? extends ToTags> tagsFunction) {
    return tagsFunction == null ? null : tagsFunction::apply;
  }

  default ValueTagsFunction<T> add(ValueTagsFunction<? super T> after) {
    Objects.requireNonNull(after);
    return (tags, value) -> {
      var tags1 = TagOps.toTags(ValueTagOps.apply(this, tags, value));
      return ValueTagOps.apply(after, tags1, value);
    };
  }

  default ValueTagsFunction<T> add(Function<? super T, ? extends ToTags> after) {
    Objects.requireNonNull(after);
    return (tags, value) -> {
      var tags1 = TagOps.toTags(ValueTagOps.apply(this, tags, value));
      return ValueTagOps.apply(after, tags1, value);
    };
  }

  default ValueTagsFunction<T> add(ToTags tags) {
    Objects.requireNonNull(tags, "tags");

    return (t, o) -> t.add(tags);
  }

  default ValueTagsFunction<T> or(BiFunction<? super Tags, ? super T, ? extends ToTags> after) {
    return ValueTagOps.or(this, after);
  }
}
