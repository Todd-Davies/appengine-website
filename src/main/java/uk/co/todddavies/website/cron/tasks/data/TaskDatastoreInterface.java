package uk.co.todddavies.website.cron.tasks.data;

import com.google.common.base.Optional;

/**
 * Provides an interface to query Datastore and perform common operations.
 */
public interface TaskDatastoreInterface {
  public Optional<RecurringTask> get(long key);
}
