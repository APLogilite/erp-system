package com.erp.platform.identity.authorization;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PermissionCache {

  private static final Logger log = LoggerFactory.getLogger(PermissionCache.class);

  private final Map<UUID, CacheEntry<List<PermissionEntry>>> userPermissionsCache = new ConcurrentHashMap<>();
  private final Map<UUID, CacheEntry<List<String>>> userRolesCache = new ConcurrentHashMap<>();
  private final long ttlMs;
  private final int maxSize;
  private long version = 0;

  public PermissionCache(
      @Value("${app.cache.permission.ttl-ms:300000}") long ttlMs,
      @Value("${app.cache.permission.max-size:10000}") int maxSize) {
    this.ttlMs = ttlMs;
    this.maxSize = maxSize;
  }

  public List<PermissionEntry> getUserPermissions(UUID userId) {
    CacheEntry<List<PermissionEntry>> entry = userPermissionsCache.get(userId);
    if (entry == null || entry.isExpired(Instant.now())) {
      if (entry != null) {
        userPermissionsCache.remove(userId);
      }
      return Collections.emptyList();
    }
    return entry.value();
  }

  public void putUserPermissions(UUID userId, List<PermissionEntry> permissions) {
    if (userPermissionsCache.size() >= maxSize) {
      evictStale();
    }
    userPermissionsCache.put(userId, new CacheEntry<>(permissions, Instant.now().plusMillis(ttlMs)));
  }

  public List<String> getUserRoles(UUID userId) {
    CacheEntry<List<String>> entry = userRolesCache.get(userId);
    if (entry == null || entry.isExpired(Instant.now())) {
      if (entry != null) {
        userRolesCache.remove(userId);
      }
      return Collections.emptyList();
    }
    return entry.value();
  }

  public void putUserRoles(UUID userId, List<String> roles) {
    if (userRolesCache.size() >= maxSize) {
      evictStale();
    }
    userRolesCache.put(userId, new CacheEntry<>(roles, Instant.now().plusMillis(ttlMs)));
  }

  public void invalidateUser(UUID userId) {
    userPermissionsCache.remove(userId);
    userRolesCache.remove(userId);
    version++;
    log.debug("Invalidated permission cache for user: {}", userId);
  }

  public void invalidateAll() {
    userPermissionsCache.clear();
    userRolesCache.clear();
    version++;
    log.debug("Invalidated all permission caches");
  }

  public long getVersion() { return version; }

  @Scheduled(fixedRateString = "${app.cache.permission.cleanup-ms:60000}")
  public void evictStale() {
    Instant now = Instant.now();
    userPermissionsCache.entrySet().removeIf(e -> e.getValue().isExpired(now));
    userRolesCache.entrySet().removeIf(e -> e.getValue().isExpired(now));
  }

  private record CacheEntry<T>(T value, Instant expiresAt) {
    boolean isExpired(Instant now) { return now.isAfter(expiresAt); }
  }

  public static class PermissionEntry {
    private final String resourceType;
    private final String resource;
    private final String action;

    public PermissionEntry(String resourceType, String resource, String action) {
      this.resourceType = resourceType;
      this.resource = resource;
      this.action = action;
    }

    public String getResourceType() { return resourceType; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
  }
}
