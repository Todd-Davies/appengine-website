package uk.co.todddavies.website.blog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
final class BlogHomeServletModule extends HttpServlet {

  private final SoyTofu soyTofu;

  @Inject
  private BlogHomeServletModule(SoyTofu soyTofu) {
    this.soyTofu = soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().print(soyTofu.newRenderer(".blog").renderHtml());
  }
}
