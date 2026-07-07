# Task ID-STR-001-002: AccessScopeService

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Create the `AccessScopeService` that computes accessible organizations, companies, and branches based on a user's role assignments. This is the heart of the new access control model.

## Files to Create

### `service/AccessScopeService.java`
- Package: `com.erp.platform.identity.service`
- Depends on: `UserRoleRepository`, `RoleOrganizationRepository`, `RoleCompanyRepository`, `RoleBranchRepository`, `OrganizationRepository`, `CompanyRepository`, `BranchRepository`

## Methods

### `getAccessibleOrganizationIds(UUID userId) → List<UUID>`
1. Load user's roles via `userRoleRepo.findByUserId(userId)`
2. For each role:
   - Load `roleOrgRepo.findByRoleId(roleId)`
   - If entries exist → collect their `organization` IDs
   - If NO entries (empty) → load `role.getTenant()` → load ALL orgs under that tenant via `orgRepo.findByTenantId(tenantId)`
3. Return UNION (distinct) of all org IDs

### `getAccessibleCompanyIds(UUID userId) → List<UUID>`
1. Load user's roles
2. For each role:
   - Load `roleCoRepo.findByRoleId(roleId)`
   - If entries exist → collect those `company` IDs
   - If NO entries → load accessible orgs for this role → load ALL companies under those orgs via `coRepo.findByOrganizationIdIn(orgIds)`
3. Return UNION (distinct) of all company IDs

### `getAccessibleBranchIds(UUID userId) → List<UUID>`
1. Load user's roles
2. For each role:
   - Load `roleBranchRepo.findByRoleId(roleId)`
   - If entries exist → collect those `branch` IDs
   - If NO entries → load accessible companies for this role → load ALL branches under those companies via `branchRepo.findByCompanyIdIn(coIds)`
3. Return UNION (distinct) of all branch IDs

### `getRoleScopes(UUID userId) → Map<String, RoleScope>`
- For each role assigned to user:
  - Build `RoleScope` object: `{ fullAccess: boolean, tenantId: UUID, organizationIds: List<UUID>, companyIds: List<UUID>, branchIds: List<UUID> }`
  - `fullAccess` = `roleOrgRepo.findByRoleId(roleId).isEmpty()` (no RoleOrg entries = full access to tenant)
  - `tenantId` = role's tenant ID
  - `organizationIds` = from `getAccessibleOrganizationIds` filtered to this role
  - `companyIds` = from `getAccessibleCompanyIds` filtered to this role
  - `branchIds` = from `getAccessibleBranchIds` filtered to this role
- Return map keyed by `role.getCode()`

### `getAccessForRole(UUID roleId) → RoleScope`
- Single-role version used by options endpoint
- Same logic as above but for one role only

## Internal Helpers

### `getAccessibleOrgIdsForRole(Role role, List<RoleOrganization> entries) → List<UUID>`
Shared logic: if entries empty → all orgs under role.tenant ; else → entry org IDs

### `getAccessibleCoIdsForRole(Role role, List<RoleCompany> entries, List<UUID> roleOrgIds) → List<UUID>`
Shared logic: if entries empty → all cos under accessible orgs ; else → entry co IDs

### `getAccessibleBrIdsForRole(Role role, List<RoleBranch> entries, List<UUID> roleCoIds) → List<UUID>`
Shared logic: if entries empty → all branches under accessible companies ; else → entry branch IDs

## RoleScope DTO (inner class or separate file)
```
RoleScope {
  boolean fullAccess;
  UUID tenantId;
  List<UUID> organizationIds;
  List<UUID> companyIds;
  List<UUID> branchIds;
}
```

## Validation
- `mvn compile` passes
- Unit test idea: create a user with role → verify accessible orgs match expected
