package uk.co.todddavies.website.credentials;

import com.google.common.base.Optional;

/**
 * Provides an interface to query Datastore and perform common operations.
 */
public interface CredentialsDatastoreInterface {
  public Optional<Credential> get(long key);
}
