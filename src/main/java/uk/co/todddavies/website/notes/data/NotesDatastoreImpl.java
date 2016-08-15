package uk.co.todddavies.website.notes.data;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of{@code NotesDatastoreInterface}.
 */
final class NotesDatastoreImpl implements NotesDatastoreInterface {
  
  private static final Logger log = Logger.getLogger(NotesDatastoreImpl.class.getName());
  private static String KIND = "NotesDocument";
  
  private final DatastoreService datastore;
  private final AsyncDatastoreService asyncDatastore;
  
  @Inject
  NotesDatastoreImpl(DatastoreService datastore, AsyncDatastoreService asyncDatastore) {
    this.datastore = datastore;
    this.asyncDatastore = asyncDatastore;
  }
  
  public ImmutableList<NotesDocument> listNotes() {
    Query query = new Query(KIND);
    query.addSort("downloads", Query.SortDirection.DESCENDING);
    ImmutableList.Builder<NotesDocument> notes = ImmutableList.builder();
    for (Entity entity : datastore.prepare(query).asIterable()) {
      notes.add(NotesDocument.createFromEntity(entity));
    }
    return notes.build();
  }
  
  public Optional<NotesDocument> get(long key) {
    try {
      return Optional.of(NotesDocument.createFromEntity(datastore.get(createKey(key))));
    } catch (EntityNotFoundException e) {
      return Optional.<NotesDocument>absent();
    }
  }

  public int incrementDownloads(NotesDocument notesDocument) {
    try {
      Entity entity = datastore.get(createKey(notesDocument.getKey()));
      long newDownloads = (long) entity.getProperty("downloads") + 1;
      entity.setProperty("downloads", newDownloads);
      datastore.put(entity);
      return (int) newDownloads;
    } catch (EntityNotFoundException e) {
      String errorString = "NotesDocument '%s' not found in Datastore.";
      log.log(Level.WARNING, String.format(errorString, notesDocument), e);
      return -1;
    }
  }
  
  public Future<Integer> incrementDownloadsAsync(final NotesDocument notesDocument) {
    ListenableFuture<Entity> entity =
        JdkFutureAdapters.listenInPoolThread(asyncDatastore.get(createKey(notesDocument.getKey())));
    return Futures.transform(entity, new Function<Entity, Integer>() {
      @Override
      public Integer apply(Entity input) {
        return updateDownloadCount(input, notesDocument);
      }});
  }
  
  private final Integer updateDownloadCount(
      com.google.appengine.api.datastore.Entity entity,
      NotesDocument notesDocument) {
    if (entity != null) {
      final long newDownloads = (long) entity.getProperty("downloads") + 1;
      entity.setProperty("downloads", newDownloads);
      try {
        Futures.getUnchecked(asyncDatastore.put(entity));
        return (int) newDownloads;
      } catch (UncheckedExecutionException e) {
        String errorString = "An error occured updating the download count of %s";
        log.log(Level.WARNING, String.format(errorString, notesDocument), e);
        return -1;
      }
    } else {
      log.warning(String.format("NotesDocument '%s' not found in Datastore.", notesDocument));
      return -1;
    }
  }
  
  private Key createKey(long key) {
    return KeyFactory.createKey("NotesDocument", key);
  }

  @Override
  public void put(NotesDocument document) {
    Entity entity = new Entity(createKey(document.getKey()));
    entity.setProperty("name", document.getName());
    entity.setProperty("course_code", document.getCourseCode());
    entity.setProperty("download_url", document.getUrl());
    entity.setProperty("tags", document.getTags());
    entity.setProperty("downloads", document.getDownloads());
    datastore.put(entity);
  }
}
