package uk.co.todddavies.website.cache;

import com.google.inject.Guice;

import org.junit.Before;

/**
 * Test for {@code MemcacheModule}.
 */
public class MemcacheModuleTest { 
  
  @Before
  public void setUp() {
    Guice.createInjector(new MemcacheModule()).injectMembers(this);
  }
  
  // TODO(td): Write tests for memcache module
}
