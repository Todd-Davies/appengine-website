package uk.co.todddavies.website.cache;

import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.co.todddavies.website.cache.Annotations.CacheInstance;
import uk.co.todddavies.website.cache.MemcacheKeys.MemcacheKey;
import uk.co.todddavies.website.notes.data.NotesDocument;
import uk.co.todddavies.website.testing.LogVerifier;
import uk.co.todddavies.website.testing.LogVerifierModule;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test for {@code MemcacheInterfaceImpl}.
 */
public class MemcacheInterfaceImplTest {
  
  @Mock
  private Cache mockCache;
  
  @Inject
  private MemcacheInterfaceImpl cacheInterface;
  
  @Inject
  @SuppressWarnings("rawtypes")
  Map<Class, LogVerifier> logVerifiers;
  
  @Before
  public void setUp() {
    mockCache =  mock(Cache.class);
    
    Guice.createInjector(
        LogVerifierModule.create(MemcacheInterfaceImpl.class),
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
        is(equalTo(Optional.<Serializable>empty())));
    
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
  
  @Test
  public void testCacheValueIncorrectType() {
    // Return an integer instead of the expected type
    when(mockCache.get(any(String.class))).thenReturn(123);
    
    String expectedMessage = "Memcache key is of an unexpected type for key 'NOTES_LIST'\n"
        + "Expected type: 'com.google.common.collect.ImmutableMap<java.lang.String,"
        + " java.util.LinkedList<uk.co.todddavies.website.notes.data.NotesDocument>>'\n"
        + "Actual type: 'class java.lang.Integer'\nValue: '123'\nThis should not happen; are"
        + " multiple systems writing to the same memcache instance?\n";
    
    assertThat(cacheInterface.get(MemcacheKey.NOTES_LIST),
        is(equalTo(Optional.<Serializable>empty())));
    
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
    logVerifiers.get(MemcacheInterfaceImpl.class).verify(Level.SEVERE, expectedMessage);
  }
  
  @Test
  public void testCacheGetValueCorrectType() {
    ImmutableMap<String, LinkedList<NotesDocument>> dummyCachedValue =
        ImmutableMap.of("", new LinkedList<NotesDocument>());
    
    when(mockCache.get(any(String.class))).thenReturn(dummyCachedValue);
    
    assertThat(
        cacheInterface.<ImmutableMap<String, LinkedList<NotesDocument>>>get(MemcacheKey.NOTES_LIST),
        is(equalTo(Optional.of(dummyCachedValue))));
    
    verify(mockCache).get(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testCachePutValueCorrectType() {
    ImmutableMap<String, LinkedList<NotesDocument>> dummyCachedValue =
        ImmutableMap.of("", new LinkedList<NotesDocument>());
    
    cacheInterface.put(MemcacheKey.NOTES_LIST, dummyCachedValue);
    
    verify(mockCache).put(any(String.class), eq(dummyCachedValue));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testCachePutValueIncorrectType() {
    String expectedMessage = "The value to be put in memcache for key 'NOTES_LIST' was of the"
        + " wrong type.\n"
        + "Expected type: 'com.google.common.collect.ImmutableMap<java.lang.String,"
        + " java.util.LinkedList<uk.co.todddavies.website.notes.data.NotesDocument>>'\n"
        + "Actual type: 'class java.lang.Integer'\nValue: '123'\n";
    
    cacheInterface.put(MemcacheKey.NOTES_LIST, 123);
    
    verify(mockCache, never()).put(any(String.class), any());
    logVerifiers.get(MemcacheInterfaceImpl.class).verify(Level.SEVERE, expectedMessage);
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testCachePutValueNull() {
    cacheInterface.put(MemcacheKey.NOTES_LIST, null);
    
    verify(mockCache, never()).put(any(String.class), any());
  }
  
  @Test
  public void testRemoveValue() {
    cacheInterface.remove(MemcacheKey.NOTES_LIST);
    
    verify(mockCache).remove(MemcacheKeys.KEY_MAP.get(MemcacheKey.NOTES_LIST));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testPutServiceException() {
    when(mockCache.put(any(Object.class), any(Object.class)))
        .thenThrow(new MemcacheServiceException("No service!"));
    String expectedMessage = "MemCache service down";
    ImmutableMap<String, LinkedList<NotesDocument>> testValue = ImmutableMap.of();
    
    cacheInterface.put(MemcacheKey.NOTES_LIST, testValue);
    
    logVerifiers.get(MemcacheInterfaceImpl.class).verifyLogContains(Level.WARNING, expectedMessage);
  }
  
  @Test
  public void testGetServiceException() {
    when(mockCache.get(any(Object.class)))
        .thenThrow(new MemcacheServiceException("No service!"));
    String expectedMessage = "MemCache service down";
    
    cacheInterface.get(MemcacheKey.NOTES_LIST);
    
    logVerifiers.get(MemcacheInterfaceImpl.class).verifyLogContains(Level.WARNING, expectedMessage);
  }
  
  @Test
  public void testRemoveServiceException() {
    when(mockCache.remove(any(Object.class)))
        .thenThrow(new MemcacheServiceException("No service!"));
    String expectedMessage = "MemCache service down";
    
    cacheInterface.remove(MemcacheKey.NOTES_LIST);
    
    logVerifiers.get(MemcacheInterfaceImpl.class).verifyLogContains(Level.WARNING, expectedMessage);
  }
}
