package se.fnord.logstash.reactor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.core.LifeCycle.State.STARTED;

public class LoggerTestExtension implements ParameterResolver {
  private static class TestConfiguration extends NullConfiguration {
    private final TestAppender appender = new TestAppender();
    @Override
    protected void setToDefault() {
      addAppender(appender);
      var rootLogger = getRootLogger();
      rootLogger.addAppender(appender, null, null);
      rootLogger.setLevel(Level.ALL);
    }

    public TestAppender testAppender() {
      return appender;
    }
  }

  private static class TestAppender implements Appender {
    private final ConcurrentLinkedQueue<LogEvent> recordedLogEvents = new ConcurrentLinkedQueue<>();
    private ErrorHandler errorHandler;

    public List<LogEvent> getLogEvents() {
      return recordedLogEvents.stream()
          .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void append(LogEvent event) {
      recordedLogEvents.add(event.toImmutable());
    }

    @Override
    public String getName() {
      return "testAppender";
    }

    @Override
    public Layout<? extends Serializable> getLayout() {
      return null;
    }

    @Override
    public boolean ignoreExceptions() {
      return false;
    }

    @Override
    public ErrorHandler getHandler() {
      return errorHandler;
    }

    @Override
    public void setHandler(ErrorHandler handler) {
      this.errorHandler = handler;
    }

    @Override
    public State getState() {
      return STARTED;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
      return true;
    }

    @Override
    public boolean isStopped() {
      return false;
    }
  }

  public static class TestLogManager {
    private final LoggerContext loggerContext;
    private final TestAppender appender;

    public TestLogManager(LoggerContext loggerContext, TestAppender appender) {
      this.loggerContext = loggerContext;
      this.appender = appender;
    }

    public Logger getLogger(String name) {
      return loggerContext.getLogger(name);
    }

    public List<LogEvent> getLogEvents() {
      return appender.getLogEvents();
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return TestLogManager.class.isAssignableFrom(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    var loggerContext = new LoggerContext("testContext");
    var configuration = new TestConfiguration();
    loggerContext.start(configuration);

    return new TestLogManager(loggerContext, configuration.testAppender());
  }
}
