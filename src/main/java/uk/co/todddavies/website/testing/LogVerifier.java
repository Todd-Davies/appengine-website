package uk.co.todddavies.website.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableSet;
import com.google.gdata.util.common.base.Pair;

import org.junit.Assert;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;
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
  
  LogVerifier(Class<?> targetClass) {
    logHandler = new LogHandler();
    Logger.getLogger(targetClass.getName()).addHandler(logHandler);
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
    assertThat(logHandler.logs, contains(Pair.of(level, message)));
  }
  
  /**
   * Verify that a log was present containing part of the message.
   */
  public void verifyLogContains(Level level, String message) {
    for (Pair<Level, String> log : logHandler.logs) {
      if (log.getFirst().equals(level) && log.getSecond().contains(message)) {
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
   * Verify that no logs were created.
   */
  public void verifyNoLogs() {
    assertThat(logHandler.logs, is(empty()));
  }
  
  public ImmutableSet<Pair<Level, String>> getLog() {
    return ImmutableSet.<Pair<Level, String>>builder().addAll(logHandler.logs).build();
  }
  
  private static class LogHandler extends Handler {
    private final Set<Pair<Level, String>> logs = new HashSet<>();
    
    @Override
    public void close() throws SecurityException {/* Not required */}

    @Override
    public void flush() {/* Not required */}

    @Override
    public void publish(LogRecord record) {
      logs.add(Pair.of(record.getLevel(), record.getMessage()));
    }
  }
}
