package uk.co.todddavies.website.closure;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.google.template.soy.SoyModule;

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
        install(new SoyRenderModule());
      }
    };
  }

  @Override
  protected void configureServlets() {
    serve("/").with(MinimalistHomeServlet.class);
    serve("/notes/").with(MinimalistNotesServlet.class);
    serve("/socialmedia/").with(MinimalistSocialMediaServlet.class);
  }
}
