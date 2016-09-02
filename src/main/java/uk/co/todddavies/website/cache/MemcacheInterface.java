package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import com.google.common.base.Optional;

import java.io.Serializable;

/**
 * Interface for reading and writing objects to memcache in a typesafe manner.
 */
public interface MemcacheInterface {
  
  public <T extends Serializable> Optional<T> get(MemcacheKey key);

  public <T extends Serializable> boolean put(MemcacheKey key, T object);

  public void remove(MemcacheKey key);
}
