package uk.co.todddavies.website.notes;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.Preconditions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@SuppressWarnings("serial")
@Singleton
final class NotesApiDownloadServlet extends HttpServlet { 
  
  private static final String KIND = "NotesDocument";
  
  private final Datastore datastore;
  private final Provider<Optional<Long>> keyProvider;
  
  @Inject
  private NotesApiDownloadServlet(
      @Named("key") Provider<Optional<Long>> keyProvider,
      Datastore datastore) {
    this.datastore = datastore;
    this.keyProvider = keyProvider;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    Optional<Long> key = keyProvider.get();
    if (!key.isPresent()) {
      resp.sendError(400, "Notes key must be supplied.");
    } else {
      Key notesKey = datastore.newKeyFactory().kind(KIND).newKey(key.get());
      // TODO(td): Add caching here (w/ long timeout)
      Entity entity = datastore.get(notesKey);
      if (entity == null) {
        resp.sendError(404, String.format("Notes document with ID %d not found.", key.get()));
      } else {
        // TODO(td): Do this in a task queue for latency
        datastore.update(NotesDocument.incrementDownloads(entity));
        NotesDocument document = NotesDocument.createFromEntity(entity);
        resp.sendRedirect(document.url);
      }
    }
  }
}