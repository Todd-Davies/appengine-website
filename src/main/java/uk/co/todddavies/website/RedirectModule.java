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
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class RedirectModule  extends ServletModule {
  
  private static final Logger log = Logger.getLogger(RedirectModule.class.getName());
  
  // TODO: Read from config
  private static final ImmutableMap<String, String> REDIRECT_MAP = ImmutableMap.of(
      "/home", "/#home",
      "/notes", "/#notes",
      "/contact", "/#contact");
  
  private static final String UNKNOWN_PATH_WARNING = "Request to path '%s' was handled by the "
      + "redirect module, but was not registered with a redirect target. Registered targets "
      + "are:\n%s"; 
  
  private static final ImmutableMap<String, String> GENERATED_MAP =
      ImmutableMap.<String, String>builder()
          .putAll(REDIRECT_MAP)
          .putAll(addSlashesToPaths(REDIRECT_MAP))
          .build();
  
  private RedirectModule() {}
  
  static AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new RedirectModule());
        bind(new TypeLiteral<ImmutableMap<String, String>>() {}).annotatedWith(RedirectMap.class)
            .toInstance(GENERATED_MAP);
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    for (String key : GENERATED_MAP.keySet()) {
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
        log.warning(String.format(UNKNOWN_PATH_WARNING,
            path, REDIRECT_MAP.toString()));
        resp.sendError(404);
      }
    }
  }
  
  /**
   * Adds slashes to the paths (keys) of a redirect mapping.
   * 
   * E.g. (/contact, /#contact) will go to (/contact/, #contact);
   */
  private static final ImmutableMap<String, String> addSlashesToPaths(
      ImmutableMap<String, String> input) {
    ImmutableMap.Builder<String, String> output = ImmutableMap.builder();
    for (Entry<String, String> mapping : input.entrySet()) {
      output.put(mapping.getKey() + '/', mapping.getValue());
    }
    return output.build();
  }
  
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  private @interface RedirectMap {}
}