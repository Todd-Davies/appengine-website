package uk.co.todddavies.website;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class RedirectModule  extends ServletModule {
  
  // TODO: Read from config
  private static final ImmutableMap<String, String> REDIRECT_MAP = ImmutableMap.of(
      "/home/", "/#home",
      "/notes/", "/#notes",
      "/contact/", "/#contact");
  
  private RedirectModule() {}
  
  static AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new RedirectModule());
        bind(new TypeLiteral<ImmutableMap<String, String>>() {}).annotatedWith(RedirectMap.class)
            .toInstance(REDIRECT_MAP);
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    for (String key : REDIRECT_MAP.keySet()) {
      serve(key).with(RedirectServlet.class);
    }
  }
  
  @Singleton
  @SuppressWarnings("serial")
  private static final class RedirectServlet extends HttpServlet { 
  
    private final ImmutableMap<String, String> redirectMap;
    
    @Inject
    private RedirectServlet(@RedirectMap ImmutableMap<String, String> redirectMap) {
      this.redirectMap = redirectMap;
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
      String path = req.getRequestURI();
      if (redirectMap.containsKey(path)) {
        resp.sendRedirect(redirectMap.get(path));
      } else {
        resp.sendError(404);
      }
    }
  }
  
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  private @interface RedirectMap {}
}