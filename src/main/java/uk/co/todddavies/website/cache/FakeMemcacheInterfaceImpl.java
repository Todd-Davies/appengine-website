package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import com.google.common.annotations.VisibleForTesting;

import java.io.Serializable;
import java.util.Optional;

/**
 * Fake implementation of {@code MemcacheInterface} intended for use in testing or when memcache is
 * unavailable.
 */
@VisibleForTesting
public final class FakeMemcacheInterfaceImpl implements MemcacheInterface {
  
  @Override
  public <T extends Serializable> Optional<T> get(MemcacheKey key) {
    return Optional.empty();
  }

  @Override
  public <T extends Serializable> boolean put(MemcacheKey key, T object) {
    /* Do nothing, leave the 'cache' empty */
    return false;
  }

  @Override
  public void remove(MemcacheKey key) {
    /* Do nothing, there's nothing to remove */
  }
}
