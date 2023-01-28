package se.fnord.logtags.log4j2_logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;
import org.openjdk.jmh.infra.Blackhole;

public class BlackholeLogger implements Logger {
    private final Blackhole blackhole;
    private final int intLevel;
    private final Level level;

    public BlackholeLogger(Blackhole blackhole, Level level) {
        this.blackhole = blackhole;
        this.intLevel = level.intLevel();
        this.level = level;
    }

    @Override
    public <MF extends MessageFactory> MF getMessageFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isEnabled(Level level) {
        blackhole.consume(level);
        return intLevel >= level.intLevel();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker) {
        blackhole.consume(marker);
        return isEnabled(level);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isEnabled(Level.DEBUG, marker);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isEnabled(Level.ERROR);
    }

    @Override
    public boolean isFatalEnabled() {
        return isEnabled(Level.FATAL);
    }

    @Override
    public boolean isFatalEnabled(Marker marker) {
        return isEnabled(Level.FATAL);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isEnabled(Level.INFO);
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isEnabled(Level.TRACE);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isEnabled(Level.WARN);
    }

    @Override
    public void catching(Level level, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(t);
    }

    @Override
    public void catching(Throwable t) {
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void debug(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void debug(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void debug(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void debug(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void debug(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void debug(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void debug(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void debug(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void debug(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void debug(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void debug(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void debug(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void debug(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void debug(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void debug(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(String message) {
        blackhole.consume(message);
    }

    @Override
    public void debug(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void debug(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void debug(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void debug(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void debug(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void debug(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void debug(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void debug(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entry() {
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entry(Object... params) {
        blackhole.consume(params);
    }

    @Override
    public void error(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void error(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void error(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void error(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void error(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void error(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void error(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void error(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void error(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void error(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void error(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void error(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void error(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void error(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void error(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void error(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(String message) {
        blackhole.consume(message);
    }

    @Override
    public void error(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void error(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void error(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void error(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void error(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void error(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void error(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void error(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void exit() {
    }

    @Override
    @SuppressWarnings("deprecation")
    public <R> R exit(R result) {
        blackhole.consume(result);
        return result;
    }

    @Override
    public void fatal(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void fatal(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void fatal(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void fatal(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void fatal(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void fatal(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void fatal(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void fatal(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void fatal(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void fatal(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void fatal(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void fatal(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void fatal(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void fatal(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(String message) {
        blackhole.consume(message);
    }

    @Override
    public void fatal(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void fatal(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void fatal(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void fatal(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void fatal(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void fatal(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void info(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void info(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void info(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void info(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void info(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void info(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void info(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void info(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void info(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void info(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void info(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void info(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void info(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void info(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void info(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void info(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(String message) {
        blackhole.consume(message);
    }

    @Override
    public void info(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void info(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void info(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void info(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void info(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void info(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void info(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void info(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void log(Level level, Marker marker, Message msg) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void log(Level level, Marker marker, Message msg, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void log(Level level, Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, CharSequence message) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, Object message) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, Marker marker, Object message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, String message) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object... params) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void log(Level level, Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void log(Level level, Marker marker, String message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void log(Level level, Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Message msg) {
        blackhole.consume(level);
        blackhole.consume(msg);
    }

    @Override
    public void log(Level level, Message msg, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, MessageSupplier msgSupplier) {
        blackhole.consume(level);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void log(Level level, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, CharSequence message) {
        blackhole.consume(level);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, CharSequence message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Object message) {
        blackhole.consume(level);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, Object message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, String message) {
        blackhole.consume(level);
        blackhole.consume(message);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void log(Level level, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Supplier<?> msgSupplier) {
        blackhole.consume(level);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void log(Level level, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(level);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void log(Level level, String message, Object p0) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(level);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void printf(Level level, Marker marker, String format, Object... params) {
        blackhole.consume(level);
        blackhole.consume(marker);
        blackhole.consume(format);
        blackhole.consume(params);
    }

    @Override
    public void printf(Level level, String format, Object... params) {
        blackhole.consume(level);
        blackhole.consume(format);
        blackhole.consume(params);
    }

    @Override
    public <T extends Throwable> T throwing(Level level, T t) {
        blackhole.consume(level);
        blackhole.consume(t);
        return t;
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        blackhole.consume(t);
        return t;
    }

    @Override
    public void trace(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void trace(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void trace(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void trace(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void trace(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void trace(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void trace(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void trace(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void trace(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void trace(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void trace(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void trace(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void trace(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void trace(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void trace(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void trace(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(String message) {
        blackhole.consume(message);
    }

    @Override
    public void trace(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void trace(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void trace(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void trace(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void trace(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void trace(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void trace(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void trace(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public EntryMessage traceEntry() {
        return null;
    }

    @Override
    public EntryMessage traceEntry(String format, Object... params) {
        blackhole.consume(format);
        blackhole.consume(params);
        return null;
    }

    @Override
    public EntryMessage traceEntry(Supplier<?>... paramSuppliers) {
        blackhole.consume(paramSuppliers);
        return null;
    }

    @Override
    public EntryMessage traceEntry(String format, Supplier<?>... paramSuppliers) {
        blackhole.consume(format);
        blackhole.consume(paramSuppliers);
        return null;
    }

    @Override
    public EntryMessage traceEntry(Message message) {
        blackhole.consume(message);
        return null;
    }

    @Override
    public void traceExit() {
    }

    @Override
    public <R> R traceExit(R result) {
        blackhole.consume(result);
        return result;
    }

    @Override
    public <R> R traceExit(String format, R result) {
        blackhole.consume(format);
        blackhole.consume(result);
        return result;
    }

    @Override
    public void traceExit(EntryMessage message) {
        blackhole.consume(message);
    }

    @Override
    public <R> R traceExit(EntryMessage message, R result) {
        blackhole.consume(message);
        blackhole.consume(result);
        return result;
    }

    @Override
    public <R> R traceExit(Message message, R result) {
        blackhole.consume(message);
        blackhole.consume(result);
        return result;
    }

    @Override
    public void warn(Marker marker, Message msg) {
        blackhole.consume(marker);
        blackhole.consume(msg);
    }

    @Override
    public void warn(Marker marker, Message msg, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, MessageSupplier msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void warn(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, CharSequence message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void warn(Marker marker, CharSequence message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, Object message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void warn(Marker marker, Object message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, String message) {
        blackhole.consume(marker);
        blackhole.consume(message);
    }

    @Override
    public void warn(Marker marker, String message, Object... params) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void warn(Marker marker, String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void warn(Marker marker, String message, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, Supplier<?> msgSupplier) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
    }

    @Override
    public void warn(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(marker);
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void warn(Message msg) {
        blackhole.consume(msg);
    }

    @Override
    public void warn(Message msg, Throwable t) {
        blackhole.consume(msg);
        blackhole.consume(t);
    }

    @Override
    public void warn(MessageSupplier msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void warn(MessageSupplier msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void warn(CharSequence message) {
        blackhole.consume(message);
    }

    @Override
    public void warn(CharSequence message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(Object message) {
        blackhole.consume(message);
    }

    @Override
    public void warn(Object message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(String message) {
        blackhole.consume(message);
    }

    @Override
    public void warn(String message, Object... params) {
        blackhole.consume(message);
        blackhole.consume(params);
    }

    @Override
    public void warn(String message, Supplier<?>... paramSuppliers) {
        blackhole.consume(message);
        blackhole.consume(paramSuppliers);
    }

    @Override
    public void warn(String message, Throwable t) {
        blackhole.consume(message);
        blackhole.consume(t);
    }

    @Override
    public void warn(Supplier<?> msgSupplier) {
        blackhole.consume(msgSupplier);
    }

    @Override
    public void warn(Supplier<?> msgSupplier, Throwable t) {
        blackhole.consume(msgSupplier);
        blackhole.consume(t);
    }

    @Override
    public void warn(Marker marker, String message, Object p0) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(marker);
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }

    @Override
    public void warn(String message, Object p0) {
        blackhole.consume(message);
        blackhole.consume(p0);
    }

    @Override
    public void warn(String message, Object p0, Object p1) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
    }

    @Override
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        blackhole.consume(message);
        blackhole.consume(p0);
        blackhole.consume(p1);
        blackhole.consume(p2);
        blackhole.consume(p3);
        blackhole.consume(p4);
        blackhole.consume(p5);
        blackhole.consume(p6);
        blackhole.consume(p7);
        blackhole.consume(p8);
        blackhole.consume(p9);
    }
}