package uk.co.todddavies.website.pages; 

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
@SuppressWarnings("serial")
final class PagesApiServlet extends HttpServlet {
  
  private static final String HOME_URL = "/home/";
  // TODO(td): Discover these automatically?
  private static final ImmutableMap<String, String> PAGES = ImmutableMap.of(
      HOME_URL, "Home",
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
    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    response.put("home", HOME_URL);
    response.put("pages", PAGES);
    resp.getWriter().print(jsonObjectWriter.writeValueAsString(response));
  }
}
