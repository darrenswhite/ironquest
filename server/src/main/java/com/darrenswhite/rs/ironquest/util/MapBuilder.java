package com.darrenswhite.rs.ironquest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Builder used for constructing {@link Map}s.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Darren S. White
 */
public class MapBuilder<K, V> {

  private final Map<K, V> map;

  public MapBuilder() {
    this(HashMap::new);
  }

  public MapBuilder(Supplier<Map<K, V>> mapFactory) {
    this.map = mapFactory.get();
  }

  public MapBuilder<K, V> put(K key, V value) {
    map.put(key, value);
    return this;
  }

  public Map<K, V> build() {
    return map;
  }
}
