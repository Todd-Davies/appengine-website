package uk.co.todddavies.website.cron.tasks.data;

import uk.co.todddavies.website.cron.tasks.data.Annotations.TaskDatastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.AbstractModule;

/**
 * Module to provide {@code NotesDatastoreInterface}.
 */
public final class TasksDatastoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(DatastoreService.class).annotatedWith(TaskDatastore.class)
        .toInstance(DatastoreServiceFactory.getDatastoreService());
    
    bind(TaskDatastoreInterface.class).to(TasksDatastoreImpl.class);
  }
}
