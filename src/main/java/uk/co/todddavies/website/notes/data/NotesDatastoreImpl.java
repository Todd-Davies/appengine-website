package uk.co.todddavies.website.notes.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of{@code NotesDatastoreInterface}.
 */
final class NotesDatastoreImpl implements NotesDatastoreInterface, Serializable {

  private static final long serialVersionUID = -8170917626618952925L;
  private static final Logger log = Logger.getLogger(NotesDatastoreImpl.class.getName());
  private static String KIND = "NotesDocument";
  
  private final DatastoreService datastore;
  
  @Inject
  NotesDatastoreImpl(DatastoreService datastore) {
    this.datastore = datastore;

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
