package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.Annotations.CacheInstance;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import java.io.Serializable;

import javax.cache.Cache;

/**
 * Interfaces with memcache in a typesafe manner.
 * 
 * Objects are validated as being the correct type as to avoid casting exceptions at runtime.
 */
final class MemcacheInterfaceImpl implements MemcacheInterface {

  private final Cache memcache;
  
  @Inject
  MemcacheInterfaceImpl(@CacheInstance Cache memcache) {
    this.memcache = memcache;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> Optional<T> get(MemcacheKey key) {
   // Retrieve the object from the cache
    T out = (T) memcache.get(MemcacheKeys.KEY_MAP.get(key));
    // The cache didn't contain that object
    if (out == null) {
      return Optional.absent();
    } else {
      // Check that the retrieved object is of the correct type.
      TypeLiteral<?> expectedType = MemcacheKeys.EXPECTED_TYPES.get(key);
      if (expectedType.getRawType().isInstance(out)) {
        return Optional.of(out);
      } else {
        System.err.printf("Memcache key as of an unexpected type for key '%s'\n", key);
        System.err.printf("Expected type: '%s'\n", expectedType.toString());
        System.err.printf("Actual type: '%s'\n", out == null ? "null" : out.getClass().toString());
        System.err.printf("Value: '%s'\n", out == null ? "null" : out.toString());
        return Optional.absent();
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> void put(MemcacheKey key, T object) {
    // TODO(td): Ensure that object is the correct type
    memcache.put(MemcacheKeys.KEY_MAP.get(key), object);
  }

  @Override
  public void remove(MemcacheKey key) {
    memcache.remove(MemcacheKeys.KEY_MAP.get(key));
  }

}
