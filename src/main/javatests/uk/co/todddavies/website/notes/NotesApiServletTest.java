package uk.co.todddavies.website.notes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.cache.Cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.co.todddavies.website.JsonObjectWriterModule;
import uk.co.todddavies.website.notes.data.NotesDatastoreInterface;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * Test for {@code NotesApiServlet}.
 */
public class NotesApiServletTest {

  private static final String TAG_1 = "tag1";
  private static final String TAG_2 = "tag2";
  private static final String TAG_3 = "tag3";
  
  private static final ImmutableList<String> TAGS = ImmutableList.of(TAG_1, TAG_2, TAG_3);
  
  private static final NotesDocument NOTES_1 =
      NotesDocument.createForTest("test1", "http://t1.com", ImmutableList.of(TAG_1), 0, 1);
  private static final NotesDocument NOTES_2 =
      NotesDocument.createForTest("test2", "http://t2.com", ImmutableList.of(TAG_2), 0, 2);
  private static final NotesDocument NOTES_3 =
      NotesDocument.createForTest("test3", "http://t3.com", ImmutableList.of(TAG_1, TAG_2), 0, 3);
  private static final NotesDocument NOTES_4 =
      NotesDocument.createForTest("test4", "http://t4.com", ImmutableList.of(TAG_3), 0, 4);
 
  private static final ImmutableList<NotesDocument> TEST_DATA = ImmutableList.of(
      NOTES_1, NOTES_2, NOTES_3, NOTES_4);
  
  @Mock
  private final NotesDatastoreInterface mockStorage = mock(NotesDatastoreInterface.class);
  
  @Inject
  private NotesApiServlet servlet;
  
  @Before
  public void setUp() {
    Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(NotesDatastoreInterface.class).toInstance(mockStorage);
            bind(new TypeLiteral<Optional<Cache>>() {}).toInstance(Optional.<Cache>absent());
          }
        },
        new JsonObjectWriterModule()
        ).injectMembers(this);
    
    when(mockStorage.listNotes()).thenReturn(TEST_DATA);
  }
  
  @Test
  public void testListNotesByTagOrdering() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output = servlet.listNotesByTag(TAGS);
    
    assertThat(output.keySet(), contains(TAG_1, TAG_2, TAG_3));
    assertThat(output.get(TAG_1), contains(NOTES_1, NOTES_3));
    assertThat(output.get(TAG_2), contains(NOTES_2));
    assertThat(output.get(TAG_3), contains(NOTES_4));
  }
  
  @Test
  public void testListNotesByTagSecondaryTag() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(ImmutableList.of(TAG_2));
    
    assertThat(output.keySet(), contains(TAG_2));
    assertThat(output.get(TAG_2), contains(NOTES_2, NOTES_3));
  }
  
  @Test
  public void testListNotesByTagOmissions() {
    LinkedHashMap<String, LinkedList<NotesDocument>> output =
        servlet.listNotesByTag(ImmutableList.of(TAG_1));
    
    assertThat(output.keySet(), contains(TAG_1));
    assertThat(output.get(TAG_1), contains(NOTES_1, NOTES_3));
  }
  
  @Test
  public void testListNotesByTagEmpty() {
    when(mockStorage.listNotes()).thenReturn(ImmutableList.<NotesDocument>of());
    
    LinkedHashMap<String, LinkedList<NotesDocument>> output = servlet.listNotesByTag(TAGS);
    
    assertThat(output.entrySet(), is(empty()));
  }
}
