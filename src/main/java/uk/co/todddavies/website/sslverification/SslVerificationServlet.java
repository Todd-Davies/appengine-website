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
    resp.getWriter().print("CmBqIFkRQXtUqLTEZ2UW_05Tu3leGJ2WJIsThPMvoHM.FdQRW4Dc3IShx1jP1m8oPPJcLi"
        + "PD7FLqJSaQD4tgWIQ");
  }
}
