package uk.co.todddavies.website.notes;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Singleton
final class NotesPromoServlet extends HttpServlet {

  private final SoySauce soySauce;

  @Inject
  private NotesPromoServlet(SoySauce soySauce) {
    this.soySauce = soySauce;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    URI uri = URI.create(request.getParameter("continue"));
    SanitizedContent s = UnsafeSanitizedContentOrdainer.ordainAsSafe(uri.toString(), SanitizedContent.ContentKind.URI);

    response.getWriter().print(
        soySauce
            .renderTemplate("todddavies.website.notesPromo")
            .setData(ImmutableMap.of("continue", s))
            .renderHtml().get().getContent());
  }
}