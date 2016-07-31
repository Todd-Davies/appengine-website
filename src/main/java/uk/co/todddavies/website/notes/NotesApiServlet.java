package uk.co.todddavies.website.notes;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
final class NotesApiServlet extends HttpServlet { 
  
  private final Datastore datastore ;
  private final ObjectWriter jsonObjectWriter;
  
  @Inject
  private NotesApiServlet(
      Datastore datastore,
      ObjectWriter jsonObjectWriter) {
    this.datastore = datastore;
    this.jsonObjectWriter = jsonObjectWriter;
  }
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    ImmutableList<NotesDocument> notes = listNotes();
    resp.getWriter().print(jsonObjectWriter.writeValueAsString(notes));
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