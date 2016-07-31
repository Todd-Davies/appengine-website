package uk.co.todddavies.website.pages; 

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
@SuppressWarnings("serial")
final class PagesApiServlet extends HttpServlet {
  
  // TODO(td): Discover these automatically?
  private static final ImmutableMap<String, String> PAGES = ImmutableMap.of(
      "/contact/", "Contact",
      "/notes/", "Notes");
  
  private final ObjectWriter jsonObjectWriter;
  
  @Inject
  private PagesApiServlet(ObjectWriter jsonObjectWriter) {
    this.jsonObjectWriter = jsonObjectWriter;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    resp.getWriter().print(jsonObjectWriter.writeValueAsString(PAGES));
  }
}
