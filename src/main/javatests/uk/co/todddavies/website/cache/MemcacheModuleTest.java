package uk.co.todddavies.website.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;

/**
 * Test for {@code MemcacheModule}.
 */
public class MemcacheModuleTest {
  
  @Inject private Optional<Cache> cache; 
  
  @Before
  public void setUp() {
    Guice.createInjector(new MemcacheModule()).injectMembers(this);
  }
  
  @Test
  public void testInject() {
    assertThat(cache.isPresent(), is(equalTo(true)));
  }
}
