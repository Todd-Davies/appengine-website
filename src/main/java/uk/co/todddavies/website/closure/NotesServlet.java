package uk.co.todddavies.website.closure;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import uk.co.todddavies.website.cache.MemcacheInterface;
import uk.co.todddavies.website.cache.MemcacheKeys;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

@Singleton
final class NotesServlet extends HttpServlet {

  private final NotesDatastoreInterface notesStorage;
  private final MemcacheInterface memCache;
  private final SoyTofu soyTofu;

  // TODO(td): Don't hardcode this list
  private static final ImmutableList<String> TAGS =
      ImmutableList.of("Third Year", "Second Year", "First Year", "A-Level", "GCSE");

  @Inject
  private NotesServlet(
      NotesDatastoreInterface notesStorage,
      MemcacheInterface memCache,
      SoyTofu soyTofu) {
    this.notesStorage = notesStorage;
    this.memCache = memCache;
    this.soyTofu = soyTofu;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Optional<ImmutableMap<String, ImmutableList<ImmutableMap<String, String>>>> cachedNotes =
        memCache.get(MemcacheKeys.MemcacheKey.NOTES_SOY);
    Optional<Long> cachedDownloads =
        memCache.get(MemcacheKeys.MemcacheKey.NOTES_DOWNLOADS);
    if (!(cachedNotes.isPresent() && cachedDownloads.isPresent())) {
      ImmutableList<NotesDocument> notes = notesStorage.listNotes();
      if (!cachedNotes.isPresent()) {
        cachedNotes = Optional.of(listNotesByTag(notes));
      }
      if (!cachedDownloads.isPresent()) {
        cachedDownloads = Optional.of(downloads(notes));
      }
    }
    final ImmutableMap<String, ImmutableList<ImmutableMap<String, String>>> notes = cachedNotes.get();

    resp.getWriter().print(soyTofu.newRenderer(".notes")
        .setData(new SoyMapData(
            "downloads", cachedDownloads.get(),
            "tags", TAGS.stream().filter(tag -> notes.keySet().contains(tag)).collect(ImmutableList.toImmutableList()),
            "notes", cachedNotes.get()))
        .render());
  }

  private long downloads(ImmutableList<NotesDocument> notes) {
    return notes.stream().map(NotesDocument::getDownloads).mapToLong(Long::longValue).sum();
  }

  private static ImmutableMap<String, ImmutableList<ImmutableMap<String, String>>> listNotesByTag(ImmutableList<NotesDocument> notes) {
    ImmutableListMultimap<Optional<String>, NotesDocument> tagMap = Multimaps.index(notes, NotesServlet::getFirstTag);
    HashMap<String, ImmutableList.Builder<ImmutableMap<String, String>>> intermediate = new HashMap<>();
    tagMap.entries().stream()
        // Filter notes documents without a tag we wanted
        .filter((entry) -> entry.getKey().isPresent())
        // Convert each notes doc to be soy-friendly and add it
        .forEach((entry) -> {
          String tag = entry.getKey().get();
          if (!intermediate.containsKey(tag)) {
            intermediate.put(tag, ImmutableList.builder());
          }
          intermediate.get(tag).add(convertToMap(entry.getValue()));
        });
    // Make it immutable
    ImmutableMap.Builder<String, ImmutableList<ImmutableMap<String, String>>> out = ImmutableMap.builder();
    for (Entry<String, ImmutableList.Builder<ImmutableMap<String, String>>> entry : intermediate.entrySet()) {
      out.put(entry.getKey(), entry.getValue().build());
    }
    return out.build();
  }

  private static ImmutableMap<String, String> convertToMap(NotesDocument notes) {
    return ImmutableMap.of(
        "name", notes.getName(),
        "course_code", notes.getCourseCode(),
        "downloads", String.format("%d", notes.getDownloads()),
        "url", String.format("/api/notes-dl?key=%d", notes.getKey()));
  }

  /**
   * Returns the first tag in {@code tags} that is present in {@code notes.getTags()}. Returns
   * {@code Optional.absent()} if none are present.
   */
  private static Optional<String> getFirstTag(NotesDocument notes) {
    for (String tag : TAGS) {
      if (notes.getTags().contains(tag)) {
        return Optional.of(tag);
      }
    }
    return Optional.empty();
  }
}
