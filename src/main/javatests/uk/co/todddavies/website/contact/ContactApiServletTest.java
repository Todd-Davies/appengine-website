package uk.co.todddavies.website.contact;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.todddavies.website.JsonObjectWriterModule;
import uk.co.todddavies.website.contact.Annotations.EasterEggQuestion;
import uk.co.todddavies.website.contact.Annotations.EasterEggRefreshNumber;
import uk.co.todddavies.website.contact.Annotations.EmailAddress;
import uk.co.todddavies.website.contact.captcha.CaptchaQuestion;
import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Test for {@code ContactApiServlet}.
 */
public class ContactApiServletTest {
  
  private static final String TEST_EMAIL = "test@testmail.com";
  private static final CaptchaQuestion TEST_CAPTCHA = CaptchaQuestion.create("Hi", "there");
  private static final CaptchaQuestion EGG_CAPTCHA = CaptchaQuestion.create("Egg", "captcha");
  
  @Inject
  private ContactApiServlet servlet;
  
  @Inject
  @SuppressWarnings("rawtypes")
  Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(String.class).annotatedWith(EmailAddress.class).toInstance(TEST_EMAIL);
            bind(Integer.class).annotatedWith(EasterEggRefreshNumber.class).toInstance(5);
            bind(CaptchaQuestion.class)
                .annotatedWith(EasterEggQuestion.class)
                .toInstance(EGG_CAPTCHA);
            bind(CaptchaQuestion.class).toInstance(TEST_CAPTCHA);
          }
        },
        new JsonObjectWriterModule(),
        LogVerifierModule.create(ContactApiServlet.class)
        ).injectMembers(this);
  }
 
  @Test
  public void testHappyCase() throws IOException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    // TODO(td): Replace this with a fake session implementation 
    HttpSession mockSession = mock(HttpSession.class);
    when(mockRequest.getSession()).thenReturn(mockSession);
    
    servlet.doGet(mockRequest, mockResponse);
    
    verify(mockWriter).print(any(String.class));
    logVerifiers.get(ContactApiServlet.class).verifyNoLogs();
    
    when(mockSession.getAttribute(ContactApiServlet.SESSION_CONTACT_PRESSES)).thenReturn(5);
    servlet.doGet(mockRequest, mockResponse);
    
    logVerifiers.get(ContactApiServlet.class)
        .verifyLogContains(Level.INFO, "User pressed the contact button 5 times; easter egg activated!");
  }
}
