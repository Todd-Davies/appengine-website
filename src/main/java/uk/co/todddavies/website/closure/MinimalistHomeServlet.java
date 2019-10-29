package uk.co.todddavies.website.closure;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

@Singleton
final class MinimalistHomeServlet extends HttpServlet {

  private static final String TEMPLATE_NAME = "todddavies.website.minimalisthome";
  private static final LocalDate BIRTH_DATE = LocalDate.of(1995, 06, 04);

  private final SoySauce soySauce;

  @Inject
  private MinimalistHomeServlet(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int yearsOld = Period.between(BIRTH_DATE, LocalDate.now()).getYears();
    resp.getWriter().print(
        soySauce
            .renderTemplate(TEMPLATE_NAME)
            .setData(ImmutableMap.of("age", yearsOld))
            .renderHtml().get().getContent());
  }
}
