package uk.co.todddavies.website.sslverification; 

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
@SuppressWarnings("serial")
final class SslVerificationServlet extends HttpServlet {
  
  @Inject
  private SslVerificationServlet() {}
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    resp.getWriter().print("J0w3Dh5wav3etnz_lEo6SSVdxCGFpHpcEdkagw9KtFM.OnhvgC6zwPgFskHEs3uAT67O8"
        + "c5ENzP5hQxOB5o7gk0");
  }
}
