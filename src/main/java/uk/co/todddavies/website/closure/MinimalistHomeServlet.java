package uk.co.todddavies.website.closure;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
final class MinimalistHomeServlet extends HttpServlet {

  private static final String EXPECTED_PATH = "/minimal/";
  private static final String TEMPLATE_NAME = ".minimalisthome";

  private static final long BIRTH_MILLIS = 802224000000L;
  private static final long YEAR_MILLIS = 31556952000L;
  private static final SoyMapData HOME_DATA = new SoyMapData("age", (System.currentTimeMillis() - BIRTH_MILLIS) / YEAR_MILLIS);

  private final SoyTofu soyTofu;

  @Inject
  private MinimalistHomeServlet(SoyTofu soyTofu) {
    this.soyTofu= soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (EXPECTED_PATH.equals(req.getRequestURI())) {
      resp.getWriter().print(soyTofu.newRenderer(TEMPLATE_NAME).setData(HOME_DATA).render());
    } else {
      resp.sendError(404, String.format("Page '%s' not found.", req.getRequestURI()));
    }
  }
}
