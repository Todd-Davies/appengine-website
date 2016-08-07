package uk.co.todddavies.website.notes.data;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of{@code NotesDatastoreInterface}.
 */
final class NotesDatastoreImpl implements NotesDatastoreInterface {
  
  private static final Logger log = Logger.getLogger(NotesDatastoreImpl.class.getName());
  private static String KIND = "NotesDocument";
  
  private final Datastore datastore;
  
  @Inject
  NotesDatastoreImpl(Datastore datastore) {
    this.datastore = datastore;
  }
  
  public ImmutableList<NotesDocument> listNotes() {
    Query<Entity> query = Query
        .entityQueryBuilder()
        .kind(KIND)
        .orderBy(OrderBy.desc("downloads")).build();
    ImmutableList.Builder<NotesDocument> notes = ImmutableList.builder();
    Iterator<Entity> it = datastore.run(query);
    while (it.hasNext()) {
      notes.add(NotesDocument.createFromEntity(it.next()));
    }
    return notes.build();
  }
  
  public Optional<NotesDocument> get(long key) {
    Entity entity = datastore.get(createKey(key));
    if (entity == null) {
      return Optional.<NotesDocument>absent();
    } else {
      return Optional.of(NotesDocument.createFromEntity(entity));
    }
  }

  public int incrementDownloads(NotesDocument notesDocument) {
    Entity entity = datastore.get(createKey(notesDocument.getKey()));
    if (entity != null) {
      long newDownloads = entity.getLong("downloads") + 1;
      datastore.update(Entity.builder(entity).set("downloads", newDownloads).build());
      return (int) newDownloads;
    } else {
      log.log(Level.WARNING, 
          String.format("NotesDocument '%s' not found in Datastore.", notesDocument));
      return -1;
    }
  }
  
  private Key createKey(long key) {
    return datastore.newKeyFactory().kind(KIND).newKey(key);
  }
}
