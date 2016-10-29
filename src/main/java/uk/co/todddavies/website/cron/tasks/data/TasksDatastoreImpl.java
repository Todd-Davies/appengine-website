package uk.co.todddavies.website.cron.tasks.data;

import uk.co.todddavies.website.cron.tasks.data.Annotations.TaskDatastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import java.io.Serializable;

/**
 * Implementation of{@code NotesDatastoreInterface}.
 */
final class TasksDatastoreImpl implements TaskDatastoreInterface, Serializable {

  private static final long serialVersionUID = -758112017900118918L;
  private static String KIND = "RecurringTask";
  
  private final DatastoreService datastore;
  
  @Inject
  TasksDatastoreImpl(@TaskDatastore DatastoreService datastore) {
    this.datastore = datastore;
  }
  
  public Optional<RecurringTask> get(long key) {
    try {
      return Optional.of(
          RecurringTask.createFromEntity(datastore.get(KeyFactory.createKey(KIND, key))));
    } catch (EntityNotFoundException e) {
      return Optional.<RecurringTask>absent();
    }
  }
}
