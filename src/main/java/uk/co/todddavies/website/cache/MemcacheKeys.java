package uk.co.todddavies.website.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;
import uk.co.todddavies.website.notes.data.NotesDocument;

import java.util.LinkedList;

public final class MemcacheKeys {
  
  public enum MemcacheKey {
    NOTES_LIST,
    NOTES_SOY,
    NOTES_DOWNLOADS,
    RSS_FEED,
  }
  
  static ImmutableMap<MemcacheKey, TypeLiteral<?>> EXPECTED_TYPES =
      ImmutableMap.of(
          MemcacheKey.NOTES_LIST,
          new TypeLiteral<ImmutableMap<String, LinkedList<NotesDocument>>>() {},
          MemcacheKey.NOTES_SOY,
          new TypeLiteral<ImmutableMap<String, ImmutableList<ImmutableMap<String, String>>>>() {},
          MemcacheKey.NOTES_DOWNLOADS,
          new TypeLiteral<Long>() {},
          MemcacheKey.RSS_FEED,
          new TypeLiteral<String>() {});
  
  static final ImmutableMap<MemcacheKey, String>  KEY_MAP = 
      ImmutableMap.of(MemcacheKey.NOTES_LIST, "notes-list",
          MemcacheKey.NOTES_SOY, "notes-soy",
          MemcacheKey.NOTES_DOWNLOADS, "notes-dl",
          MemcacheKey.RSS_FEED, "rss-feed");
  
  private MemcacheKeys() {/* Private constructor */}
}
