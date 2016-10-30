package uk.co.todddavies.website.cron.tasks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import uk.co.todddavies.website.credentials.Credential;
import uk.co.todddavies.website.credentials.Credentials;
import uk.co.todddavies.website.credentials.CredentialsDatastoreInterface;
import uk.co.todddavies.website.cron.tasks.Annotations.TaskId;
import uk.co.todddavies.website.cron.tasks.data.RecurringTask;
import uk.co.todddavies.website.cron.tasks.data.TaskDatastoreInterface;
import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test for {@code CronTasksServlet}.
 */
public class CronTasksServletTest {
  
  @Mock private CredentialsDatastoreInterface mockCredentialsInterface = 
      mock(CredentialsDatastoreInterface.class);
  @Mock private TaskDatastoreInterface mockTasksInterface = 
      mock(TaskDatastoreInterface.class);
  
  private static final ImmutableMap<Long, RecurringTask> TASK_STORAGE = 
      ImmutableMap.of(999L, RecurringTask.createForTest("testTask", "testNotes", 999));
  
  @Inject
  private CronTasksServlet servlet;
  
  private Optional<Long> taskId;
  
  @Inject
  @SuppressWarnings("rawtypes")
  Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    taskId = Optional.absent();
    Guice.createInjector(
        LogVerifierModule.create(CronTasksServlet.class),
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(CredentialsDatastoreInterface.class).toInstance(mockCredentialsInterface);
            bind(TaskDatastoreInterface.class).toInstance(mockTasksInterface);
          }
          @Provides @TaskId Optional<Long> provideDummyTaskId() { return taskId; }
        }).injectMembers(this);
  }
  
  @Test
  public void testNotCalledByCron() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    servlet.doGet(mock(HttpServletRequest.class), mockResponse);
    
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContains(Level.WARNING, "Not called from Cron.");
    verify(mockResponse).setStatus(401);
  }
  
  @Test
  public void testNoId() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader(eq("X-Appengine-Cron"))).thenReturn("true");
    
    taskId = Optional.absent();
    
    servlet.doGet(mockRequest, mockResponse);
    
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContains(Level.WARNING, "Task ID not supplied to endpoint");
    verify(mockResponse).setStatus(200);
  }
  
  @Test
  public void testNotFoundId() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader(eq("X-Appengine-Cron"))).thenReturn("true");
    
    taskId = Optional.of(123L);
    when(mockTasksInterface.get(eq(123L))).thenReturn(Optional.<RecurringTask>absent());
    
    servlet.doGet(mockRequest, mockResponse);
    
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContains(Level.WARNING, "Task with ID 123 not found!");
    verify(mockResponse).setStatus(200);
  }
  
  @Test
  public void testNoHabiticaCredentials() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader(eq("X-Appengine-Cron"))).thenReturn("true");
    
    taskId = Optional.of(999L);
    when(mockTasksInterface.get(eq(999L))).thenReturn(Optional.of(TASK_STORAGE.get(999L)));
    
    when(mockCredentialsInterface.get(Credentials.HABITICA_USER))
        .thenReturn(Optional.<Credential>absent());
    
    servlet.doGet(mockRequest, mockResponse);

    System.out.println(logVerifiers.get(CronTasksServlet.class).getLog());
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContains(Level.WARNING, "API request to Habitica failed");
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContainsExceptionMessage("Unable to find key for credential ID");
    verify(mockResponse).sendError(500, "API request to Habitica failed.");
  }
  
  @Test
  public void testHabiticaApiRequestFailed() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader(eq("X-Appengine-Cron"))).thenReturn("true");
    
    taskId = Optional.of(999L);
    when(mockTasksInterface.get(eq(999L))).thenReturn(Optional.of(TASK_STORAGE.get(999L)));
    
    when(mockCredentialsInterface.get(Credentials.HABITICA_USER))
        .thenReturn(Optional.of(
            Credential.createForTest("Habitica User", "user", Credentials.HABITICA_USER)));
    when(mockCredentialsInterface.get(Credentials.HABITICA_KEY))
    .thenReturn(Optional.of(
        Credential.createForTest("Habitica Key", "key", Credentials.HABITICA_KEY)));
    
    servlet.doGet(mockRequest, mockResponse);
    System.out.println(logVerifiers.get(CronTasksServlet.class).getLog());
    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContainsExceptionMessage("Habitica API returned an invalid response");
    verify(mockResponse).sendError(500, "API request to Habitica failed.");
  }
}
