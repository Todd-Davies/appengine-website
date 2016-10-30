package uk.co.todddavies.website.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableMap;
import com.google.gdata.util.common.base.Pair;

import org.junit.Assert;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Snoops on the {@code java.util.logging.Logger} output of a class for verification of log output 
 * in tests.
 * 
 * TODO(td): Add support for sequential verification of logs?
 */
public final class LogVerifier {
  
  final LogHandler logHandler;
  
  LogVerifier(Class<?> targetClass, boolean suppressOutput) {
    logHandler = new LogHandler();
    Logger targetLogger = Logger.getLogger(targetClass.getName());
    targetLogger.addHandler(logHandler);
    targetLogger.setUseParentHandlers(!suppressOutput);
  }
  
  @Before
  public void clearLog() {
    logHandler.logs.clear();
  }
  
  /**
   * Verify that a log was present.
   */
  @SuppressWarnings("unchecked")
  public void verify(Level level, String message) {
    assertThat(logHandler.logs.keySet(), contains(Pair.of(level, message)));
  }
  
  /**
   * Verify that a log was present containing part of the message.
   */
  public void verifyLogContains(Level level, String message) {
    for (Entry<Pair<Level, String>, Throwable> log : logHandler.logs.entrySet()) {
      if (log.getKey().getFirst().equals(level) && log.getKey().getSecond().contains(message)) {
        // Passed :)
        return;
      }
    }
    Assert.fail(
        String.format(
            "No log with level '%s' and containing message '%s' was found in the logs.",
            level, message));
  }
  
  /**
   * Verify that a log was present containing part of the message.
   */
  public void verifyLogContainsExceptionMessage(String message) {
    for (Throwable exception : logHandler.logs.values()) {
      if (exception != null && exception.getMessage().contains(message)) {
        // Passed :)
        return;
      }
    }
    Assert.fail(
        String.format("No throwable containing message '%s' was found in the logs.", message));
  }
  
  /**
   * Verify that no logs were created.
   */
  public void verifyNoLogs() {
    assertThat(logHandler.logs.entrySet(), is(empty()));
  }
  
  public ImmutableMap<Pair<Level, String>, Throwable> getLog() {
    return ImmutableMap.<Pair<Level, String>, Throwable>builder().putAll(logHandler.logs).build();
  }
  
  private static class LogHandler extends Handler {
    private final HashMap<Pair<Level, String>, Throwable> logs = new HashMap<>();
    
    @Override
    public void close() throws SecurityException {/* Not required */}

    @Override
    public void flush() {/* Not required */}

    @Override
    public void publish(LogRecord record) {
      logs.put(Pair.of(record.getLevel(), record.getMessage()), record.getThrown());
    }
  }
}
