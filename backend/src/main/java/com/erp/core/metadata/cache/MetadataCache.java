package com.erp.core.metadata.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe metadata cache using ConcurrentHashMap.
 * Design supports future migration to Caffeine or Redis.
 */
public class MetadataCache {

  private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

  public Object get(String key) {
    return cache.get(key);
  }

  public void put(String key, Object value) {
    cache.put(key, value);
  }

  public void evict(String key) {
    cache.remove(key);
  }

  public void clear() {
    cache.clear();
  }

  public boolean containsKey(String key) {
    return cache.containsKey(key);
  }

  public Map<String, Object> getAll() {
    return new HashMap<>(cache);
  }

  public int size() {
    return cache.size();
  }
}
