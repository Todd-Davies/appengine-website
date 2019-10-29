package uk.co.todddavies.website.closure;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
final class MinimalistHomeServlet extends HttpServlet {

  private static final String TEMPLATE_NAME = "todddavies.website.minimalisthome";

  private static final long BIRTH_MILLIS = 802224000000L;
  private static final long YEAR_MILLIS = 31556952000L;
  private static final ImmutableMap<String, String> HOME_DATA = ImmutableMap.of(
      "age", String.valueOf((System.currentTimeMillis() - BIRTH_MILLIS) / YEAR_MILLIS));

  private final SoySauce soySauce;

  @Inject
  private MinimalistHomeServlet(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.getWriter().print(soySauce.renderTemplate(TEMPLATE_NAME).setData(HOME_DATA).renderHtml().get().getContent());
  }
}
