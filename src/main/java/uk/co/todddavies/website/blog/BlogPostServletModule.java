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
final class BlogPostServletModule extends HttpServlet {
  static final ImmutableMap<String, String> PATH_MAP = ImmutableMap.<String, String>builder()
      .put("uk-election-2019-series-labour", "ukElection19SeriesLabour")
      .put("uk-election-2019-series-conservatives", "ukElection19SeriesConservatives")
      .put("uk-election-2019-series-intro", "ukElection19SeriesIntro")
      .put("getting-stuff-down", "gettingStuffDown")
      .put("how-to-pull", "howToPull")
      .put("starting-a-blog", "startingABlog")
      .build();

  private final SoySauce soySauce;

  @Inject
  private BlogPostServletModule(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String postPath = request.getPathInfo();
    if (postPath.startsWith("/")) {
      postPath = postPath.substring(1);
    }

    if (!PATH_MAP.containsKey(postPath)) {
      response.sendError(404, String.format("Post '%s' not found.", postPath));
      return;
    }

    response.getWriter().print(
        soySauce
            .renderTemplate("todddavies.website.blog." + PATH_MAP.get(postPath))
            .renderHtml().get().getContent());
  }
}