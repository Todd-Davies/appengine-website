package uk.co.todddavies.website.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import com.google.inject.Guice;
import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@code MemcacheModule}.
 */
public class MemcacheModuleTest { 
  
  @Inject
  MemcacheInterface cacheInterface;
  
  @Before
  public void setUp() {
    Guice.createInjector(new MemcacheModule()).injectMembers(this);
  }
  
  @Test
  public void testCreateInjector() {
    assertThat(cacheInterface, notNullValue());
  }
}
