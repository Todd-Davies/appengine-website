package uk.co.todddavies.website.notes;

import uk.co.todddavies.website.cache.MemcacheInterface;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@Singleton
final class NotesApiServlet extends HttpServlet { 
  
  private final NotesDatastoreInterface notesStorage;
  private final ObjectWriter jsonObjectWriter;
  private final MemcacheInterface memCache;
  
  // TODO(td): Don't hardcode this list
  private static final ImmutableList<String> TAGS =
      ImmutableList.of("Third Year", "Second Year", "First Year", "A-Level", "GCSE");
  
  @Inject
  private NotesApiServlet(
      NotesDatastoreInterface notesStorage,
      ObjectWriter jsonObjectWriter,
      MemcacheInterface memCache) {
    this.notesStorage = notesStorage;
    this.jsonObjectWriter = jsonObjectWriter;
    this.memCache = memCache;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    ImmutableMap<String, Serializable> response;
    // Try and read the cached data.
    Optional<ImmutableMap<String, Serializable>> cachedValue = memCache.get(MemcacheKey.NOTES_LIST);
    // If it wasn't read, then read & prepare it.
    if (!cachedValue.isPresent()) {
      Pair<LinkedHashMap<String, LinkedList<NotesDocument>>, Integer> notes = listNotesByTag(TAGS);
      int totalDownloads = notes.getValue();
      response = ImmutableMap.of("downloads", totalDownloads, "notes", notes.getKey());
      memCache.put(MemcacheKey.NOTES_LIST, response);
    } else {
      response = cachedValue.get();
    }
    // Serialise to JSON and send to the client.
    resp.getWriter().print(jsonObjectWriter.writeValueAsString(response));
  }
  
  /**
   * List the notes according to the tags they have.
   * @param tags The tags to list by.
   * @return A linked map of {@code tag->List<NotesDocument>} where the order of the tag iterator is
   *     the same as the input tag list. 
   */
  @VisibleForTesting
  public Pair<LinkedHashMap<String, LinkedList<NotesDocument>>, Integer>
      listNotesByTag(ImmutableList<String> tags) {
    LinkedHashMap<String, LinkedList<NotesDocument>> output = new LinkedHashMap<>();
    // Add the tag keys in order
    for (String tag : tags) {
      output.put(tag, new LinkedList<NotesDocument>());
    }
    // Keep track of how many downloads there were
    int downloads = 0;
    // For each notes document, add it to the map
    for (NotesDocument notes : notesStorage.listNotes()) {
      Optional<String> firstTag = getFirstTag(notes, tags);
      if (firstTag.isPresent()) {
        output.get(firstTag.get()).add(notes);
        downloads += notes.getDownloads();
      } else { /* Don't show notes that aren't in the tag list */ }
    }
    // Remove tags w/ no notes
    for (String tag : tags) {
      if (output.get(tag).isEmpty()) {
        output.remove(tag);
      }
    }
    return Pair.of(output, downloads);
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
    return Optional.empty();
  }

}
