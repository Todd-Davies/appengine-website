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
final class HomeServlet extends HttpServlet {

  private static final long BIRTH_MILLIS = 802224000000L;
  private static final long YEAR_MILLIS = 31556952000L;

  private final SoyTofu soyTofu;

  @Inject
  private HomeServlet(SoyTofu soyTofu) {
    this.soyTofu= soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.getWriter().print(soyTofu.newRenderer(".home").setData(homeData()).render());
  }

  private static SoyMapData homeData() {
    return new SoyMapData("age", (System.currentTimeMillis() - BIRTH_MILLIS) / YEAR_MILLIS);
  }
}
