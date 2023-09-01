package se.fnord.logtags.log4j2_logstash.reactor;

import se.fnord.logtags.tags.Tags;
import se.fnord.logtags.tags.ToTags;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValueTagsBuilder<T> {
  @Nullable
  private final Predicate<? super T> condition;

  @Nullable
  private final ValueTagsFunction<? super T> tagsFunction;

  @Nullable
  private final ValueTagsBuilder<T> parent;

  ValueTagsBuilder(@Nullable Predicate<? super T> condition,
      @Nullable ValueTagsFunction<? super T> tagsFunction, @Nullable ValueTagsBuilder<T> parent) {
    this.condition = condition;
    this.tagsFunction = tagsFunction;
    this.parent = parent;
  }

  private <TT extends ToTags> ValueTagsBuilder<T> withTags(ValueTagsFunction<? super T> tags) {
    return new ValueTagsBuilder<>(condition, tags, parent);
  }

  public static <T> ValueTagsBuilder<T> create() {
    return new ValueTagsBuilder<>(null, null, null);
  }

  public static <T> ValueTagsBuilder<T> conditional(Predicate<? super T> condition) {
    return new ValueTagsBuilder<>(condition, null, null);
  }

  public ValueTagsBuilder<T> tags(ValueTagsFunction<? super T> tags) {
    return withTags(tags);
  }

  public static <T> ValueTagsBuilder<T> when(Class<? extends T> clazz,
      ValueTagsFunction<? super T> tags) {
    return new ValueTagsBuilder<>(clazz::isInstance, tags, null);
  }

  public ValueTagsBuilder<T> when(Class<? extends T> clazz) {
    return new ValueTagsBuilder<>(clazz::isInstance, null, null);
  }

  public ValueTagsBuilder<T> when(Predicate<? super T> condition) {
    return new ValueTagsBuilder<>(condition, null, null);
  }

  public ValueTagsBuilder<T> when(Predicate<? super T> condition, ValueTagsFunction<? super T> tags) {
    return new ValueTagsBuilder<>(condition, tags, null);
  }

  public ValueTagsBuilder<T> orWhen(Class<? extends T> clazz) {
    return new ValueTagsBuilder<>(clazz::isInstance, null, this);
  }

  public ValueTagsBuilder<T> orWhen(Predicate<? super T> condition) {
    return new ValueTagsBuilder<>(condition, null, this);
  }

  public ValueTagsBuilder<T> orElse() {
    return new ValueTagsBuilder<>(null, null, this);
  }

  public ValueTagsBuilder<T> message(String message) {
    return withTags(ValueTagOps.message(message));
  }

  public ValueTagsBuilder<T> message(Function<? super T, String> message) {
    return withTags(ValueTagOps.add("message", message));
  }

  public ValueTagsBuilder<T> message(String message, Object... args) {
    return withTags(ValueTagOps.message(message, args));
  }

  public ValueTagsBuilder<T> valueToString() {
    return withTags(ValueTagOps.valueToString());
  }

  public ValueTagsBuilder<T> add(String tag, String value) {
    return withTags(ValueTagOps.add(tag, value));
  }

  public ValueTagsBuilder<T> add(String tag, Function<? super T, Object> valueMapper) {
    return withTags(ValueTagOps.add(tag, valueMapper));
  }

  @Nullable
  private static <U> ValueTagsFunction<? super U> buildTagsFunction(ValueTagsBuilder<U> builder) {
    if (builder == null || builder.tagsFunction == null) {
      return null;
    }
    if (builder.condition == null) {
      return builder.tagsFunction;
    }
    return ValueTagOps.when(builder.condition, builder.tagsFunction);

  }

  public BiFunction<Tags, ? super T, ? extends ToTags> build() {
    var tagsFunction = buildTagsFunction(this);
    var parentTagsFunction = buildTagsFunction(this.parent);
    return ValueTagOps.or(parentTagsFunction, tagsFunction);
  }
}
