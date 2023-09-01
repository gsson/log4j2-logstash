package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface NoValueTagsFunction extends Function<Tags, ToTags> {

  static NoValueTagsFunction from(Function<? super Tags, ? extends ToTags> tagsFunction) {
    return tagsFunction::apply;
  }

  static @Nullable NoValueTagsFunction fromNullable(@Nullable Function<? super Tags, ? extends ToTags> tagsFunction) {
    return tagsFunction == null ? null : tagsFunction::apply;
  }

  default NoValueTagsFunction add(NoValueTagsFunction after) {
    Objects.requireNonNull(after);
    return tags -> {
      var tags1 = TagOps.toTags(NoValueTagOps.apply(this, tags));
      return NoValueTagOps.apply(after, tags1);
    };
  }

  default NoValueTagsFunction add(Supplier<? extends ToTags> after) {
    Objects.requireNonNull(after);
    return tags -> {
      var tags1 = TagOps.toTags(NoValueTagOps.apply(this, tags));
      return NoValueTagOps.apply(after, tags1);
    };
  }

  default NoValueTagsFunction add(ToTags tags) {
    Objects.requireNonNull(tags, "tags");

    return t -> t.add(tags);
  }

  default NoValueTagsFunction or(Function<? super Tags, ? extends ToTags> after) {
    return NoValueTagOps.or(this, after);
  }
}
