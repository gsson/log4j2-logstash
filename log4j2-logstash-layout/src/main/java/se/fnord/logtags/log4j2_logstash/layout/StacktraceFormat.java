package se.fnord.logtags.log4j2_logstash.layout;

import org.apache.logging.log4j.core.pattern.NameAbbreviator;

import java.util.IdentityHashMap;

public class StacktraceFormat {
  private final NameAbbreviator nameAbbreviator;
  private final int maxLength;
  private final int maxFrames;

  public StacktraceFormat(NameAbbreviator nameAbbreviator, int maxLength, int maxFrames) {
    this.nameAbbreviator = nameAbbreviator;
    this.maxLength = Math.max(4, maxLength);
    this.maxFrames = Math.max(2, maxFrames);
  }

  private class Formatter {
    private final StringBuilder textBuilder;
    private final IdentityHashMap<Throwable, Boolean> handled;
    private final int startPosition;

    private Formatter(StringBuilder textBuilder) {
      this.textBuilder = textBuilder;
      this.startPosition = textBuilder.length();
      this.handled = new IdentityHashMap<>();
    }

    boolean checkLength() {
      return textBuilder.length() - startPosition >= maxLength;
    }

    private void appendHeader(int indent, String prefix, Throwable throwable) {
      indent(indent);
      textBuilder.append(prefix);
      nameAbbreviator.abbreviate(throwable.getClass().getName(), textBuilder);

      var message = throwable.getLocalizedMessage();
      if (message != null) {
        textBuilder.append(": ");
        textBuilder.append(message);
      }
      textBuilder.append("\n");
    }

    private void indent(int indent) {
      textBuilder.append("\t".repeat(indent));
    }

    private void appendFileLocation(StackTraceElement element) {
      if (element.isNativeMethod()) {
        textBuilder.append("(Native Method)");
        return;
      }

      var fileName = element.getFileName();
      if (fileName == null) {
        textBuilder.append("(Unknown Source)");
        return;
      }

      textBuilder
          .append("(")
          .append(fileName);
      var lineNumber = element.getLineNumber();

      if (lineNumber >= 0) {
        textBuilder
            .append(":")
            .append(lineNumber);
      }
      textBuilder.append(")");
    }

    private void appendStackTraceElement(int indent, StackTraceElement element) {
      indent(indent);

      textBuilder.append("at ");
      if (element.getModuleName() != null) {
        nameAbbreviator.abbreviate(element.getModuleName(), textBuilder);
        textBuilder.append('/');
      }
      nameAbbreviator.abbreviate(element.getClassName(), textBuilder);
      textBuilder.append('.');
      textBuilder.append(element.getMethodName());
      appendFileLocation(element);
      textBuilder.append('\n');
    }

    private int appendUntil(StackTraceElement[] elements, StackTraceElement[] parentElements) {
      var maxCommon = Math.min(elements.length, parentElements.length);
      if (maxCommon == 0) {
        return Math.min(maxFrames, elements.length);
      }

      var elementIndex = elements.length - 1;
      var parentElementIndex = parentElements.length - 1;
      while (elementIndex >= 0 && elements[elementIndex].equals(parentElements[parentElementIndex])) {
        elementIndex--;
        parentElementIndex--;
      }
      var appendUntil = Math.min(elementIndex + 1, maxFrames);
      if (appendUntil == elements.length - 1) {
        return elements.length;
      }
      return appendUntil;
    }

    private void appendStackTraceElements(int indent, StackTraceElement[] elements,
        StackTraceElement[] parentElements) {
      var appendUntil = appendUntil(elements, parentElements);
      for (var i = 0; i < appendUntil; i++) {
        if (checkLength()) {
          return;
        }
        appendStackTraceElement(indent, elements[i]);
      }
      if (appendUntil < elements.length) {
        indent(indent);
        textBuilder.append("... ");
        textBuilder.append(elements.length - appendUntil);
        textBuilder.append(" more\n");
      }
    }

    private void adjustLength() {
      if (textBuilder.length() - startPosition > maxLength) {
        textBuilder.setLength(startPosition + maxLength - 4);
        textBuilder.append("...\n");
      }
    }

    public void appendThrowable(int indent, String prefix, Throwable throwable, StackTraceElement[] parentElements) {
      if (checkLength()) {
        return;
      }

      if (handled.put(throwable, Boolean.TRUE) == null) {
        var stackTraceElements = throwable.getStackTrace();
        appendHeader(indent, prefix, throwable);
        appendStackTraceElements(indent + 1, stackTraceElements, parentElements);
        var allSuppressed = throwable.getSuppressed();
        for (var suppressed : allSuppressed) {
          appendThrowable(indent + 1, "Suppressed: ", suppressed, stackTraceElements);
        }
        var cause = throwable.getCause();
        if (cause != null) {
          appendThrowable(indent, "Caused by: ", throwable.getCause(), stackTraceElements);
        }
      }
    }

    public void appendThrowable(Throwable throwable) {
      appendThrowable(0, "", throwable, new StackTraceElement[0]);
      adjustLength();
    }
  }

  public void appendThrowable(Throwable throwable, StringBuilder textBuilder) {
    var formatter = new Formatter(textBuilder);
    formatter.appendThrowable(throwable);
  }

}
