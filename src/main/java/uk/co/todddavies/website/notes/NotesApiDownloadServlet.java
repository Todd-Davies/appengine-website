package uk.co.todddavies.website.notes;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.Preconditions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@SuppressWarnings("serial")
@Singleton
final class NotesApiDownloadServlet extends HttpServlet { 
  
  private static final String KIND = "NotesDocument";
  
  private final Datastore datastore;
  private final Provider<Long> keyProvider;
  
  @Inject
  private NotesApiDownloadServlet(
      @Named("key") Provider<Long> keyProvider,
      Datastore datastore) {
    this.datastore = datastore;
    this.keyProvider = keyProvider;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    Long key = keyProvider.get();
    Preconditions.checkNotNull(key, "Notes key must not be null.");
    Key notesKey = datastore.newKeyFactory().kind(KIND).newKey(key);
    // TODO: Add caching here (w/ long timeout)
    Entity entity = datastore.get(notesKey);
    if (entity == null) {
      resp.sendError(404, String.format("Notes document with ID %d not found.", key));
    } else {
      // TODO(td): Do this in a task queue for latency
      datastore.update(NotesDocument.incrementDownloads(entity));
      NotesDocument document = NotesDocument.createFromEntity(entity);
      resp.sendRedirect(document.url);
    }
  }
}