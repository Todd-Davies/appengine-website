package uk.co.todddavies.website.notes;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;

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

/**
 * Module for serving '{@code /api/notes*}'.
 */
public final class NotesServletModule extends ServletModule {
  
  private final String apiPath;
  
  private NotesServletModule(String apiPath) {
    this.apiPath = apiPath; 
  }
  
  /**
   * Static method for installing the requisite dependencies
   */
  public static AbstractModule create(final String apiPath) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new NotesServletModule(apiPath));
        install(new MemcacheModule());
        install(new NotesDatastoreModule());
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
  private static final class KeyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
      String keyParameter = request.getParameter("key");
      Optional<Long> key = keyParameter == null 
          ? Optional.<Long>absent()
          : Optional.of(Long.valueOf(keyParameter));
      request.setAttribute(
          Key.get(
              new TypeLiteral<Optional<Long>>() {},
              Names.named("key"))
            .toString(),
          key);  
      chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
  }
  
  @Provides
  @Named("key")
  @RequestScoped Optional<Long> provideKey() {
    throw new IllegalStateException("Notes key is derived from the request.");
  }
}
