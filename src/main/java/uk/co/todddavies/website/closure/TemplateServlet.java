package uk.co.todddavies.website.closure;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Singleton
final class TemplateServlet extends HttpServlet {

  private static String PREFIX = "/_/v2";
  private static final ImmutableMap<String, String> TEMPLATE_MAP = addSlashesToPaths(ImmutableMap.of(
      PREFIX + "", ".base",
      PREFIX + "/contact", ".contact"));

  private final SoyTofu soyTofu;

  @Inject
  private TemplateServlet(SoyTofu soyTofu) {
    this.soyTofu= soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (TEMPLATE_MAP.containsKey(req.getRequestURI())) {
      resp.getWriter().print(soyTofu.newRenderer(TEMPLATE_MAP.get(req.getRequestURI())).render());
    } else {
      resp.sendError(404, String.format("Template %s not mapped.", req.getRequestURI()));
    }
  }

  /**
   * Adds slashes to the paths (keys) of a redirect mapping.
   *
   * E.g. (/contact, /#contact) will go to (/contact, /#contact) + (/contact/, #contact);
   */
  private static final ImmutableMap<String, String> addSlashesToPaths(
      ImmutableMap<String, String> input) {
    ImmutableMap.Builder<String, String> output = ImmutableMap.builder();
    for (Map.Entry<String, String> mapping : input.entrySet()) {
      output.put(mapping.getKey(), mapping.getValue());
      output.put(mapping.getKey() + '/', mapping.getValue());
    }
    return output.build();
  }
}
