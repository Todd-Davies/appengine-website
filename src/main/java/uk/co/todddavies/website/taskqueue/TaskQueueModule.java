package uk.co.todddavies.website.taskqueue;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Module to provide {@code Queue} for task queues.
 */
public final class TaskQueueModule extends AbstractModule {

  @Override
  protected void configure() {}
  
  @Provides
  Queue provideTaskQueue() {
    return QueueFactory.getDefaultQueue();
  }
}
