package uk.co.todddavies.website.notes;

import uk.co.todddavies.website.cache.MemcacheInterface;
import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;
import uk.co.todddavies.website.notes.data.NotesDocument;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.mortbay.log.Log;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
      if (optionalNotes.isPresent()) {
        // Increment the download count asynchronously
        taskQueue.add(
            DownloadCountIncrementer.create(key.get()));
        resp.sendRedirect(optionalNotes.get().getUrl());
      } else {
        resp.sendError(404, String.format("Notes document with ID %d not found.", key.get()));
      }
    }
  }
  
  /**
   * Increments the download count of a {@code NotesDocument}
   * TODO(td): Consider the dependency injection story for this
   */
  private static final class DownloadCountIncrementer implements DeferredTask {
    
    private final long key;
    
    @Inject private Provider<NotesDatastoreInterface> storageInterfaceProvider;
    @Inject private Provider<MemcacheInterface> memcacheProvider;
    
    private transient NotesDatastoreInterface storageInterface;
    private transient MemcacheInterface memcache;
    
    static TaskOptions create(long key) {
      return TaskOptions.Builder.withPayload(
          new DownloadCountIncrementer(key));
    }
    
    private DownloadCountIncrementer(long key) {
      this.key = key;
    }
    
    @Override
    public void run() {
      Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
          install(new MemcacheModule());
          install(new NotesDatastoreModule());
        }
      }).injectMembers(this);
      
      if (storageInterface == null) {
        storageInterface = storageInterfaceProvider.get();
      }
      Optional<NotesDocument> optionalNotes = storageInterface.get(key);
      if (optionalNotes.isPresent()) {
        storageInterface.incrementDownloads(optionalNotes.get());
        if (memcache == null) {
          memcache = memcacheProvider.get();
        }
        memcache.remove(MemcacheKey.NOTES_LIST);
      } else {
        Log.warn(String.format("Unable to find notes file with key %d\n", key));
      }
    }
  }
}