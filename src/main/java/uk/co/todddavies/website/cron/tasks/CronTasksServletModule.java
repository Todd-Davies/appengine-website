package uk.co.todddavies.website.cron.tasks;

import uk.co.todddavies.website.cron.tasks.Annotations.TaskId;
import uk.co.todddavies.website.cron.tasks.data.TasksDatastoreModule;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class CronTasksServletModule extends ServletModule {
  
  private static final String CRON_PATH = "/cron/tasks/*";
  
  private CronTasksServletModule() {/* Private constructor */}
  
  /**
   * Static method for installing the requisite dependencies.
   */
  public static AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new CronTasksServletModule());
        install(new TasksDatastoreModule());
        // Additional modules go here
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    filter(CRON_PATH).through(TaskIdFilter.class);
    serve(CRON_PATH).with(CronTasksServlet.class);
  }
  
  /**
   * Injects {@code @named{"id"} Long id} to requests of the format /cron/tasks/?id={id}
   */
  @Singleton
  private static final class TaskIdFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {/* Not required. */}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
      String keyParameter = request.getParameter("id");
      Optional<Long> key = keyParameter == null 
          ? Optional.<Long>absent()
          : Optional.of(Long.valueOf(keyParameter));
      request.setAttribute(
          Key.get(
              new TypeLiteral<Optional<Long>>() {},
              TaskId.class)
            .toString(),
          key);  
      chain.doFilter(request, response);
    }

    @Override
    public void destroy() {/* Not required. */}
  }
  
  @Provides
  @TaskId
  @RequestScoped Optional<Long> provideKey() {
    throw new IllegalStateException("Task id is derived from the request.");
  }
}
