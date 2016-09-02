package uk.co.todddavies.website.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;

import org.junit.Test;

/**
 * Test for {@code MemcacheKeys}.
 */
public class MemcacheKeysTest {
  
  @Test
  public void testKeyMapIsBijective() {
    assertThat(MemcacheKeys.KEY_MAP.keySet(), contains(MemcacheKey.values()));
  }
  
  @Test
  public void testExpectedTypesMapIsBijective() {
    assertThat(MemcacheKeys.EXPECTED_TYPES.keySet(), contains(MemcacheKey.values()));
  }
}
