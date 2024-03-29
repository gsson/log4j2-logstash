package se.fnord.logtags.log4j2_logstash.taggedmessage;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Chars;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import se.fnord.logtags.tags.TagConsumer;
import se.fnord.logtags.tags.Tags;

@AsynchronouslyFormattable
public class TaggedMessage implements Message, StringBuilderFormattable {
    private static final TagConsumer<StringBuilder> SIMPLE_RENDERER = new TagConsumer<>() {
        private void prepareForAppend(StringBuilder sb, int approximateLength) {
            if (sb.length() > 0) {
                sb.ensureCapacity(approximateLength + 1);
                sb.append(' ');
            } else {
                sb.ensureCapacity(approximateLength);
            }
        }

        @Override
        public void textTag(CharSequence key, CharSequence value, StringBuilder sb) {
            prepareForAppend(sb, key.length() + value.length() + 3);
            sb.append(key)
                .append(Chars.EQ)
                .append(Chars.DQUOTE)
                .append(value)
                .append(Chars.DQUOTE);
        }

        @Override
        public void longTag(CharSequence key, long value, StringBuilder sb) {
            prepareForAppend(sb, key.length() + 5);
            sb.append(key)
                .append(Chars.EQ)
                .append(value);
        }

        @Override
        public void booleanTag(CharSequence key, boolean value, StringBuilder sb) {
            prepareForAppend(sb, key.length() + 5);
            sb.append(key)
                .append(Chars.EQ)
                .append(value);
        }

        @Override
        public void doubleTag(CharSequence key, double value, StringBuilder sb) {
            prepareForAppend(sb, key.length() + 5);
            sb.append(key)
                .append(Chars.EQ)
                .append(value);
        }

        @Override
        public void nullTag(CharSequence key, StringBuilder sb) {
            prepareForAppend(sb, key.length() + 5);
            sb.append(key).append("=null");
        }
    };
    private static final long serialVersionUID = 1L;
    private final Tags tags;
    @Nullable
    private final Throwable throwable;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "This is done according to the API spec")
    public TaggedMessage(Tags tags, @Nullable Throwable throwable) {
        this.tags = tags;
        this.throwable = throwable;
    }

    private static void renderTags(StringBuilder sb, Tags tags) {
        tags.forEach(sb, SIMPLE_RENDERER);
    }

    public Tags getTags() {
        return tags;
    }

    @Override
    public String getFormattedMessage() {
        StringBuilder buffer = new StringBuilder();
        renderTags(buffer, tags);
        return buffer.toString();
    }

    @Override
    public String getFormat() {
        return getFormattedMessage();
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "This is done according to the API spec")
    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void formatTo(StringBuilder buffer) {
        renderTags(buffer, tags);
    }
}
