package uk.co.todddavies.website.blog;

import com.google.inject.Inject;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

final class BlogHomeServletModule extends HttpServlet {

  private final SoySauce soySauce;

  @Inject
  private BlogHomeServletModule(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().print(soySauce.renderTemplate(".blog").renderHtml());
  }
}
