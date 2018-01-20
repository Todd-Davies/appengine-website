package uk.co.todddavies.website.closure;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.tofu.SoyTofu;
import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;
import uk.co.todddavies.website.taskqueue.TaskQueueModule;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Module for serving the closure template powered frontend.
 */
public final class TemplateServletModule extends ServletModule {

  private static final Logger log = Logger.getLogger(TemplateServletModule.class.getName());

  private TemplateServletModule() { }

  /**
   * Static method for installing the requisite dependencies.
   */
  public static AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new SoyModule());
        install(new TemplateServletModule());
      }
    };
  }

  @Override
  protected void configureServlets() {
    serve("/").with(TemplateServlet.class);
    serve("/home*").with(HomeServlet.class);
    serve("/contact*").with(TemplateServlet.class);
    serve("/notes*").with(NotesServlet.class);
  }

  @Provides
  @Singleton
  private SoyTofu provideSoyRenderer() throws IOException {
    SoyFileSet.Builder fileSet = SoyFileSet.builder();
    ServletContext context = getServletContext();
    for (String path : context.getResourcePaths("/WEB-INF/templates/")) {
      fileSet.add(getServletContext().getResource(path));
    }
    return fileSet.build().compileToTofu().forNamespace("todddavies.website");
  }
}
