package uk.co.todddavies.website.notes.data;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;
import com.google.inject.AbstractModule;

/**
 * Module to provide {@code NotesDatastoreInterface}.
 */
public final class NotesDatastoreModule extends AbstractModule {

  @Override
  protected void configure() {
    Datastore datastore = DatastoreOptions.defaultInstance().service();
    bind(Datastore.class).toInstance(datastore);
    bind(KeyFactory.class).toInstance(datastore.newKeyFactory().kind("NotesDocument"));
  }

}
