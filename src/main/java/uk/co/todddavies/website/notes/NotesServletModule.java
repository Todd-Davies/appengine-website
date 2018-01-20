package uk.co.todddavies.website.notes;

import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;
import uk.co.todddavies.website.taskqueue.TaskQueueModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Module for serving '{@code /api/notes*}'.
 */
public final class NotesServletModule extends ServletModule {
  
  @VisibleForTesting static final String KEY_PARAMTER_NAME = "key";
  private static final Logger log = Logger.getLogger(NotesServletModule.class.getName());
  
  private final String apiPath;
  
  private NotesServletModule(String apiPath) {
    this.apiPath = apiPath; 
  }
  
  /**
   * Static method for installing the requisite dependencies.
   */
  public static AbstractModule create(final String apiPath) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new NotesServletModule(apiPath));
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    filter(apiPath + "notes-dl*").through(KeyFilter.class);
    serve(apiPath + "notes-dl*").with(NotesApiDownloadServlet.class);
    serve(apiPath + "notes").with(NotesApiServlet.class);
  }
  
  /**
   * Injects {@code @named{"key"} Long key} to requests with a 'key' GET parameter.
   */
  @Singleton
  @VisibleForTesting
  static final class KeyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {/* Not required. */}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
      String keyParameter = request.getParameter(KEY_PARAMTER_NAME);
      Optional<Long> key = parseLong(keyParameter, KEY_PARAMTER_NAME);
      request.setAttribute(
          Key.get(
              new TypeLiteral<Optional<Long>>() {},
              Names.named(KEY_PARAMTER_NAME))
            .toString(),
          key);  
      chain.doFilter(request, response);
    }

    @Override
    public void destroy() {/* Not required. */}
    
    private static Optional<Long> parseLong(String input, String argument) {
      try {
        return Optional.of(Long.valueOf(input));
      } catch (NumberFormatException e) {
        if (input != null && !input.isEmpty()) {
           log.warning(String.format(
               "Parameter for '%s' could not be parsed to a long. Value passed was:\n%s",
               argument, input));
        }
        return Optional.<Long>absent();
      }
    }
  }
  
  @Provides
  @Named("key")
  @RequestScoped Optional<Long> provideKey() {
    throw new IllegalStateException("Notes key is derived from the request.");
  }
}
