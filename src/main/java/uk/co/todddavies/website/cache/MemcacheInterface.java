package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import java.io.Serializable;
import java.util.Optional;

/**
 * Interface for reading and writing objects to memcache in a typesafe manner.
 */
public interface MemcacheInterface {
  
  public <T extends Serializable> Optional<T> get(MemcacheKey key);

  public <T extends Serializable> boolean put(MemcacheKey key, T object);

  public void remove(MemcacheKey key);
}
