package uk.co.todddavies.website.pages;

import com.google.inject.servlet.ServletModule;

public final class PagesServletModule extends ServletModule {
  
  public PagesServletModule() {}
  
  @Override
  protected void configureServlets() {
    serve("/api/pages").with(PagesApiServlet.class);
  }  
}
