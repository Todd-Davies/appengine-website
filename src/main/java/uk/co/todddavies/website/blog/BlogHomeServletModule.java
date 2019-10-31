package uk.co.todddavies.website.blog;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
final class BlogHomeServletModule extends HttpServlet {

  private final SoySauce soySauce;

  @Inject
  private BlogHomeServletModule(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().print(
        soySauce
            .renderTemplate("todddavies.website.blog")
            .setData(ImmutableMap.of("content", "Coming soon!"))
            .renderHtml().get().getContent());
  }
}
