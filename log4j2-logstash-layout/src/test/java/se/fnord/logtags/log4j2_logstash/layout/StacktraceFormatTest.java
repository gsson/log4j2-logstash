package se.fnord.logtags.log4j2_logstash.layout;

import org.apache.logging.log4j.core.pattern.NameAbbreviator;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StacktraceFormatTest {
  private String formatToString(StacktraceFormat format, Throwable throwable) {
    var sb = new StringBuilder();
    format.appendThrowable(throwable, sb);
    return sb.toString();
  }
  private String jdkFormatToString(Throwable throwable) {
    var writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  @Test
  public void testMaxLength() {
    var throwable = new IllegalArgumentException("Error");
    throwable.fillInStackTrace();
    var format = new StacktraceFormat(NameAbbreviator.getDefaultAbbreviator(), 15, Integer.MAX_VALUE);

    assertEquals("java.lang.I...\n", formatToString(format, throwable));
    format = new StacktraceFormat(NameAbbreviator.getDefaultAbbreviator(), 1000, Integer.MAX_VALUE);

    assertEquals(1000, formatToString(format, throwable).length());
  }

  @Test
  public void testMaxFrames() {
    var throwable = new IllegalArgumentException("Error");
    throwable.fillInStackTrace();
    var format = new StacktraceFormat(NameAbbreviator.getDefaultAbbreviator(), Integer.MAX_VALUE, 10);

    assertEquals(1 /* head */ + 10 /* stack */ + 1 /* ... x more */, formatToString(format, throwable).split("\n").length);
    format = new StacktraceFormat(NameAbbreviator.getDefaultAbbreviator(), Integer.MAX_VALUE, 20);

    assertEquals(1 /* head */ + 20 /* stack */ + 1 /* ... x more */, formatToString(format, throwable).split("\n").length);
  }

  @Test
  public void testAbbreviation() {
    var throwable = new IllegalArgumentException("Error");
    throwable.fillInStackTrace();
    var format = new StacktraceFormat(NameAbbreviator.getAbbreviator("0"), 30, Integer.MAX_VALUE);

    assertEquals("IllegalArgumentException: ...\n", formatToString(format, throwable));

    format = new StacktraceFormat(NameAbbreviator.getAbbreviator("-1"), 35, Integer.MAX_VALUE);

    assertEquals("lang.IllegalArgumentException: ...\n", formatToString(format, throwable));

    format = new StacktraceFormat(NameAbbreviator.getAbbreviator("1.2"), 35, Integer.MAX_VALUE);

    assertEquals("j.la.IllegalArgumentException: ...\n", formatToString(format, throwable));
  }

  @Test
  public void testJdkDefaultFormatting() {
    var throwable1 = new IllegalArgumentException("Error 1");
    throwable1.fillInStackTrace();
    var throwable2 = new IllegalArgumentException("Error 2", throwable1);
    throwable2.fillInStackTrace();
    var throwable3 = new IllegalArgumentException("Error 3");
    throwable3.fillInStackTrace();
    throwable3.addSuppressed(throwable2);
    var throwable4 = new IllegalArgumentException("Error 4", throwable3);

    var format = new StacktraceFormat(NameAbbreviator.getDefaultAbbreviator(), Integer.MAX_VALUE, Integer.MAX_VALUE);

    assertEquals(jdkFormatToString(throwable1), formatToString(format, throwable1));
    assertEquals(jdkFormatToString(throwable2), formatToString(format, throwable2));
    assertEquals(jdkFormatToString(throwable3), formatToString(format, throwable3));
    assertEquals(jdkFormatToString(throwable4), formatToString(format, throwable4));
  }

}
