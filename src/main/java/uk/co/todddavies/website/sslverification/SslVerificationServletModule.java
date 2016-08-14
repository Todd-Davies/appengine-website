package uk.co.todddavies.website.sslverification;

import com.google.inject.servlet.ServletModule;

public final class SslVerificationServletModule extends ServletModule {
  
  private final boolean serve;
  
  public SslVerificationServletModule(boolean serve) {
    this.serve = serve;
  }
  
  @Override
  protected void configureServlets() {
    if (serve) {
      serve("*").with(SslVerificationServlet.class);
    }
  }  
}
