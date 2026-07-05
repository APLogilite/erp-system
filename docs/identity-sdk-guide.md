# Identity Platform SDK — Developer Guide

## Overview

The Identity SDK (`com.erp.platform.identity.sdk`) provides a clean,
documented API surface for business modules to consume identity services.

Business modules **must never** access identity database tables directly.
They must use the SDK.

## Package Structure

```
com.erp.platform.identity.sdk
├── IdentityFacade.java        — Unified entry point (inject this)
├── IdentityClient.java         — For non-Spring consumers
├── AuthorizationHelper.java    — Static permission helpers
├── ContextHelper.java          — Static context helpers
├── provider/
│   ├── CurrentUserProvider.java
│   ├── CurrentContextProvider.java
│   ├── PermissionProvider.java
│   └── SessionProvider.java
│   └── impl/  (Spring @Component implementations)
├── annotation/
│   ├── CurrentUser.java        — @CurrentUser on controller params
│   ├── CurrentContext.java     — @CurrentContext on controller params
│   └── EnableTenantFilter.java — Enable Hibernate tenant filter
├── resolver/
│   ├── CurrentUserArgumentResolver.java
│   └── CurrentContextArgumentResolver.java
├── filter/
│   ├── TenantFilter.java       — Hibernate @FilterDef definitions
│   └── TenantFilterAspect.java — AOP aspect for @EnableTenantFilter
├── plugin/
│   ├── PluginProvider.java     — SPI for plugins
│   ├── PluginRegistryManager.java
│   ├── PermissionRegistry.java
│   ├── RoleRegistry.java
│   ├── MenuRegistry.java
│   └── ContextExtensionRegistry.java
└── integration/
    ├── MetadataIntegration.java
    ├── WorkflowIntegration.java
    ├── SchedulerIntegration.java
    ├── NotificationIntegration.java
    └── AuditIntegration.java
```

## Usage Patterns

### 1. Injecting IdentityFacade

```java
@Service
public class InventoryService {

    private final IdentityFacade identity;

    public InventoryService(IdentityFacade identity) {
        this.identity = identity;
    }

    public void adjustStock(String productId, int quantity) {
        identity.checkPermission("INVENTORY", "stock", "WRITE");
        String companyId = identity.getCurrentCompanyId();
        // ...
    }
}
```

### 2. Injecting Individual Providers

```java
@Service
public class ReportingService {

    private final CurrentContextProvider context;
    private final PermissionProvider permission;

    public ReportingService(CurrentContextProvider context,
                            PermissionProvider permission) {
        this.context = context;
        this.permission = permission;
    }

    public void generateReport(String reportCode) {
        if (!permission.hasModuleAccess("REPORTING")) {
            throw new SecurityException("Access denied");
        }
        String tenantId = context.getCurrentTenantId();
        // ...
    }
}
```

### 3. Using @CurrentUser and @CurrentContext in Controllers

```java
@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    @GetMapping("/me")
    public ResponseEntity<?> me(@CurrentUser UserAccount user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/context")
    public ResponseEntity<?> context(@CurrentContext RuntimeContext ctx) {
        return ResponseEntity.ok(ctx);
    }
}
```

### 4. Using IdentityClient (Non-Spring Contexts)

```java
// In batch jobs, scheduled tasks, or non-request-scoped contexts:
@Component
public class ReportScheduler {

    private final IdentityClient identityClient;

    public ReportScheduler(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void generateHourlyReports() {
        String adminId = "...";  // system user ID
        if (identityClient.isAdmin(adminId)) {
            // ...
        }
    }
}
```

### 5. Multi-Tenant Filtering

```java
@EnableTenantFilter
@Transactional
public List<Order> findCustomerOrders(String customerId) {
    // Hibernate will automatically add WHERE tenant_id = :tenantId
    // based on the current RuntimeContext
    return orderRepository.findByCustomerId(customerId);
}
```

### 6. Plugin Registration

```java
@Component
public class InventoryPlugin implements PluginProvider {

    @Override
    public String getPluginName() { return "inventory"; }

    @Override
    public String getPluginVersion() { return "1.0.0"; }

    @Override
    public List<RegisteredPermission> getPermissions() {
        return List.of(
            new RegisteredPermission("INV_READ", "View Inventory",
                "INVENTORY", "stock", "READ", "inventory"),
            new RegisteredPermission("INV_WRITE", "Modify Inventory",
                "INVENTORY", "stock", "WRITE", "inventory")
        );
    }

    @Override
    public List<MenuItem> getMenus() {
        return List.of(
            new MenuItem("inventory", "Inventory", "InventoryIcon",
                "/app/inventory", null, 10, "inventory", "MODULE:inventory:READ")
        );
    }
}
```

## Provider API Reference

### CurrentUserProvider

| Method | Returns | Description |
|--------|---------|-------------|
| `getCurrentUser()` | `Optional<UserAccount>` | Full user entity |
| `getCurrentUserId()` | `String` | UUID of current user |
| `getCurrentUsername()` | `String` | Login username |
| `getCurrentUserEmail()` | `String` | Email address |
| `getCurrentUserDisplayName()` | `String` | Display name (or username) |
| `isAuthenticated()` | `boolean` | Whether a user is logged in |

### CurrentContextProvider

| Method | Returns | Description |
|--------|---------|-------------|
| `getCurrentContext()` | `Optional<RuntimeContext>` | Full context POJO |
| `getCurrentTenantId()` | `String` | Active tenant UUID |
| `getCurrentOrganizationId()` | `String` | Active org UUID |
| `getCurrentCompanyId()` | `String` | Active company UUID |
| `getCurrentBranchId()` | `String` | Active branch UUID |
| `getCurrentDepartmentId()` | `String` | Active dept UUID |
| `getCurrentRoles()` | `List<String>` | Role codes |
| `getCurrentPermissions()` | `List<String>` | Permission strings |
| `getCurrentLanguage()` | `String` | Language code |
| `getCurrentTimezone()` | `String` | Timezone ID |

### PermissionProvider

| Method | Returns | Description |
|--------|---------|-------------|
| `hasPermission(type, resource, action)` | `boolean` | Check specific permission |
| `hasAnyPermission(type, resource, actions...)` | `boolean` | Check any of multiple actions |
| `hasModuleAccess(module)` | `boolean` | Check MODULE:module:READ |
| `isAdmin()` | `boolean` | Is sys_admin or tnt_admin |
| `checkPermission(type, resource, action)` | `void` | Throws on denial |
| `getEffectivePermissions()` | `List<String>` | All permission strings |

### SessionProvider

| Method | Returns | Description |
|--------|---------|-------------|
| `getCurrentSessionId()` | `Optional<String>` | Current session UUID |
| `getCurrentSession()` | `Optional<UserSession>` | Current session entity |
| `getActiveSessions(userId)` | `List<UserSession>` | All active sessions |
| `forceLogout(sessionId)` | `void` | Soft-delete a session |
| `isSessionActive(sessionId)` | `boolean` | Session still valid? |

## Testing with the SDK

```java
@SpringBootTest
class SalesServiceTest {

    @MockBean
    private PermissionProvider permissionProvider;

    @Autowired
    private SalesService salesService;

    @Test
    void shouldCreateOrderWhenAuthorized() {
        when(permissionProvider.hasPermission("SALES", "order", "WRITE"))
            .thenReturn(true);
        // ... test logic
    }
}
```
