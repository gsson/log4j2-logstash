package se.fnord.logtags.tags;

public class TagToString {
  private static final ToStringConsumer CONSUMER = new ToStringConsumer();

  private static void appendStart(CharSequence key, StringBuilder stringBuilder) {
    if (stringBuilder.length() > 1) {
      stringBuilder.append(", ");
    }
    stringBuilder.append("<");
    quoteText(stringBuilder, key);
    stringBuilder.append(": ");
  }

  private static void appendEnd(StringBuilder stringBuilder) {
    stringBuilder.append(">");
  }

  private static void quoteText(StringBuilder stringBuilder, CharSequence value) {
    stringBuilder.ensureCapacity(stringBuilder.capacity() + value.length() + 2);
    stringBuilder.append('"');
    var it = value.codePoints().iterator();
    while (it.hasNext()) {
      var c = it.nextInt();
      switch (c) {
      case '"' -> stringBuilder.append("\\\"");
      case '\\' -> stringBuilder.append("\\\\");
      case '\b' -> stringBuilder.append("\\b");
      case '\f' -> stringBuilder.append("\\f");
      case '\n' -> stringBuilder.append("\\n");
      case '\r' -> stringBuilder.append("\\r");
      case '\t' -> stringBuilder.append("\\t");
      default -> {
        if (Character.isISOControl(c)) {
          stringBuilder.append(String.format("\\u%04x", c));
        } else {
          stringBuilder.appendCodePoint(c);
        }
      }
      }

    }
    stringBuilder.append('"');
  }

  private static class ToStringConsumer implements TagConsumer<StringBuilder> {

    @Override
    public void textTag(CharSequence key, CharSequence value, StringBuilder stringBuilder) {
      appendStart(key, stringBuilder);
      quoteText(stringBuilder, value);
      appendEnd(stringBuilder);
    }

    @Override
    public void longTag(CharSequence key, long value, StringBuilder stringBuilder) {
      appendStart(key, stringBuilder);
      stringBuilder.append(value);
      appendEnd(stringBuilder);
    }

    @Override
    public void booleanTag(CharSequence key, boolean value, StringBuilder stringBuilder) {
      appendStart(key, stringBuilder);
      stringBuilder.append(value);
      appendEnd(stringBuilder);
    }

    @Override
    public void doubleTag(CharSequence key, double value, StringBuilder stringBuilder) {
      appendStart(key, stringBuilder);
      stringBuilder.append(value);
      appendEnd(stringBuilder);
    }

    @Override
    public void nullTag(CharSequence key, StringBuilder stringBuilder) {
      appendStart(key, stringBuilder);
      stringBuilder.append("null");
      appendEnd(stringBuilder);
    }
  }

  public static String toString(Tags tags) {
    var collector = new StringBuilder();
    collector.append("[");
    tags.forEach(collector, CONSUMER);
    collector.append("]");
    return collector.toString();
  }
}
