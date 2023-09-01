package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.*;

import static se.fnord.logtags.log4j2_logstash.reactor.TagOps.formatString;

public class NoValueTagOps {
  private static boolean test(BooleanSupplier f) {
    try {
      return f.getAsBoolean();
    } catch (RuntimeException e) {
      return false;
    }
  }

  static @Nullable ToTags applyOr(Function<? super Tags, ? extends ToTags> first, Function<? super Tags, ? extends ToTags> second, Tags tags) {
    var t = apply(first, tags);
    if (t != null) {
      return t;
    }
    return apply(second, tags);
  }

  static <TT extends ToTags> @Nullable TT apply(Function<? super Tags, TT> f, Tags tags) {
    try {
      return f.apply(tags);
    } catch (RuntimeException e) {
      return null;
    }
  }

  static @Nullable Tags apply(Supplier<? extends ToTags> f, Tags tags) {
    Objects.requireNonNull(tags, "tags");

    try {
      var fTags = f.get();
      return fTags == null ? null : tags.add(fTags);
    } catch (RuntimeException e) {
      return null;
    }
  }

  static NoValueTagsFunction forSupplier(Supplier<? extends ToTags> f) {
    Objects.requireNonNull(f, "f");

    return tags -> apply(f, tags);
  }

  public static NoValueTagsFunction message(String message) {
    Objects.requireNonNull(message, "message");
    return tags -> tags.add("message", message);
  }

  public static NoValueTagsFunction message(String message, Object... args) {
    Objects.requireNonNull(message, "message");
    Objects.requireNonNull(args, "args");
    return tags -> tags.add("message", formatString(message, args));
  }

  public static NoValueTagsFunction when(BooleanSupplier predicate,
      Function<? super Tags, ? extends ToTags> f) {
    Objects.requireNonNull(predicate);
    Objects.requireNonNull(f);
    return tags -> test(predicate) ? apply(f, tags) : null;
  }


  public static @Nullable NoValueTagsFunction or(
      @Nullable Function<? super Tags, ? extends ToTags> first,
      @Nullable Function<? super Tags, ? extends ToTags> second) {
    if (second == null) {
      return NoValueTagsFunction.fromNullable(first);
    }
    if (first == null) {
      return NoValueTagsFunction.fromNullable(second);
    }
    return tags -> applyOr(first, second, tags);
  }
}
