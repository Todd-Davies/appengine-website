package uk.co.todddavies.website.notes;

import com.google.appengine.api.taskqueue.Queue;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
@SuppressWarnings("serial")
final class NotesApiDownloadServlet extends HttpServlet { 
  
  private final NotesDatastoreInterface storageInterface;
  private final Provider<Optional<Long>> keyProvider;
  private final Queue taskQueue;
  
  @Inject
  private NotesApiDownloadServlet(
      @Named("key") Provider<Optional<Long>> keyProvider,
      NotesDatastoreInterface storageInterface,
      Queue taskQueue) {
    this.storageInterface = storageInterface;
    this.keyProvider = keyProvider;
    this.taskQueue = taskQueue;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    Optional<Long> key = keyProvider.get();
    if (!key.isPresent()) {
      resp.sendError(400, "Notes key must be supplied.");
    } else {
      // TODO(td): Add caching here (w/ long timeout)
      Optional<NotesDocument> optionalNotes = storageInterface.get(key.get());
      if (!optionalNotes.isPresent()) {
        // Increment the download count asynchronously
        //taskQueue.add(
        //    DownloadCountIncrementer.create(key.get()));
        String notesUrl = "https://blob.com"; //optionalNotes.get().getUrl();
        if ("true".equals(req.getParameter("promoExperiment"))) {
          resp.sendRedirect("/notes/promo?continue=" + notesUrl);
        } else {
          resp.sendRedirect(notesUrl);
        }
      } else {
        resp.sendError(404, String.format("Notes document with ID %d not found.", key.get()));
      }
    }
  }
}