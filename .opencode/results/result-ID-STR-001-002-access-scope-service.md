# Result ID-STR-001-002: AccessScopeService

## Status: ✅ Complete

## File Created

### `service/AccessScopeService.java`
The core access control service. Computes accessible entities from a user's role assignments.

#### Inner Class: `RoleScope`
- `fullAccess: boolean` — true when role has no RoleOrg entries (full access to tenant)
- `tenantId: UUID`
- `organizationIds: List<UUID>`
- `companyIds: List<UUID>`
- `branchIds: List<UUID>`

#### Public Methods
- `getAccessibleOrganizationIds(UUID userId) → List<UUID>` — UNION of all orgs accessible across user's roles
- `getAccessibleCompanyIds(UUID userId) → List<UUID>` — UNION of all companies (from RoleCo or derived from orgs)
- `getAccessibleBranchIds(UUID userId) → List<UUID>` — UNION of all branches (from RoleBranch or derived from companies)
- `getRoleScopes(UUID userId) → Map<String, RoleScope>` — map of role code → RoleScope for all user roles
- `getAccessForRole(UUID roleId) → RoleScope` — scope for a single role by ID

#### Internal Logic
- `getAccessibleOrgIdsForRole(Role)` — if RoleOrg entries exist → those orgs; else → all orgs under role's tenant
- `getAccessibleCoIdsForRole(Role)` — if RoleCo entries exist → those companies; else → all companies under accessible orgs
- `getAccessibleBrIdsForRole(Role)` — if RoleBranch entries exist → those branches; else → all branches under accessible companies

## Validation
- `mvn compile` ✅
