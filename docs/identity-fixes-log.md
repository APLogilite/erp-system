# Identity Platform — Fixes Log

Brief notes on issues encountered and their resolutions during development.

## Bean Name Conflicts (identity vs modules)

Identity beans shared simple names with business module beans (both scanned by Spring), causing `UnsatisfiedDependencyException`.

| Bean | Fix |
|------|-----|
| `PermissionEvaluator` | `@Component("identityPermissionEvaluator")` |
| `AuditController` | `@RestController("identityAuditController")` |
| `AuthController` | `@RestController("identityAuthController")` |
| `PermissionRegistry` (SDK) | `@Component("identityPermissionRegistry")` |
| `DepartmentRepository` | `@Repository("identityDepartmentRepository")` |

**Lesson:** All identity beans need explicit names to avoid collision with `com.erp.modules.*`.

## Hibernate Entity Name Conflict

`Department` existed in both `com.erp.platform.identity.entity` and `com.erp.modules.hr.entity`. Hibernate requires unique entity names per `PersistenceUnit`.

**Fix:** `@Entity(name = "IdentityDepartment")` on identity's `Department.java`.

**Lesson:** Any identity entity whose simple name overlaps with a modules entity needs a custom JPA entity name.

## Missing @Component on SDK Beans

`MenuRegistry` and `ContextExtensionRegistry` were plain Java records/classes — Spring could not inject them into `PluginRegistryManager`.

**Fix:** Added `@Component` to both classes.

**Lesson:** All SDK beans consumed by Spring-managed components need `@Component` or equivalent stereotype.

## Entity Scan Scope

Pre-existing `DatabaseConnectionTest` fails because `@EntityScan` only covers `com.erp.modules`, not `com.erp.platform.identity`.

**Status:** Pre-existing, not identity-caused. Needs `@EntityScan` update if running integration tests against identity DB.

## Flyway Disabled

Migration scripts (`V1__init_identity_schema.sql`, `V2__identity_audit_events.sql`) exist but Flyway is disabled (`spring.flyway.enabled=false`). Schema is created via JPA `ddl-auto=update`.

**Note:** Re-enable Flyway if migrating to managed schema versioning. Ensure identity tables use `identity_*` naming to avoid conflict with modules tables.

## Tenant Data Isolation (Hibernate Filters)

Added server-side tenant data isolation to prevent cross-tenant data leaks.

### Entities with `@Filter(name = "tenantFilter")`
- `Organization` — filters by `tenant_id`
- `Company` — added `tenant_id` column + `@Filter`
- `Branch` — added `tenant_id` column + `@Filter`
- `Department` — added `tenant_id` column + `@Filter`
- `Role` — filters by `tenant_id = :tenantId OR tenant_id IS NULL` (shows tenant roles + system-wide roles)

### `TenantFilter.java` Fix
Changed `@ParamDef` type from `String.class` to `UUID.class` to match actual UUID columns.

### `RuntimeContextService.resolve()` Fix
- Context now resolves from user's **actual org/company memberships** (`UserOrganization`, `UserCompany`)
- **sys_admin** users get `null` tenant context → Hibernate filters skip → see everything
- Other users get their first org's tenant context → filters apply

### Service Methods with `@EnableTenantFilter`
- `AdminService`: `getAllOrganizations()`, `getAllCompanies()`, `getAllBranches()`, `getAllDepartments()`
- `RoleAdminService`: `getAllRoles()`
- `UserAdminService.getAllUsers()`: manual tenant filtering via `UserOrganization.findByOrganizationTenantId()`
- `SessionAdminService.getActiveSessions()`: filters by `tenantId` from context

### Login Response
- `LoginResponse.UserInfo` now includes `roles` and `permissions` lists
- `AuthenticationService` injects `PermissionResolver` to compute effective permissions
- JWT tokens now carry `tenantId`, `organizationId`, `companyId` claims
- `UserSession` records have `tenantId`, `organizationId`, `companyId` populated at login

### Seed Data
- All Company, Branch, Department records now set `tenant` relationship
- Added 3 new test users with different scopes (see README)
