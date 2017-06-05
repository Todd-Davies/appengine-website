package uk.co.todddavies.website.cron.tasks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.todddavies.website.credentials.Credential;
import uk.co.todddavies.website.credentials.Credentials;
import uk.co.todddavies.website.credentials.CredentialsDatastoreInterface;
import uk.co.todddavies.website.cron.tasks.Annotations.TaskId;
import uk.co.todddavies.website.cron.tasks.data.RecurringTask;
import uk.co.todddavies.website.cron.tasks.data.TaskDatastoreInterface;
import uk.co.todddavies.website.cron.tasks.testing.HttpPostMatcher;
import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provides;

import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tests for {@code CronTasksServlet}.
 * 
 * TODO(td): De-duplicate logic in these tests
 */
public class CronTasksServletTest {
  
  private static final String USER_VALUE = "user";
  private static final String KEY_VALUE = "key";
  private static final String TASK_NAME = "testTask";
  private static final String TASK_NOTES = "taskNotes";
  private static final RecurringTask STORED_TASK = 
      RecurringTask.createForTest(TASK_NAME, TASK_NOTES, 999);
  
  @Mock private TaskDatastoreInterface mockTasksInterface = mock(TaskDatastoreInterface.class);
  @Mock private CredentialsDatastoreInterface mockCredentialsInterface = 
      mock(CredentialsDatastoreInterface.class);
  
  private HttpClient testHttpClient;
  private Optional<Long> taskId;
  
  @Inject
  private CronTasksServlet servlet;
  
  @Inject
  @SuppressWarnings("rawtypes")
  Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    taskId = Optional.absent();
    testHttpClient = mock(HttpClient.class);
    Guice.createInjector(
        LogVerifierModule.create(CronTasksServlet.class),
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(CredentialsDatastoreInterface.class).toInstance(mockCredentialsInterface);
            bind(TaskDatastoreInterface.class).toInstance(mockTasksInterface);
          }
          @Provides @TaskId Optional<Long> provideDummyTaskId() { return taskId; }
          @Provides HttpClient provideHttpClient() { return testHttpClient; }
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
    
    HttpParams mockHttpParams = mock(HttpParams.class);
    when(mockHttpParams.setParameter(any(String.class), any(String.class)))
        .thenReturn(mockHttpParams);
    when(testHttpClient.getParams()).thenReturn(mockHttpParams);
    
    taskId = Optional.of(999L);
    when(mockTasksInterface.get(eq(999L))).thenReturn(Optional.of(STORED_TASK));
    
    when(mockCredentialsInterface.get(Credentials.HABITICA_USER))
        .thenReturn(Optional.<Credential>absent());
    when(mockCredentialsInterface.get(Credentials.HABITICA_KEY))
        .thenReturn(Optional.<Credential>absent());
    
    servlet.doGet(mockRequest, mockResponse);

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
    
    HttpParams mockHttpParams = mock(HttpParams.class);
    when(mockHttpParams.setParameter(any(String.class), any(String.class)))
        .thenReturn(mockHttpParams);
    when(testHttpClient.getParams()).thenReturn(mockHttpParams);
    
    // Mock an unauthorised response
    org.apache.http.HttpResponse mockHttpResponse = mock(org.apache.http.HttpResponse.class);
    StatusLine mockStatusLine = mock(StatusLine.class);
    when(mockStatusLine.getStatusCode()).thenReturn(401);
    when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
    when(testHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
    
    taskId = Optional.of(999L);
    when(mockTasksInterface.get(eq(999L))).thenReturn(Optional.of(STORED_TASK));
    
    when(mockCredentialsInterface.get(Credentials.HABITICA_USER))
        .thenReturn(Optional.of(
            Credential.createForTest("Habitica User", "user", Credentials.HABITICA_USER)));
    when(mockCredentialsInterface.get(Credentials.HABITICA_KEY))
    .thenReturn(Optional.of(
        Credential.createForTest("Habitica Key", "key", Credentials.HABITICA_KEY)));
    
    servlet.doGet(mockRequest, mockResponse);

    logVerifiers.get(CronTasksServlet.class)
        .verifyLogContains(Level.WARNING, "API request to Habitica failed");
    verify(mockResponse).sendError(500, "API request to Habitica failed.");
  }
  
  @Test
  public void testHappyCase() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader(eq("X-Appengine-Cron"))).thenReturn("true");
    
    HttpParams mockHttpParams = mock(HttpParams.class);
    when(mockHttpParams.setParameter(any(String.class), any(String.class)))
        .thenReturn(mockHttpParams);
    when(testHttpClient.getParams()).thenReturn(mockHttpParams);
    
    taskId = Optional.of(999L);
    when(mockTasksInterface.get(eq(999L))).thenReturn(Optional.of(STORED_TASK)); 
    
    when(mockCredentialsInterface.get(Credentials.HABITICA_USER))
        .thenReturn(Optional.of(
            Credential.createForTest("Habitica User", USER_VALUE, Credentials.HABITICA_USER)));
    when(mockCredentialsInterface.get(Credentials.HABITICA_KEY))
    .thenReturn(Optional.of(
        Credential.createForTest("Habitica Key", KEY_VALUE, Credentials.HABITICA_KEY)));
    
    HttpPost expectedPost = new HttpPost(CronTasksServlet.HABITICA_ENDPOINT);
    expectedPost.setHeader("x-api-user", USER_VALUE);
    expectedPost.setHeader("x-api-key", KEY_VALUE);
    expectedPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("type", "todo"));
    urlParameters.add(new BasicNameValuePair("notes", TASK_NOTES));
    urlParameters.add(new BasicNameValuePair("text", TASK_NAME));
    expectedPost.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
    
    org.apache.http.HttpResponse mockHttpResponse = mock(org.apache.http.HttpResponse.class);
    StatusLine mockStatusLine = mock(StatusLine.class);
    when(mockStatusLine.getStatusCode()).thenReturn(201);
    when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
    when(testHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
    
    servlet.doGet(mockRequest, mockResponse);

    logVerifiers.get(CronTasksServlet.class).verify(Level.INFO, 
        String.format("Task '%s' processed successfully", STORED_TASK));
    verify(testHttpClient).execute(argThat(HttpPostMatcher.match(expectedPost)));
  }
}
