package uk.co.todddavies.website.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogVerifier {
  
  final LogHandler logHandler;
  
  LogVerifier(Class<?> targetClass) {
    logHandler = new LogHandler();
    Logger.getLogger(targetClass.getName()).addHandler(logHandler);
  }
  
  public void verify(Class<?> targetClass, Level level, String message) {
    LogRecord record = logHandler.popLogRecord();
    assertThat(record.getLevel(), is(equalTo(level)));
    assertThat(record.getMessage(), is(equalTo(message)));
  }
  
  public ImmutableList<LogRecord> getLog() {
    return ImmutableList.<LogRecord>builder().addAll(logHandler.log).build();
  }
  
  private static class LogHandler extends Handler {
    private final Queue<LogRecord> log = new LinkedList<LogRecord>();
    
    @Override
    public void close() throws SecurityException {/* Not required */}

    @Override
    public void flush() {/* Not required */}

    @Override
    public void publish(LogRecord record) {
      log.add(record);
    }
    
    public LogRecord popLogRecord() {
      return log.remove();
    }
  }
}
