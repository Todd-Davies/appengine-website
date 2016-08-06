package uk.co.todddavies.website.notes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.todddavies.website.JsonObjectWriterModule;
import uk.co.todddavies.website.cache.MemcacheKeys;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provides;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.cache.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Test for {@code NotesApiServlet}.
 */
public class NotesApiServletTest {

  private static final String TAG_1 = "tag1";
  private static final String TAG_2 = "tag2";
  private static final String TAG_3 = "tag3";
  
  private static final ImmutableList<String> TAGS = ImmutableList.of(TAG_1, TAG_2, TAG_3);
  
  private static final NotesDocument NOTES_1 =
      NotesDocument.createForTest("test1", "", "http://t1.com", ImmutableList.of(TAG_1), 9, 1);
  private static final NotesDocument NOTES_2 =
      NotesDocument.createForTest("test2", "", "http://t2.com", ImmutableList.of(TAG_2), 9, 2);
  private static final NotesDocument NOTES_3 =
      NotesDocument.createForTest("test3", "","http://t3.com", ImmutableList.of(TAG_1, TAG_2), 9, 3);
  private static final NotesDocument NOTES_4 =
      NotesDocument.createForTest("test4", "", "http://t4.com", ImmutableList.of(TAG_3), 9, 4);
 
  private static final ImmutableList<NotesDocument> TEST_DATA = ImmutableList.of(
      NOTES_1, NOTES_2, NOTES_3, NOTES_4);
  
  @Mock
  private final NotesDatastoreInterface mockStorage = mock(NotesDatastoreInterface.class);
  @Mock
  private final Cache mockCache = mock(Cache.class);
  
  private Optional<Cache> dummyCache = Optional.absent();
  
  @Inject
  private NotesApiServlet servlet;
  
  @Before
  public void setUp() {
    Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(NotesDatastoreInterface.class).toInstance(mockStorage);
          }
          
          @Provides
          Optional<Cache> dummyCacheProvider() {
            return dummyCache;
          }
        },
        new JsonObjectWriterModule()
        ).injectMembers(this);
    
    when(mockStorage.listNotes()).thenReturn(TEST_DATA);
  }
 
  @Test
  public void testHappyCase() throws IOException {
    ImmutableMap<String, LinkedList<NotesDocument>> dummyCachedValue =
        ImmutableMap.of("Tag", new LinkedList<NotesDocument>());
    
    dummyCache = Optional.of(mockCache);
    when(mockCache.get(any(String.class))).thenReturn(dummyCachedValue);
    
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    servlet.doGet(mock(HttpServletRequest.class), mockResponse);
    
    verify(mockWriter).print("{\n" +
        "  \"downloads\" : 0,\n" +
        "  \"notes\" : { }\n" +
      "}");
  }
  
  @Test
  public void testNoCache() throws IOException {
    when(mockStorage.listNotes()).thenReturn(ImmutableList.of(
        NotesDocument.createForTest(
            "name", "course code", "url", ImmutableList.of("Third Year"), 0, 0)));
    
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    PrintWriter mockWriter = mock(PrintWriter.class);
    when(mockResponse.getWriter()).thenReturn(mockWriter);
    
    servlet.doGet(mock(HttpServletRequest.class), mockResponse);
    
    verify(mockWriter).print("{\n" +
        "  \"downloads\" : 0,\n" +
        "  \"notes\" : {\n" +
        "    \"Third Year\" : [ {\n" +
        "      \"name\" : \"name\",\n" +
        "      \"download_url\" : \"/api/notes-dl?key=0\",\n" +
        "      \"downloads\" : 0,\n" +
        "      \"tags\" : [ \"Third Year\" ]\n" +
        "    } ]\n" +
        "  }\n" +
      "}");
  }
  
  @Test
  public void testListNotesByTagDownloads() {
    int downloads =
        servlet.listNotesByTag(TAGS).getSecond();
    
    assertThat(downloads, is(equalTo(36)));
  }
  
  @Test
  public void testListNotesByTagOrdering() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(TAGS).getFirst();
    
    assertThat(output.keySet(), contains(TAG_1, TAG_2, TAG_3));
    assertThat(output.get(TAG_1), contains(NOTES_1, NOTES_3));
    assertThat(output.get(TAG_2), contains(NOTES_2));
    assertThat(output.get(TAG_3), contains(NOTES_4));
  }
  
  @Test
  public void testListNotesByTagSecondaryTag() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(ImmutableList.of(TAG_2)).getFirst();
    
    assertThat(output.keySet(), contains(TAG_2));
    assertThat(output.get(TAG_2), contains(NOTES_2, NOTES_3));
  }
  
  @Test
  public void testListNotesByTagOmissions() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(ImmutableList.of(TAG_1)).getFirst();
    
    assertThat(output.keySet(), contains(TAG_1));
    assertThat(output.get(TAG_1), contains(NOTES_1, NOTES_3));
  }
  
  @Test
  public void testListNotesByTagEmpty() {
    when(mockStorage.listNotes()).thenReturn(ImmutableList.<NotesDocument>of());
    
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(TAGS).getFirst();
    
    assertThat(output.entrySet(), is(empty()));
  }
  
  @Test
  public void testUnknownCacheKey() {
    // TODO(td): Assert that the correct error message is logged.
    assertThat(NotesApiServlet.get(dummyCache, /* random key */ "asdff84wrfdfhg43"), nullValue());
  }
  
  @Test
  public void testAbsentCache() {
    // TODO(td): Assert that the correct error message is logged.
    assertThat(NotesApiServlet.get(Optional.<Cache>absent(), MemcacheKeys.NOTES_KEY), nullValue());
  }
  
  @Test
  public void testCacheValueNotFound() {
    dummyCache = Optional.of(mockCache);
    when(mockCache.get(any(String.class))).thenReturn(null);
    
    assertThat(NotesApiServlet.get(dummyCache, MemcacheKeys.NOTES_KEY), nullValue());
    
    verify(mockCache).get(MemcacheKeys.NOTES_KEY);
  }
  
  @Test
  public void testCacheValueIncorrectType() {
    dummyCache = Optional.of(mockCache);
    // Return an integer instead of the expected type
    when(mockCache.get(any(String.class))).thenReturn(123);
    
    assertThat(NotesApiServlet.get(dummyCache, MemcacheKeys.NOTES_KEY), nullValue());
    // TODO(td): Assert that the correct error message is logged.
    verify(mockCache).get(MemcacheKeys.NOTES_KEY);
  }
  
  @Test
  public void testCacheValueCorrectType() {
    ImmutableMap<String, LinkedList<NotesDocument>> dummyCachedValue =
        ImmutableMap.of("", new LinkedList<NotesDocument>());
    
    dummyCache = Optional.of(mockCache);
    when(mockCache.get(any(String.class))).thenReturn(dummyCachedValue);
    
    assertThat(NotesApiServlet
        .<ImmutableMap<String, LinkedList<NotesDocument>>> get(
            dummyCache,
            MemcacheKeys.NOTES_KEY),
        is(equalTo(dummyCachedValue)));
    verify(mockCache).get(MemcacheKeys.NOTES_KEY);
  }
  
  @Test
  public void testAbsentCachePut() {
    assertThat(NotesApiServlet.put(dummyCache,MemcacheKeys.NOTES_KEY, ""), is(equalTo(false)));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testCachePut() {
    dummyCache = Optional.of(mockCache);
    when(mockCache.put(any(String.class), any(String.class))).thenReturn(null);
    assertThat(NotesApiServlet.put(dummyCache,MemcacheKeys.NOTES_KEY, ""), is(equalTo(true)));
  }
}
