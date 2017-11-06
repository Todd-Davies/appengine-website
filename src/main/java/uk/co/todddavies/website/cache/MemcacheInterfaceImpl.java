package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.Annotations.CacheInstance;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;

/**
 * Interfaces with memcache in a typesafe manner.
 * 
 * Objects are validated as being the correct type as to avoid casting exceptions at runtime.
 */
final class MemcacheInterfaceImpl implements MemcacheInterface, Serializable {
  
  private static final long serialVersionUID = 4623929768126779493L;

  private static final Logger log = Logger.getLogger(MemcacheInterfaceImpl.class.getName());

  private final Cache memcache;
  
  @Inject
  MemcacheInterfaceImpl(@CacheInstance Cache memcache) {
    this.memcache = memcache;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> Optional<T> get(MemcacheKey key) {
    T out = null;
    // Try and retrieve the object from the cache
    try {
      out = (T) memcache.get(MemcacheKeys.KEY_MAP.get(key));
    } catch (Exception e) {
      log.log(Level.WARNING, "MemCache service down", e);
    }
    // The cache didn't contain that object
    if (out == null) {
      return Optional.absent();
    } else {
      // Check that the retrieved object is of the correct type.
      TypeLiteral<?> expectedType = MemcacheKeys.EXPECTED_TYPES.get(key);
      if (expectedType.getRawType().isInstance(out)) {
        return Optional.of(out);
      } else {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(String.format(
            "Memcache key is of an unexpected type for key '%s'\n", key));
        logIncorrectTypeError(errorMessage, key, out);
        errorMessage.append(
            "This should not happen; are multiple systems writing to the same memcache"
            + " instance?\n");
        log.severe(errorMessage.toString());
        return Optional.absent();
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Serializable> boolean put(MemcacheKey key, T object) {
    if (object != null
        && MemcacheKeys.EXPECTED_TYPES.get(key).getRawType().isAssignableFrom(object.getClass())) {
      try {
        memcache.put(MemcacheKeys.KEY_MAP.get(key), object);
        return true;
      } catch (Exception e) {
        log.log(Level.WARNING, "MemCache service down", e);
      }
    } else {
      StringBuilder errorMessage = new StringBuilder();
      errorMessage.append(String.format(
          "The value to be put in memcache for key '%s' was of the wrong type.\n",
          key.toString()));
      logIncorrectTypeError(errorMessage, key, object);
      log.severe(errorMessage.toString());
    }
    return false;
  }

  @Override
  public void remove(MemcacheKey key) {
    try {
      memcache.remove(MemcacheKeys.KEY_MAP.get(key));
    } catch (Exception e) {
      log.log(Level.WARNING, "MemCache service down", e);
    }
  }
  
  private void logIncorrectTypeError(StringBuilder sb, MemcacheKey key, Object object) {
    sb.append(String.format(
        "Expected type: '%s'\n", MemcacheKeys.EXPECTED_TYPES.get(key).toString()));
    sb.append(String.format(
        "Actual type: '%s'\n", object == null ? "null" : object.getClass().toString()));
    sb.append(String.format("Value: '%s'\n", object == null ? "null" : object.toString()));
  }
}
