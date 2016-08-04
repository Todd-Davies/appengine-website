package uk.co.todddavies.website.notes.data;

import java.util.Iterator;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class NotesDatastoreInterface {

  private static String KIND = "NotesDocument";
  
  private final Datastore datastore;
  
  @Inject
  NotesDatastoreInterface(Datastore datastore) {
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
  
}
