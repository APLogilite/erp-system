package com.erp.platform.identity.authorization;

import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.RolePermission;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.RolePermissionRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionEvaluatorTest {

    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RolePermissionRepository rolePermissionRepository;

    private PermissionCache permissionCache;
    private PermissionResolver permissionResolver;
    private PermissionEvaluator permissionEvaluator;
    private UUID userId;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        permissionCache = new PermissionCache(60000, 100);
        permissionResolver = new PermissionResolver(userRoleRepository, rolePermissionRepository, permissionCache);
        permissionEvaluator = new PermissionEvaluator(permissionResolver, permissionCache);
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
    }

    private void givenUserHasPermission(String resourceType, String resource, String action) {
        Role role = new Role();
        role.setId(roleId);
        role.setCode("test_role");

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(userRole));

        Permission perm = new Permission();
        perm.setResourceType(resourceType);
        perm.setResource(resource);
        perm.setAction(action);

        RolePermission rp = new RolePermission();
        rp.setPermission(perm);
        when(rolePermissionRepository.findByRoleIdIn(any())).thenReturn(List.of(rp));
    }

    private void givenUserHasRole(String roleCode) {
        Role role = new Role();
        role.setId(roleId);
        role.setCode(roleCode);

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        when(userRoleRepository.findByUserId(userId)).thenReturn(List.of(userRole));
        lenient().when(rolePermissionRepository.findByRoleIdIn(any())).thenReturn(List.of());
    }

    @Test
    void shouldGrantPermission() {
        givenUserHasPermission("MODULE", "inventory", "READ");
        assertThat(permissionEvaluator.hasPermission(userId, "MODULE", "inventory", "READ")).isTrue();
    }

    @Test
    void shouldDenyMissingPermission() {
        givenUserHasPermission("MODULE", "inventory", "READ");
        assertThat(permissionEvaluator.hasPermission(userId, "MODULE", "sales", "READ")).isFalse();
    }

    @Test
    void shouldGrantAdminActionAsWildcard() {
        givenUserHasPermission("MODULE", "inventory", "ADMIN");
        assertThat(permissionEvaluator.hasPermission(userId, "MODULE", "inventory", "WRITE")).isTrue();
        assertThat(permissionEvaluator.hasPermission(userId, "MODULE", "inventory", "DELETE")).isTrue();
    }

    @Test
    void shouldCheckAnyPermission() {
        givenUserHasPermission("MODULE", "inventory", "READ");
        assertThat(permissionEvaluator.hasAnyPermission(userId, "MODULE", "inventory", "READ", "WRITE")).isTrue();
        assertThat(permissionEvaluator.hasAnyPermission(userId, "MODULE", "inventory", "WRITE", "DELETE")).isFalse();
    }

    @Test
    void shouldCheckModuleAccess() {
        givenUserHasPermission("MODULE", "inventory", "READ");
        assertThat(permissionEvaluator.hasModuleAccess(userId, "inventory")).isTrue();
        assertThat(permissionEvaluator.hasModuleAccess(userId, "sales")).isFalse();
    }

    @Test
    void shouldThrowOnCheckPermissionWhenDenied() {
        givenUserHasPermission("MODULE", "inventory", "READ");
        assertThatThrownBy(() -> permissionEvaluator.checkPermission(userId, "MODULE", "sales", "WRITE"))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    void shouldDetectAdminRole() {
        givenUserHasRole("sys_admin");
        assertThat(permissionEvaluator.isAdmin(userId)).isTrue();
    }

    @Test
    void shouldDetectTenantAdminRole() {
        givenUserHasRole("tnt_admin");
        assertThat(permissionEvaluator.isAdmin(userId)).isTrue();
    }

    @Test
    void shouldNotDetectNonAdminRole() {
        givenUserHasRole("user");
        assertThat(permissionEvaluator.isAdmin(userId)).isFalse();
    }
}
