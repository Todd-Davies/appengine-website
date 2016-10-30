package uk.co.todddavies.website.credentials;

import uk.co.todddavies.website.credentials.Annotations.CredentialsDatastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import java.io.Serializable;

/**
 * Implementation of{@code CredentialsDatastoreInterface}.
 */
final class CredentialsDatastoreImpl implements CredentialsDatastoreInterface, Serializable {

  private static final long serialVersionUID = -2559283963364099570L;
  private static String KIND = "Credential";
  
  private final DatastoreService datastore;
  
  @Inject
  CredentialsDatastoreImpl(@CredentialsDatastore DatastoreService datastore) {
    this.datastore = datastore;
  }
  
  public Optional<Credential> get(long key) {
    try {
      return Optional.of(
          Credential.createFromEntity(datastore.get(KeyFactory.createKey(KIND, key))));
    } catch (EntityNotFoundException e) {
      return Optional.<Credential>absent();
    }
  }
}
