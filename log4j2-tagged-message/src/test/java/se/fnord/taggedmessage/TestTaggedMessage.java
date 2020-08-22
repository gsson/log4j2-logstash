package se.fnord.taggedmessage;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTaggedMessage {

    @Test
    public void testFormatToNoTags() {
        TaggedMessage message = new TaggedMessage(Tags.empty(), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("", sb.toString());
    }

    @Test
    public void testFormatToSingleTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", "b"), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=\"b\"", sb.toString());
    }

    @Test
    public void testFormatToMultiTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", "b", "c", "d"), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=\"b\" c=\"d\"", sb.toString());
    }

    @Test
    public void testGetFormat() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", "b"), null);
        assertEquals("a=\"b\"", message.getFormat());
    }

    @Test
    public void testGetFormattedMessage() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", "b"), null);
        assertEquals("a=\"b\"", message.getFormattedMessage());
    }

    @Test
    public void testNumericTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", 42), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=42", sb.toString());
    }

    @Test
    public void testNullTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", null), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=null", sb.toString());
    }

    @Test
    public void testBooleanTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", true), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=true", sb.toString());
    }


    @Test
    public void testOtherTag() {
        TaggedMessage message = new TaggedMessage(Tags.of("a", LocalDateTime.of(1900, 1, 1, 1, 1)), null);
        StringBuilder sb = new StringBuilder();
        message.formatTo(sb);
        assertEquals("a=\"1900-01-01T01:01\"", sb.toString());
    }
}
