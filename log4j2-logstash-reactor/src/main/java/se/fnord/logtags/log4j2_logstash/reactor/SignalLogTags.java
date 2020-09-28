package se.fnord.logtags.log4j2_logstash.reactor;

import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import se.fnord.logtags.log4j2_logstash.taggedmessage.Tags;

import java.util.function.BiFunction;
import java.util.function.Function;

class SignalLogTags<T> implements Function<Signal<? extends T>, Tags> {
  private final Function<Context, Tags> contextTags;
  private final BiFunction<Tags, T, Tags> valueTags;
  private final BiFunction<Tags, Throwable, Tags> errorTags;

  SignalLogTags(Function<Context, Tags> contextTags,
      BiFunction<Tags, T, Tags> valueTags,
      BiFunction<Tags, Throwable, Tags> errorTags) {
    this.contextTags = contextTags;
    this.valueTags = valueTags;
    this.errorTags = errorTags;
  }

  @Override
  public Tags apply(Signal<? extends T> signal) {
    var tags = contextTags.apply(signal.getContext());
    if (signal.hasValue()) {
      tags = valueTags.apply(tags, signal.get());
    }
    if (signal.hasError()) {
      tags = errorTags.apply(tags, signal.getThrowable());
    }
    return tags;
  }
}
