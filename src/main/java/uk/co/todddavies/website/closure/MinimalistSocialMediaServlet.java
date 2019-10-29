package uk.co.todddavies.website.closure;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
final class MinimalistSocialMediaServlet extends HttpServlet {

  private static final String TEMPLATE_NAME = "todddavies.website.socialmedia";

  private final SoySauce soySauce;

  @Inject
  private MinimalistSocialMediaServlet(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.getWriter().print(soySauce.renderTemplate(TEMPLATE_NAME).renderHtml().get().getContent());
  }
}
