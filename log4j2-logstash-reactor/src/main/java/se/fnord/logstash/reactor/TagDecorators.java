package se.fnord.logstash.reactor;

import se.fnord.taggedmessage.Tags;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TagDecorators {
  public static <T> BiFunction<Tags, T, Tags> withMessage(String message) {
    return (t, v) -> t.add("message", message);
  }

  public static <T> BiFunction<Tags, T, Tags> withMessage(Supplier<String> message) {
    return (t, v) -> t.add("message", message.get());
  }

  public static <T> BiFunction<Tags, T, Tags> withMessage(Function<? super T, String> message) {
    return (t, v) -> t.add("message", message.apply(v));
  }

  public static BiFunction<Tags, Throwable, Tags> errorMessage() {
    return withMessage(Objects::toString);
  }
}
