# Identity Platform — Complete Summary

## Overview

A metadata-driven enterprise identity & access management platform embedded in the ERP system. Manages multi-tenant user identities, authentication (JWT/BCrypt), authorization (RBAC), hierarchical organizational context, session lifecycle, audit logging, and plugin-based extensibility.

**Package:** `com.erp.platform.identity` (backend)  
**Stack:** Spring Boot 3.3.4 / Java 17 / JPA / PostgreSQL / React 18 / TypeScript / Zustand / React Query

---

## 1. Data Model (15 Entities)

All entities extend `BaseEntity` (UUID id, soft-delete, timestamps). Tables use `identity_*` prefix.

| Entity | Table | Purpose |
|--------|-------|---------|
| `Tenant` | `identity_tenants` | Multi-tenant isolation root. Fields: code, name, domain, logoUrl, locale defaults |
| `Organization` | `identity_organizations` | Hierarchical org unit within tenant (parent/child via self-join) |
| `Company` | `identity_companies` | Legal entity within an org (taxId, registrationNumber, currency) |
| `Branch` | `identity_branches` | Physical location of a company |
| `Department` (name: `IdentityDepartment`) | `identity_departments` | Department within a branch (hierarchical, costCenter) |
| `UserAccount` | `identity_users` | Core user: username, email, passwordHash, status, login tracking, lockout fields |
| `Role` | `identity_roles` | Named role (isSystem, tenant-scoped) |
| `Permission` | `identity_permissions` | Atomic permission (resourceType, resource, action, module) |
| `UserRole` | `identity_user_roles` | Many-to-many join: User ↔ Role |
| `RolePermission` | `identity_role_permissions` | Many-to-many join: Role ↔ Permission |
| `UserOrganization` | `identity_user_organizations` | User-Org membership |
| `UserCompany` | `identity_user_companies` | User-Company membership (isDefault flag) |
| `UserSession` | `identity_user_sessions` | Auth session: token, refreshToken, IP, UA, expiresAt, context IDs |
| `UserPreference` | `identity_user_preferences` | One-to-one: language, timezone, date/number format, theme |
| `AuditRecord` | `identity_audit_records` | Audit log: eventType, userId, username, IP, old/new values |

---

## 2. Authentication & Security

### Auth Flow
1. `POST /api/v1/auth/login` → validates credentials (BCrypt), checks lockout (5 failed = 30 min lock, auto-unlock after expiry), creates `UserSession`, returns `{ accessToken, refreshToken, sessionId, user }`
2. Access token: JWT (HS256, 15 min), refresh token: JWT (7 days)
3. `JwtAuthenticationFilter` extracts Bearer token, validates, sets security context
4. `ContextFilter` resolves `RuntimeContext` from session, sets in `RuntimeContextHolder` (ThreadLocal)

### Security Configuration
- `SecurityConfig.java`: CSRF disabled, stateless sessions, public routes = `/auth/login`, `/auth/refresh`, `/auth/logout`, all else authenticated
- Custom `AuthenticationEntryPoint` (401) and `AccessDeniedHandler` (403) return standard `ApiResponse` envelope
- Filter chain order: JwtAuthenticationFilter → ContextFilter

### Password Policy
- BCrypt strength 12
- Minimum 8 chars: uppercase, lowercase, digit, special character
- Lockout after 5 failed attempts for 30 minutes (auto-unlock on successful login if expired)
- Unified error messages (no username enumeration)

### JWT Claims
`sub` (userId), `username`, `email`, `tenantId`, `tenantCode`, `orgId`, `companyId`, `branchId`, `sessionId`, `roles`

---

## 3. Authorization (RBAC)

### Architecture
- **`PermissionEvaluator`** — core engine: checks user permissions against resource type/resource/action. ADMIN action = wildcard. Detects `sys_admin`/`tnt_admin` roles (bypass all checks).
- **`PermissionResolver`** — resolves user permissions from DB via join chain: `UserRole → RolePermission → Permission`. Batch query `findByRoleIdIn()` eliminates N+1.
- **`RoleResolver`** — resolves user roles, assignment/removal with cache invalidation.
- **`PermissionCache`** — ConcurrentHashMap cache per userId: TTL 5 min, max 10K entries, scheduled eviction every 60s.
- **`AuthorizationService`** — high-level facade: `hasPermission()`, `checkPermission()`, `hasModuleAccess()`, `isAdmin()`, `getEffectivePermissions()`, `invalidateCache()`.
- **`@RequirePermission`** — annotation-based AOP guard: `AuthorizationInterceptor` checks permission before method execution.

### Permission Standard
Permissions are stored as: `{resourceType}:{resource}:{action}` (e.g., `user:*:read`, `report:sales:export`)
Special role `sys_admin`/`tnt_admin` grants all permissions. ADMIN action on any resource grants all actions on that resource.

---

## 4. Runtime Context

Every authenticated request has a `RuntimeContext` (stored in `RuntimeContextHolder` ThreadLocal, cleared after request):

**Fields:** userId, username, tenant (id+code+name), organization (id+code+name), company (id+code+name), branch (id+code+name), department (id+code+name), roles, permissions, language, timezone, currency, dateFormat, numberFormat, theme, ipAddress, sessionId

