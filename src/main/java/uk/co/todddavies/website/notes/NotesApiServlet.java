package uk.co.todddavies.website.notes;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.cache.Cache;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
final class NotesApiServlet extends HttpServlet { 
  
  private static final String CACHE_KEY = "notes-list";
  
  private final NotesDatastoreInterface notesStorage;
  private final ObjectWriter jsonObjectWriter;
  private final Optional<Cache> memCache;
  
  @Inject
  private NotesApiServlet(
      NotesDatastoreInterface notesStorage,
      ObjectWriter jsonObjectWriter,
      Optional<Cache> memCache) {
    this.notesStorage = notesStorage;
    this.jsonObjectWriter = jsonObjectWriter;
    this.memCache = memCache;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    String notesList = memCache.isPresent() 
        ? (String) memCache.get().get(CACHE_KEY)
        : null;
    if (notesList == null) {
      ImmutableList<NotesDocument> notes = notesStorage.listNotes();
      int totalDownloads = 0;
      for (NotesDocument document : notes) {
        totalDownloads += document.getDownloads();
      }
      notesList = jsonObjectWriter.writeValueAsString(
          ImmutableMap.of(
              "downloads", totalDownloads,
              "notes", notes));
      memCache.get().put(CACHE_KEY, notesList);
    }
    resp.getWriter().print(notesList);
  }
  
  /**
   * List the notes according to the tags they have.
   * @param tags The tags to list by.
   * @return A linked map of tag->List<NotesDocument> where the order of the tag iterator is the
   * same as the input tag list. 
   */
  @VisibleForTesting
  public LinkedHashMap<String, LinkedList<NotesDocument>> listNotesByTag(ImmutableList<String> tags) {
    LinkedHashMap<String, LinkedList<NotesDocument>> output = new LinkedHashMap<>();
    // Add the tag keys in order
    for (String tag : tags) {
      output.put(tag, new LinkedList<NotesDocument>());
    }
    // For each notes document, add it to the map
    for (NotesDocument notes : notesStorage.listNotes()) {
      Optional<String> firstTag = getFirstTag(notes, tags);
      if (firstTag.isPresent()) {
        output.get(firstTag.get()).add(notes);
      } else { /* Don't show notes that aren't in the tag list */ }
    }
    // Remove tags w/ no notes
    for (String tag : tags) {
      if (output.get(tag).isEmpty()) {
        output.remove(tag);
      }
    }
    return output;
  }
  
  /**
   * Returns the first tag in {@code tags} that is present in {@code notes.getTags()}. Returns
   * {@code Optional.absent()} if none are present.
   */
  private static Optional<String> getFirstTag(NotesDocument notes, ImmutableList<String> tags) {
    for (String tag : tags) {
      if (notes.getTags().contains(tag)) {
        return Optional.of(tag);
      }
    }
    return Optional.absent();
  }
}