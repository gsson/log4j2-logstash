package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.util.MessageSupplier;
import org.mockito.ArgumentMatcher;
import se.fnord.logtags.log4j2_logstash.taggedmessage.TaggedMessage;
import se.fnord.logtags.tags.TagsUtil;

import javax.annotation.Nullable;
import java.util.List;

import static se.fnord.logtags.tags.TagsUtil.collectTags;

public class TaggedMessageMatcher implements ArgumentMatcher<TaggedMessage> {
  @Nullable
  private final Class<? extends Throwable> expectedThrowable;
  private final List<TagsUtil.Tag> expectedMessageTags;

  private TaggedMessageMatcher(@Nullable Class<? extends Throwable> expectedThrowable, TagsUtil.Tag... tags) {
    this.expectedThrowable = expectedThrowable;
    this.expectedMessageTags = List.of(tags);
  }

  private boolean throwableMatches(@Nullable Throwable throwable) {
    if (expectedThrowable == null) {
      return throwable == null;
    } else {
      return this.expectedThrowable.isInstance(throwable);
    }
  }

  @Override
  public boolean matches(TaggedMessage message) {
    var isExpectedThrowable = throwableMatches(message.getThrowable());
    var tagsAreEqual = expectedMessageTags.equals(collectTags(message.getTags()));

    return isExpectedThrowable && tagsAreEqual;
  }

  @Override
  public String toString() {
    return "TaggedMessageMatcher{throwable=" + expectedThrowable + ", tags=" + expectedMessageTags + '}';
  }

  public static ArgumentMatcher<TaggedMessage> valueMessage(TagsUtil.Tag... tags) {
    return new TaggedMessageMatcher(null, tags);
  }

  public static ArgumentMatcher<MessageSupplier> valueMessageSupplier(TagsUtil.Tag... tags) {
    var messageMatcher = valueMessage(tags);
    return messageSupplier -> messageMatcher.matches((TaggedMessage) messageSupplier.get());
  }

  public static ArgumentMatcher<TaggedMessage> errorMessage(Class<? extends Throwable> t, TagsUtil.Tag... tags) {
    return new TaggedMessageMatcher(t, tags);
  }

  public static ArgumentMatcher<MessageSupplier> errorMessageSupplier(Class<? extends Throwable> t, TagsUtil.Tag... tags) {
    var messageMatcher = errorMessage(t, tags);
    return messageSupplier -> messageMatcher.matches((TaggedMessage) messageSupplier.get());
  }
}
