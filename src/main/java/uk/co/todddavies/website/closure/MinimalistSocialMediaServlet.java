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
final class MinimalistSocialMediaServlet extends HttpServlet {

  private static final String TEMPLATE_NAME = ".socialmedia";

  private final SoyTofu soyTofu;

  @Inject
  private MinimalistSocialMediaServlet(SoyTofu soyTofu) {
    this.soyTofu= soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.getWriter().print(soyTofu.newRenderer(TEMPLATE_NAME).render());
  }
}
