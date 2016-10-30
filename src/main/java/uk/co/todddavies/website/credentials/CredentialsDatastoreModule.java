package uk.co.todddavies.website.credentials;

import uk.co.todddavies.website.credentials.Annotations.CredentialsDatastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.AbstractModule;

/**
 * Module to provide {@code CredentialsDatastoreInterface}.
 */
public final class CredentialsDatastoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(DatastoreService.class).annotatedWith(CredentialsDatastore.class)
        .toInstance(DatastoreServiceFactory.getDatastoreService());
    
    bind(CredentialsDatastoreInterface.class).to(CredentialsDatastoreImpl.class);
  }
}
