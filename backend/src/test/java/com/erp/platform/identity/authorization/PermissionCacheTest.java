package com.erp.platform.identity.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionCacheTest {

    private PermissionCache cache;
    private UUID userId;

    @BeforeEach
    void setUp() {
        cache = new PermissionCache(60000, 100);
        userId = UUID.randomUUID();
    }

    @Test
    void shouldReturnEmptyForUnknownUser() {
        assertThat(cache.getUserPermissions(userId)).isEmpty();
        assertThat(cache.getUserRoles(userId)).isEmpty();
    }

    @Test
    void shouldStoreAndRetrievePermissions() {
        var permissions = List.of(
                new PermissionCache.PermissionEntry("MODULE", "inventory", "READ")
        );
        cache.putUserPermissions(userId, permissions);
        assertThat(cache.getUserPermissions(userId)).hasSize(1);
    }

    @Test
    void shouldStoreAndRetrieveRoles() {
        var roles = List.of("sys_admin");
        cache.putUserRoles(userId, roles);
        assertThat(cache.getUserRoles(userId)).containsExactly("sys_admin");
    }

    @Test
    void shouldInvalidateSingleUser() {
        cache.putUserPermissions(userId, List.of(
                new PermissionCache.PermissionEntry("MODULE", "test", "READ")
        ));
        cache.invalidateUser(userId);
        assertThat(cache.getUserPermissions(userId)).isEmpty();
    }

    @Test
    void shouldInvalidateAll() {
        cache.putUserPermissions(userId, List.of(
                new PermissionCache.PermissionEntry("MODULE", "test", "READ")
        ));
        cache.putUserRoles(userId, List.of("role1"));
        cache.invalidateAll();
        assertThat(cache.getUserPermissions(userId)).isEmpty();
        assertThat(cache.getUserRoles(userId)).isEmpty();
    }

    @Test
    void shouldIncrementVersionOnInvalidation() {
        long v1 = cache.getVersion();
        cache.invalidateUser(userId);
        assertThat(cache.getVersion()).isGreaterThan(v1);
    }
}
