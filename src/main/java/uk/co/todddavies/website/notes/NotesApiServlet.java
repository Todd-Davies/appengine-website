package uk.co.todddavies.website.notes;

import java.io.IOException;
import java.util.Iterator;

import javax.cache.Cache;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
final class NotesApiServlet extends HttpServlet { 
  
  private static final String CACHE_KEY = "notes-list";
  
  private final Datastore datastore ;
  private final ObjectWriter jsonObjectWriter;
  private final Optional<Cache> memCache;
  
  @Inject
  private NotesApiServlet(
      Datastore datastore,
      ObjectWriter jsonObjectWriter,
      Optional<Cache> memCache) {
    this.datastore = datastore;
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
      ImmutableList<NotesDocument> notes = listNotes();
      int totalDownloads = 0;
      for (NotesDocument document : notes) {
        totalDownloads += document.downloads;
      }
      notesList = jsonObjectWriter.writeValueAsString(
          ImmutableMap.of(
              "downloads", totalDownloads,
              "notes", notes));
      memCache.get().put(CACHE_KEY, notesList);
    }
    resp.getWriter().print(notesList);
  }
  
  private ImmutableList<NotesDocument> listNotes() {
    Query<Entity> query = Query
        .entityQueryBuilder()
        .kind("NotesDocument")
        .orderBy(OrderBy.desc("downloads")).build();
    ImmutableList.Builder<NotesDocument> notes = ImmutableList.builder();
    Iterator<Entity> it = datastore.run(query);
    while (it.hasNext()) {
      notes.add(NotesDocument.createFromEntity(it.next()));
    }
    return notes.build();
  }
}