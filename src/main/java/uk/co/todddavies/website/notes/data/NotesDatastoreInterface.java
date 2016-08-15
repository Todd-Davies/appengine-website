package uk.co.todddavies.website.notes.data;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.concurrent.Future;

/**
 * Provides an interface to query Datastore and perform common operations.
 */
public interface NotesDatastoreInterface {
  
  public void put(NotesDocument document);
  
  public ImmutableList<NotesDocument> listNotes();
  
  public Optional<NotesDocument> get(long key);

  public int incrementDownloads(NotesDocument notesDocument);
  
  public Future<Integer> incrementDownloadsAsync(NotesDocument notesDocument);
}
