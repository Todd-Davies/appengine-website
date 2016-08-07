package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import java.io.Serializable;

/**
 * Fake implementation of {@code MemcacheInterface} intended for use in testing or when memcache is
 * unavailable.
 */
@VisibleForTesting
public final class FakeMemcacheInterfaceImpl implements MemcacheInterface {
  
  @Override
  public <T extends Serializable> Optional<T> get(MemcacheKey key) {
    return Optional.absent();
  }

  @Override
  public <T extends Serializable> void put(MemcacheKey key, T object) {
    /* Do nothing, leave the 'cache' empty */  
  }

  @Override
  public void remove(MemcacheKey key) {
    /* Do nothing, there's nothing to remove */
  }
}
