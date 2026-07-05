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
