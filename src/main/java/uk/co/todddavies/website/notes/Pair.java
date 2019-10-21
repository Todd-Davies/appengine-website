package uk.co.todddavies.website.notes;

import com.google.common.annotations.VisibleForTesting;

import java.util.Map;

@VisibleForTesting
public final class Pair<K,V> implements Map.Entry<K, V> {

  private final K key;
  private V value;

  Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public static <K, V> Pair<K, V> of(K key, V value) {
    return new Pair<>(key, value);
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(V value) {
    return this.value = value;
  }
}
