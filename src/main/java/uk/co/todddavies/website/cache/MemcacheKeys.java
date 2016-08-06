package uk.co.todddavies.website.cache;

import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

import java.io.Serializable;

public final class MemcacheKeys {
  
  public static final String NOTES_KEY = "notes-list";
  
  public static ImmutableMap<String, TypeLiteral<?>> EXPECTED_TYPES =
      ImmutableMap.<String, TypeLiteral<?>>of(
          NOTES_KEY, new TypeLiteral<ImmutableMap<String, Serializable>>() {});
  
  private MemcacheKeys() {/* Private constructor */}
}
