package uk.co.todddavies.website.cache;

import com.google.common.collect.ImmutableList;
import uk.co.todddavies.website.notes.data.NotesDocument;

import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MemcacheKeys {
  
  public enum MemcacheKey {
    NOTES_LIST,
    NOTES_SOY,
    NOTES_DOWNLOADS
  }
  
  static ImmutableMap<MemcacheKey, TypeLiteral<?>> EXPECTED_TYPES =
      ImmutableMap.<MemcacheKey, TypeLiteral<?>>of(
          MemcacheKey.NOTES_LIST,
          new TypeLiteral<ImmutableMap<String, LinkedList<NotesDocument>>>() {},
          MemcacheKey.NOTES_SOY,
          new TypeLiteral<ImmutableMap<String, ImmutableList<ImmutableMap<String, String>>>>() {},
          MemcacheKey.NOTES_DOWNLOADS,
          new TypeLiteral<Long>() {});
  
  static final ImmutableMap<MemcacheKey, String>  KEY_MAP = 
      ImmutableMap.of(MemcacheKey.NOTES_LIST, "notes-list",
          MemcacheKey.NOTES_SOY, "notes-soy",
          MemcacheKey.NOTES_DOWNLOADS, "notes-dl");
  
  private MemcacheKeys() {/* Private constructor */}
}
