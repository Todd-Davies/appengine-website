package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.cache.Annotations.CacheInstance;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

/**
 * Provides {@code MemcacheInterface}.
 */
public final class MemcacheModule extends AbstractModule {
  
  private static final Logger log = Logger.getLogger(MemcacheModule.class.getName());

  private static final ImmutableMap<Integer, Object> CACHE_PROPERTIES =
      ImmutableMap.<Integer, Object>of(
          // Expire items after ten minutes
          GCacheFactory.EXPIRATION_DELTA, 600);
  
  @Override
  protected void configure() { 
    try {
      bind(Cache.class)
          .annotatedWith(CacheInstance.class)
          .toInstance(CacheManager.getInstance().getCacheFactory().createCache(CACHE_PROPERTIES));
      bind(MemcacheInterface.class).to(MemcacheInterfaceImpl.class);
    } catch (CacheException e) {
      log.log(Level.SEVERE, "Unable to bind to memcache, binding to fake cache instead.", e);
      bind(MemcacheInterface.class).to(FakeMemcacheInterfaceImpl.class);
    }
  }
}
