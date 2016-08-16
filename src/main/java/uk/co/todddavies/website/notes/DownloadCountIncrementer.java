package uk.co.todddavies.website.notes;

import uk.co.todddavies.website.cache.MemcacheInterface;
import uk.co.todddavies.website.cache.MemcacheModule;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDatastoreModule;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.util.logging.Logger;

/**
 * Increments the download count of a {@code NotesDocument}
 * TODO(td): Consider the dependency injection story for this
 */
final class DownloadCountIncrementer implements DeferredTask {
  
  private static final long serialVersionUID = -6205776849221438719L;
  private static final Logger log = Logger.getLogger(DownloadCountIncrementer.class.getName());
  
  private final long key;
  
  @Inject private Provider<NotesDatastoreInterface> storageInterfaceProvider;
  @Inject private Provider<MemcacheInterface> memcacheProvider;
  
  static TaskOptions create(long key) {
    return TaskOptions.Builder.withPayload(
        new DownloadCountIncrementer(key));
  }
  
  private DownloadCountIncrementer(long key) {
    this.key = key;
  }
  
  @Override
  public void run() {
    createInjector().injectMembers(this);
    NotesDatastoreInterface storageInterface = storageInterfaceProvider.get();
    Optional<NotesDocument> optionalNotes = storageInterface.get(key);
    if (optionalNotes.isPresent()) {
      storageInterface.incrementDownloads(optionalNotes.get());
      memcacheProvider.get().remove(MemcacheKey.NOTES_LIST);
    } else {
      log.warning(String.format("Unable to find notes file with key %d\n", key));
    }
  }
  
  private static Injector createInjector() {
    return Guice.createInjector(new MemcacheModule(), new NotesDatastoreModule());
  }
}