**Resolution:** `RuntimeContextService` — auto-resolves hierarchy (picks single option if only one available). `POST /context/switch` to change context.

---

## 5. Audit Events

- **17 event types** (`IdentityEventType` enum): user CRUD, login, logout, role/permission assignment, context changes, session lifecycle
- **`IdentityEventPublisher`** — convenience methods (`loginSuccess()`, `roleAssigned()`, etc.)
- **`IdentityEventListener`** — `@EventListener` converts events to `AuditRecord` and persists

---

## 6. Controllers & API Routes

### Auth (`/api/v1/auth`)
| Route | Controller | Purpose |
|-------|-----------|---------|
| `POST /login` | `AuthController` | Login with username/password |
| `POST /refresh` | `AuthController` | Refresh access token |
| `POST /logout` | `AuthController` | End session |
| `GET /me` | `AuthController` | Current user info |
| `POST /change-password` | `AuthController` | Change password |
| `GET /permissions` | `AuthorizationController` | Effective permissions |
| `GET /check-permission` | `AuthorizationController` | Check specific permission |
| `POST /permissions/invalidate` | `AuthorizationController` | Invalidate permission cache |

### Context (`/api/v1/context`)
| Route | Controller | Purpose |
|-------|-----------|---------|
| `GET /current` | `ContextController` | Current runtime context |
| `GET /options` | `ContextController` | Available context options |
| `POST /switch` | `ContextController` | Switch active context |

### Admin (`/api/v1/identity/`)
| Route Prefix | Controller | Purpose |
|-------------|-----------|---------|
| `/users` | `UserAdminController` | User CRUD, role/org/company assignment, preferences |
| `/roles` | `RoleAdminController` | Role CRUD, clone, permission assignment |
| `/permissions` | `PermissionAdminController` | Permission listing, filter by module/type |
| `/tenants` | `TenantAdminController` | Tenant CRUD |
| `/organizations` | `OrganizationAdminController` | Org CRUD, tree |
| `/companies` | `CompanyAdminController` | Company CRUD, filter by org |
| `/branches` | `BranchAdminController` | Branch CRUD, filter by company |
| `/departments` | `DepartmentAdminController` | Department CRUD, filter by branch |
| `/sessions` | `SessionAdminController` | Session management, force logout |
| `/audit` | `AuditController` | Audit log query by user/type/date |

All controllers return `ResponseEntity<ApiResponse<T>>`.

---

## 7. Platform SDK (`com.erp.platform.identity.sdk`)

Independent package for business modules to consume identity services without database access.

### Entry Points

| Component | Use Case |
|-----------|----------|
| `IdentityFacade` | Single-inject bean wrapping all 4 providers. Recommended for Spring components. |
| `IdentityClient` | Non-Spring contexts (scheduled tasks, batch jobs). Takes explicit userId. |
| `AuthorizationHelper` | Static `currentUserHasPermission()`, `requirePermission()`, `isSystemAdmin()` |
| `ContextHelper` | Static `tenantId()`, `orgId()`, `userId()`, etc. (returns String or null) |

### Providers

| Provider | Interface + Impl | Key Methods |
|----------|-----------------|-------------|
| CurrentUser | `CurrentUserProvider` / `CurrentUserProviderImpl` | `getCurrentUser()`, `getCurrentUserId()`, `getCurrentUsername()`, `getCurrentUserEmail()`, `getCurrentUserDisplayName()`, `isAuthenticated()` |
| CurrentContext | `CurrentContextProvider` / `CurrentContextProviderImpl` | `getCurrentContext()`, `getCurrentTenantId()`, `getCurrentOrganizationId()`, `getCurrentCompanyId()`, `getCurrentBranchId()`, `getCurrentDepartmentId()`, `getCurrentRoles()`, `getCurrentPermissions()`, `getCurrentLanguage()`, `getCurrentTimezone()` |
| Permission | `PermissionProvider` / `PermissionProviderImpl` | `hasPermission(type, resource, action)`, `hasAnyPermission()`, `hasModuleAccess()`, `isAdmin()`, `checkPermission()`, `getEffectivePermissions()` |
| Session | `SessionProvider` / `SessionProviderImpl` | `getCurrentSessionId()`, `getCurrentSession()`, `getActiveSessions(userId)`, `forceLogout(sessionId)`, `isSessionActive(sessionId)` |

### Annotations

| Annotation | Target | Effect |
|-----------|--------|--------|
| `@CurrentUser` | Controller parameter | Injects `UserAccount` entity |
| `@CurrentContext` | Controller parameter | Injects `RuntimeContext` |
| `@EnableTenantFilter` | Method/Class | Enables Hibernate tenant/org/company filters via AOP |

### Plugin System (SPI)

Modules implement `PluginProvider` to register:
- **Permissions** → auto-persisted via `PermissionRegistry`
- **Roles** → auto-created with permission links via `RoleRegistry`
- **Menus** → dynamic nav items, filterable by user permissions via `MenuRegistry`
- **Context enrichments** → extend `RuntimeContext` with custom data via `ContextExtensionRegistry`

