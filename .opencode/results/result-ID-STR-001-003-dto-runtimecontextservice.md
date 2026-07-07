# Result ID-STR-001-003: DTO + RuntimeContextService Rewrite

## Status: ✅ Complete

## Files Modified

### `dto/ContextOptionsResponse.java`
- Added `roleScopes: Map<String, RoleScope>` field + getter/setter
- Import added for `AccessScopeService.RoleScope`

### `service/RuntimeContextService.java` — Full Rewrite

#### Import Changes
- Added: `AccessScopeService`, `AccessScopeService.RoleScope`
- Removed: `UserBranch`, `UserCompany`, `UserOrganization`, `UserBranchRepository`, `UserCompanyRepository`, `UserOrganizationRepository`

#### Constructor — simplified
- Removed 3 User* repositories
- Added `AccessScopeService`

#### `resolve(UUID userId)` / `resolve(UUID userId, UUID sessionId)`
- **Removed** sys_admin nulling of tenant/org/company
- **New baseline**: first role → first accessible org (via AccessScopeService) → first company → first branch
- UserPreference overrides still applied on top

#### `getAvailableOptions(UUID userId)` — Rewritten
- Uses `AccessScopeService` to compute accessible org/co/branch IDs
- Tenants derived from accessible orgs
- Organizations/Companies/Branches loaded from accessible IDs
- Departments loaded from accessible branch IDs
- Includes `roleScopes` map for frontend role-first UI

#### `switchContext(UUID userId, ContextSwitchRequest request)`
- **Replaced** `UserOrg/UserCo` checks with `AccessScopeService.getAccessible*Ids()` checks
- Same UserPreference persistence

## Validation
- `mvn compile` ✅
