import re


RE = re.compile(r'''
    ^\s+
    ((?:
        (?:(?:<[^>]+>)|\w+)\s+
    )*)
    (\w+\s+\w+)\s*
    \(
        ((?:\w+\s+)*[\w<>?\.]+\s+\w+(?:\s*,\s*(?:\s+)*[\w<>?\.]+\s+\w+)*)?
    \);$
''', flags=re.MULTILINE + re.VERBOSE)

TPARAM_RE = re.compile(r'(?:<[^>]+>)')

SKIP_RE = re.compile(r'^is\w*|get\w+$')

text = open('Logger.java', 'r').read()

with open('BlackholeLogger.java', 'w') as out:

    out.write('''package se.fnord.logtags.log4j2_logstash.reactor;

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
''')

    for match in RE.finditer(text):
        tparams = TPARAM_RE.findall(match.group(1))
        (rt, name) = match.group(2).split()
        if SKIP_RE.match(name):
            continue
        params = match.group(3)
        params = [p.split() for p in params.split(',')] if params else []

        out.write('\n    @Override\n')
        if tparams:
            out.write('    public {} {} {}('.format(' '.join(tparams), rt, name))
        else:
            out.write('    public {} {}('.format(rt, name))
        out.write(', '.join([' '.join(p) for p in params]))
        out.write(') {\n')
        for (_, pname) in params:
            out.write('        blackhole.consume({});\n'.format(pname))
        if rt != 'void':
            
            ret = [pname for (ptype, pname) in params if ptype == rt] or ['null']
            out.write('        return {};\n'.format(ret[0]))
        out.write('    }\n')


    out.write('}')
