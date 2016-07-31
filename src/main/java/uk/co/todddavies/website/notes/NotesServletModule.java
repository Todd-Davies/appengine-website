package uk.co.todddavies.website.notes;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;
import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public final class NotesServletModule extends ServletModule {
  
  private NotesServletModule() {}
  
  public static AbstractModule create() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        install(new NotesServletModule());
        
        Datastore datastore = DatastoreOptions.defaultInstance().service();
        bind(Datastore.class).toInstance(datastore);
        bind(KeyFactory.class).toInstance(datastore.newKeyFactory().kind("NotesDocument"));
      }
    };
  }
  
  @Override
  protected void configureServlets() {
    serve("/api/notes").with(NotesApiServlet.class);
  }  
}
