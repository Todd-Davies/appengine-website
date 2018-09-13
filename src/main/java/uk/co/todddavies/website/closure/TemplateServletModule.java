package uk.co.todddavies.website.closure;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * Module for serving the closure template powered frontend.
 */
public final class TemplateServletModule extends ServletModule {

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
    serve("/").with(MinimalistHomeServlet.class);
    serve("/notes/").with(MinimalistNotesServlet.class);
    serve("/socialmedia/").with(MinimalistSocialMediaServlet.class);
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
