package uk.co.todddavies.website.notes.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.AbstractModule;

/**
 * Module to provide {@code NotesDatastoreInterface}.
 */
public final class NotesDatastoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(DatastoreService.class).toInstance(DatastoreServiceFactory.getDatastoreService());
    
    bind(NotesDatastoreInterface.class).to(NotesDatastoreImpl.class);
  }
}