`PluginRegistryManager` orchestrates all registries at startup (`@PostConstruct`).

### Integrations

| Integration | Purpose |
|-------------|---------|
| `MetadataIntegration` | Check access to metadata models, views, actions |
| `WorkflowIntegration` | Check workflow state transition permissions |
| `NotificationIntegration` | Resolve notification recipients |
| `SchedulerIntegration` | Check scheduled task execution permissions |
| `AuditIntegration` | Record audit events from business code |

### Multi-Tenant Hibernate Filters

- `TenantFilter.java` — defines `@FilterDef` for tenant, organization, company
- `TenantFilterAspect.java` — `@Around` on `@EnableTenantFilter`: enables filters before method, disables after
- Entities must declare `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` etc.

---

## 8. Frontend

### Pages & Routes

| Route | Page | Purpose |
|-------|------|---------|
| `/login` | `LoginPage` | Login form → `authService.login()` React Query mutation |
| `/forgot-password` | `ForgotPasswordPage` | Email form (no backend yet) |
| `/reset-password` | `ResetPasswordPage` | Token-based reset (no backend yet) |
| `/app/profile` | `ProfilePage` | User display, roles, permissions |
| `/app/preferences` | `PreferencesPage` | Theme/language/timezone/format selectors |
| `/app/change-password` | `ChangePasswordPage` | Current + new password form |
| `/app/sessions` | `SessionsPage` | Active sessions list, force logout |
| `/app/admin/tenants` | `TenantsAdminPage` | Tenants CRUD table |
| `/app/admin/organizations` | `OrganizationsAdminPage` | Orgs CRUD table |
| `/app/admin/companies` | `CompaniesAdminPage` | Companies CRUD table |
| `/app/admin/branches` | `BranchesAdminPage` | Branches CRUD table |
| `/app/admin/departments` | `DepartmentsAdminPage` | Departments CRUD table |
| `/app/admin/users` | `UsersAdminPage` | Users CRUD + role chips |
| `/app/admin/roles` | `RolesAdminPage` | Roles CRUD table |
| `/app/admin/permissions` | `PermissionsAdminPage` | Permissions + action chips |
| `/app/admin/sessions` | `SessionsAdminPage` | All-user session management |
| `/app/admin/audit` | `AuditPage` | Audit log with event type + username filters |

### Shared Components

- **`AdminListPage<T>`** — generic admin table with columns, loading/error/empty states, refresh/create/edit/delete actions via `ColumnDef<T>`
- **`ContextSwitcher`** — dropdown with tenant/org/company chips, switch context action
- **`UserMenu`** — avatar dropdown with profile, preferences, theme toggle, logout

### State Management

- **Zustand** `authStore` — persisted to localStorage: user, token, refreshToken
- **React Query** — `useMutation` for login (replaces mock)
- **apiClient** (axios) — Bearer token interceptor, 401 → auto-logout

---

## 9. Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| Identity independent from business modules | Clean separation; can be extracted as microservice later |
| SDK package with provider interfaces | Business modules never access identity DB directly |
| `Optional` return types in SDK | Null-safe API for consumers |
| ThreadLocal `RuntimeContextHolder` | Per-request context without passing through every method |
| Hibernate filters for multi-tenancy | Transparent filtering at query level rather than manual WHERE clauses |
| Plugin system SPI | Modules extend identity without modifying identity code |
| ConcurrentHashMap cache + @Scheduled eviction | Simple, no external cache dependency, production-capable for single node |
| Flyway disabled; ddl-auto=update | Dev velocity; revisit for production multi-instance deployment |

---

## 10. Tests (22 passing)

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `PasswordServiceTest` | 11 | BCrypt encode/match, policy validation (8 cases), lockout logic, failed attempt tracking, reset |
| `PermissionCacheTest` | 5 | Get/put, empty cache, invalidation (single + all), version tracking |
| `PermissionEvaluatorTest` | 7 | Grant/deny, admin wildcard, any-permission, module access, exception on deny, admin role detection |
| `JwtProviderTest` | 4 | Access/refresh token gen + validation, invalid token rejection, subject extraction |

---

## 11. Known Limitations

1. **No pagination** on admin list endpoints — returns full result sets
2. **No forgot/reset password** backend — frontend pages exist but hit 404
3. **No CORS config** — frontend dev server on 5173 will be blocked by browser
4. **Frontend ContextSwitcher** — not fully wired (wrong payload shape)
5. **Preferences page** — save is local simulation, no API call
6. **No frontend shared types** — admin pages define inline interfaces
7. **Plugin persistToDatabase()** not automatically called — must be invoked explicitly
8. **DatabaseConnectionTest** fails due to `@EntityScan("com.erp.modules")` scope

---

## 12. Frontend API Base URL

Configured in `frontend/src/core/api/env.ts`:
```
VITE_API_URL = http://localhost:8081/api/v1
```
Backend runs on port 8081, routes under `/api/v1/`.

---

## 13. Running The Platform

```bash
# Backend
cd backend
./start.sh                    # Spring Boot on :8081

# Frontend
cd frontend
./start.sh                    # Vite on :5173
```
