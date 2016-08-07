package uk.co.todddavies.website.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.todddavies.website.cache.Annotations.CacheInstance;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.Serializable;
import java.util.LinkedList;

import javax.cache.Cache;

/**
 * Test for {@code MemcacheInterfaceImpl}.
 */
public class MemcacheInterfaceImplTest {
  
  @Mock
  private final Cache mockCache = mock(Cache.class);
  
  @Inject
  private MemcacheInterfaceImpl cacheInterface;
  
  @Before
  public void setUp() {
    Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(Cache.class).annotatedWith(CacheInstance.class).toInstance(mockCache);
          }
        }).injectMembers(this);
  }
  
  @Test
  public void testCacheValueNotFound() {
    // Return that no value was found
    when(mockCache.get(any(String.class))).thenReturn(null);
    
    assertThat(cacheInterface.get(MemcacheKey.NOTES_LIST),
        is(equalTo(Optional.<Serializable>absent())));
    
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
  
  @Test
  public void testCacheValueIncorrectType() {
    // Return an integer instead of the expected type
    when(mockCache.get(any(String.class))).thenReturn(123);
    
    assertThat(cacheInterface.get(MemcacheKey.NOTES_LIST),
        is(equalTo(Optional.<Serializable>absent())));
    
    // TODO(td): Assert that the correct error message is logged.
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
  
  @Test
  public void testCacheValueCorrectType() {
    ImmutableMap<String, LinkedList<NotesDocument>> dummyCachedValue =
        ImmutableMap.of("", new LinkedList<NotesDocument>());
    
    when(mockCache.get(any(String.class))).thenReturn(dummyCachedValue);
    
    assertThat(
        cacheInterface.<ImmutableMap<String, LinkedList<NotesDocument>>>get(MemcacheKey.NOTES_LIST),
        is(equalTo(Optional.of(dummyCachedValue))));
    
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
}
