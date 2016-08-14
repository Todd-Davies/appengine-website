package uk.co.todddavies.website.sslverification;

import com.google.inject.servlet.ServletModule;

public final class SslVerificationServletModule extends ServletModule {
  
  public SslVerificationServletModule() {}
  
  @Override
  protected void configureServlets() {
    serve(".well-known/acme-challenge/J0w3Dh5wav3etnz_lEo6SSVdxCGFpHpcEdkagw9KtFM")
        .with(SslVerificationServlet.class);
  }  
}
