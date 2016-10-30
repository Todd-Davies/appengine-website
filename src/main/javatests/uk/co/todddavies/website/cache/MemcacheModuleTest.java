package uk.co.todddavies.website.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import com.google.inject.Guice;
import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Test for {@code MemcacheModule}.
 */
public class MemcacheModuleTest { 
  
  @Inject
  MemcacheInterface cacheInterface;
  
  @Inject
  @SuppressWarnings("rawtypes")
  Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    Guice.createInjector(
        new MemcacheModule(),
        LogVerifierModule.create(MemcacheInterface.class)).injectMembers(this);
  }
  
  @Test
  public void testCreateInjector() {
    assertThat(cacheInterface, notNullValue());
    logVerifiers.get(MemcacheInterface.class).verifyNoLogs();
  }
}
