# Result ID-STR-001-004: AdminService Rewrite

## Status: ✅ Complete

## Files Modified

### `service/AdminService.java` — Full Rewrite
- Removed `@EnableTenantFilter` (3 occurrences)
- Removed `import com.erp.platform.identity.sdk.annotation.EnableTenantFilter`
- Added `AccessScopeService` dependency
- Added `currentUserId()` helper (reads from `RuntimeContextHolder`)
- Every list method uses `AccessScopeService.getAccessible*Ids()` + JOIN FETCH queries
- Every single-entity getter validates scope
- Every update/delete calls the getter first (scope-validated lookup)
- Every filtered-list method (getByOrg, getByCompany, etc.) applies in-memory scope filter
- Added repository methods for JOIN FETCH + `WHERE id IN`:

### Repository Updates

| Repository | New Method |
|------------|-----------|
| `OrganizationRepository` | `findByIdInWithTenant(List<UUID>)` |
| `CompanyRepository` | `findByIdInWithOrganization(List<UUID>)` |
| `BranchRepository` | `findByIdInWithCompany(List<UUID>)` |
| `DepartmentRepository` | `findByIdInWithBranch(List<UUID>)`, `findByBranchIdInWithBranch(List<UUID>)` |

### `service/RoleAdminService.java`
- Removed `@EnableTenantFilter` from `getAllRoles()`
- Removed import

## Validation
- `mvn compile` ✅
