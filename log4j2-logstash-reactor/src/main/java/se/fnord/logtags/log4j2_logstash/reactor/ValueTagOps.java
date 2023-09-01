package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static se.fnord.logtags.log4j2_logstash.reactor.TagOps.formatString;

public class ValueTagOps {
  private static <T> boolean test(Predicate<? super T> f, T value) {
    try {
      return f.test(value);
    } catch (RuntimeException e) {
      return false;
    }
  }

  static <T> @Nullable ToTags applyOr(BiFunction<? super Tags, ? super T, ? extends ToTags> first, BiFunction<? super Tags, ? super T, ? extends ToTags> second, Tags tags, T value) {
    var t = apply(first, tags, value);
    if (t != null) {
      return t;
    }
    return apply(second, tags, value);
  }

  static <T, TT extends ToTags> @Nullable TT apply(BiFunction<? super Tags, ? super T, TT> f, Tags tags, T value) {
    try {
      return f.apply(tags, value);
    } catch (RuntimeException e) {
      return null;
    }
  }

  static <T> @Nullable Tags apply(Function<? super T, ? extends ToTags> f, Tags tags, T value) {
    try {
      var fTags = f.apply(value);
      return fTags == null ? null : tags.add(fTags);
    } catch (RuntimeException e) {
      return null;
    }
  }

  static <T> @Nullable Tags applyMessageFunction(Function<? super T, String> f, Tags tags, T value) {
    try {
      var message = f.apply(value);
      return message == null ? null : tags.add("message", message);
    } catch (RuntimeException e) {
      return null;
    }
  }

  static <T> BiFunction<Tags, ? super T, Tags> forFunction(Function<? super T, ? extends ToTags> f) {
    Objects.requireNonNull(f, "f");

    return (tags, value) -> apply(f, tags, value);
  }

  static <T> BiFunction<Tags, ? super T, Tags> forMessageFunction(Function<? super T, String> f) {
    Objects.requireNonNull(f, "f");

    return (tags, value) -> applyMessageFunction(f, tags, value);
  }

  public static <T> ValueTagsFunction<T> valueToString() {
    return (t, o) -> t.add("message", Objects.toString(o));
  }

  public static <T> ValueTagsFunction<T> message(String message) {
    Objects.requireNonNull(message, "message");
    return (t, o) -> t.add("message", message);
  }

  public static <T> ValueTagsFunction<T>  message(String message, Object... args) {
    Objects.requireNonNull(message, "message");
    Objects.requireNonNull(args, "args");
    return (t, o) -> t.add("message", formatString(message, args));
  }

  public static <T> ValueTagsFunction<T>  add(String tag, String value) {
    Objects.requireNonNull(tag, "tag");

    return (t, o) -> t.add(tag, value);
  }

  public static <T> ValueTagsFunction<T>  add(String tag, Function<? super T, ?> valueMapper) {
    Objects.requireNonNull(tag, "tag");
    Objects.requireNonNull(valueMapper, "valueMapper");

    return (t, o) -> t.add(tag, valueMapper.apply(o));
  }

  public static <T> ValueTagsFunction<T>  add(ToTags tags) {
    Objects.requireNonNull(tags, "tags");

    return (t, o) -> t.add(tags);
  }

  public static <T> ValueTagsFunction<T>  add(Function<? super T, ? extends ToTags> tags) {
    Objects.requireNonNull(tags, "tags");

    return (t, o) -> t.add(apply(tags, t, o));
  }

  public static <T> ValueTagsFunction<T>  when(Predicate<? super T> predicate,
      ValueTagsFunction<? super T> tags) {
    Objects.requireNonNull(predicate);
    Objects.requireNonNull(tags);
    return (t, o) -> test(predicate, o) ? apply(tags, t, o) : null;
  }

  public static <T> @Nullable ValueTagsFunction<T> or(
      @Nullable BiFunction<? super Tags, ? super T, ? extends ToTags> first,
      @Nullable BiFunction<? super Tags, ? super T, ? extends ToTags> second) {
    if (second == null) {
      return ValueTagsFunction.fromNullable(first);
    }
    if (first == null) {
      return ValueTagsFunction.fromNullable(second);
    }
    return (tags, value) -> applyOr(first, second, tags, value);
  }

}

