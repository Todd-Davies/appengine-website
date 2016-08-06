package uk.co.todddavies.website.cache;

import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

import java.util.LinkedList;

public final class MemcacheKeys {
  
  public static final String NOTES_KEY = "notes-list";
  
  // TODO(td): If the types are enums then we get more type safety
  public static ImmutableMap<String, TypeLiteral<?>> EXPECTED_TYPES =
      ImmutableMap.<String, TypeLiteral<?>>of(
          NOTES_KEY, new TypeLiteral<ImmutableMap<String, LinkedList<NotesDocument>>>() {});
  
  private MemcacheKeys() {/* Private constructor */}
}
