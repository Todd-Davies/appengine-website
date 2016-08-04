package uk.co.todddavies.website.pages;

import com.google.inject.servlet.ServletModule;

public final class PagesServletModule extends ServletModule {
  
  private final String apiPath;
  
  public PagesServletModule(String apiPath) {
    this.apiPath = apiPath;
  }
  
  @Override
  protected void configureServlets() {
    serve(apiPath + "pages").with(PagesApiServlet.class);
  }  
}
