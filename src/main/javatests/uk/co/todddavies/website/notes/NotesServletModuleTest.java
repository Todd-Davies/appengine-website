package uk.co.todddavies.website.notes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.todddavies.website.notes.NotesServletModule.KeyFilter;
import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test for {@code NotesApiServlet}.
 */
public class NotesServletModuleTest {
  
  private KeyFilter keyFilter;
  
  @Inject
  @SuppressWarnings("rawtypes")
  private Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    keyFilter = new KeyFilter();
    
    Guice.createInjector(LogVerifierModule.create(NotesServletModule.class)).injectMembers(this);
  }
 
  @Test
  public void testHappyCase() throws IOException, ServletException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getParameter(NotesServletModule.KEY_PARAMTER_NAME)).thenReturn("50");
    
    keyFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mock(FilterChain.class));
    
    String expectedKey = Key.get(
        new TypeLiteral<Optional<Long>>() {},
        Names.named(NotesServletModule.KEY_PARAMTER_NAME)).toString();
    
    verify(mockRequest).setAttribute(expectedKey, Optional.<Long>of(50L));
  }
  
  @Test
  public void testNullCase() throws IOException, ServletException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getParameter(NotesServletModule.KEY_PARAMTER_NAME)).thenReturn(null);
    
    keyFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mock(FilterChain.class));
    
    String expectedKey = Key.get(
        new TypeLiteral<Optional<Long>>() {},
        Names.named(NotesServletModule.KEY_PARAMTER_NAME)).toString();
    
    verify(mockRequest).setAttribute(expectedKey, Optional.<Long>absent());
  }
  
  @Test
  public void testEmptyCase() throws IOException, ServletException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getParameter(NotesServletModule.KEY_PARAMTER_NAME)).thenReturn("");
    
    keyFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mock(FilterChain.class));
    
    String expectedKey = Key.get(
        new TypeLiteral<Optional<Long>>() {},
        Names.named(NotesServletModule.KEY_PARAMTER_NAME)).toString();
    
    verify(mockRequest).setAttribute(expectedKey, Optional.<Long>absent());
  }
  
  
  @Test
  public void testErrorCase() throws IOException, ServletException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getParameter(NotesServletModule.KEY_PARAMTER_NAME))
        .thenReturn("myMaliciousString");
    
    keyFilter.doFilter(mockRequest, mock(HttpServletResponse.class), mock(FilterChain.class));
    
    String expectedKey = Key.get(
        new TypeLiteral<Optional<Long>>() {},
        Names.named(NotesServletModule.KEY_PARAMTER_NAME)).toString();
    
    verify(mockRequest).setAttribute(expectedKey, Optional.<Long>absent());
    logVerifiers.get(NotesServletModule.class)
    .verifyLogContains(Level.WARNING, 
        "Parameter for 'key' could not be parsed to a long. Value passed was:\nmyMaliciousString");
  }
}
