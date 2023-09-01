package se.fnord.logtags.tags;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TagsMatcher implements ArgumentMatcher<Tags> {
  private final List<TagsUtil.Tag> expectedTags;

  public TagsMatcher(List<TagsUtil.Tag> expectedTags) {
    this.expectedTags = List.copyOf(expectedTags);
  }

  @Override
  public boolean matches(Tags argument) {
    var actualTags = TagsUtil.collectTags(argument);
    return expectedTags.equals(actualTags);
  }

  @Override public String toString() {
    return expectedTags.stream()
        .map(Objects::toString)
        .collect(Collectors.joining(", ", "[", "]"));
  }

  public static TagsMatcher of(TagsUtil.Tag ... tags) {
    return new TagsMatcher(List.of(tags));
  }

  public static Tags tagsEq(TagsUtil.Tag ... tags) {
    return Mockito.argThat(of(tags));
  }
}
