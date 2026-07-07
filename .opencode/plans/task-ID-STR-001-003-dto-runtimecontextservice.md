# Task ID-STR-001-003: DTO + RuntimeContextService Rewrite

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Add `roleScopes` to the options response DTO. Rewrite `RuntimeContextService` to use `AccessScopeService` for computing available options, validating context switches, and resolving the user's context.

## Files to Modify

### 1. `dto/ContextOptionsResponse.java`
- Add field: `private Map<String, RoleScope> roleScopes;`
- Add getter + setter

### 2. `dto/ContextOptionsResponse.java` (inner class or import)
- Import `AccessScopeService.RoleScope` (or define a `RoleScopeDTO` here)

### 3. `service/RuntimeContextService.java` — Rewrite

#### `getAvailableOptions(UUID userId) → ContextOptionsResponse`
- Tenants: from accessible orgs → get distinct tenant IDs → `tenantRepo.findAllById(tenantIds)`
- Organizations: from `accessScopeService.getAccessibleOrganizationIds(userId)` → `orgRepo.findAllById(ids)`
- Companies: from `accessScopeService.getAccessibleCompanyIds(userId)` → `coRepo.findAllByIdIn(ids)`
- Branches: from `accessScopeService.getAccessibleBranchIds(userId)` → `branchRepo.findAllByIdIn(ids)`
- Roles: from `userRoleRepo.findByUserId(userId)` → extract role codes
- Departments: from `deptRepo.findByBranchIdIn(accessibleBranchIds)`
- roleScopes: from `accessScopeService.getRoleScopes(userId)`
- All options include `parentId` for cascading (tenantId for org, orgId for co, coId for branch)

#### `switchContext(UUID userId, ContextSwitchRequest request) → RuntimeContext`
- Build context from request params
- Validate against `accessScopeService.getAccessForRole(request.getRoleCode()...)` — ensure each selected org/co/branch is within the role's scope
- Persist to `UserPreference` (same as before)
- Return the resolved context

#### `resolve(UUID userId) → RuntimeContext`
- REMOVE the sys_admin special case (no more nulling tenant/org/company)
- Baseline: load first role → first org from that role's scope → first company → first branch
- Apply `UserPreference` overrides on top
- Keep preferences (language, timezone, etc.)

#### `resolve(UUID userId, UUID sessionId) → RuntimeContext`
- Same as above but applies session-level overrides (kept for backward compatibility)
- Remove the old `applyPreferenceOverrides` that references UserOrg/UserCo/UserBranch

## New Dependencies
- `AccessScopeService` (inject)
- Remove: `UserOrganizationRepository`, `UserCompanyRepository`, `UserBranchRepository` (to be deleted later)

## Validation
- `mvn compile` passes
- `/api/v1/context/options` returns `roleScopes` with correct data
- `/api/v1/context/switch` validates against role scope
