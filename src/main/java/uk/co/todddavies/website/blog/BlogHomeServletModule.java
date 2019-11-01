package uk.co.todddavies.website.blog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.co.todddavies.website.blog.BlogPostServletModule.PATH_MAP;

@Singleton
final class BlogHomeServletModule extends HttpServlet {
  private final SoySauce soySauce;

  @Inject
  private BlogHomeServletModule(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ImmutableList.Builder<SanitizedContent> posts = ImmutableList.builder();
    for (String templateName : PATH_MAP.values()) {
      String fullyQualifiedName = String.format("todddavies.website.blog.%sContent", templateName);
      posts.add(soySauce.renderTemplate(fullyQualifiedName).renderHtml().get());
    }

    response.getWriter().print(
        soySauce
            .renderTemplate("todddavies.website.blogHome")
            .setData(ImmutableMap.of("content", posts.build()))
            .renderHtml().get().getContent());
  }
}
