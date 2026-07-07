# Final Architecture Plan — Role-Scoped Access Control

## Core Model

```
System Tenant (SYS)                  ACME Tenant                     GLOBEX Tenant
├── sys_admin role ─── admin         ├── tnt_admin ─── jane.smith    ├── tnt_admin ─── diana.prince
│   NO RoleOrg = full System         │   NO RoleOrg = full ACME      │   NO RoleOrg = full GLOBEX
├── tnt_admin (template)             ├── user ─── john.doe, alice    ├── user ─── bob, charlie
├── user (template)                  │   Org: ACME-GLOBAL            │   Org: GLOBEX-CORP
├── viewer (template)                │   Co: ACME-INC, Br: HO        │   Co: GLOBEX-LTD, Br: GX-HQ
                                     ├── sales_exec ─── multi-br     └── viewer
                                     │   Org: ACME-GLOBAL
                                     │   Co: ACME-INC, Br: HO
                                     ├── warehouse_op ─── multi-br
                                     │   Org: ACME-GLOBAL
                                     │   Co: ACME-INC, Br: NB
                                     ├── manager ─── multi-role
                                     │   Org: ACME-GLOBAL
                                     └── viewer ─── multi-role
                                         Org: ACME-GLOBAL
```

### Access Inheritance Rules
- **Org access** → ALL companies under that org + ALL branches under those companies
- **Company access** (RoleCo) → ALL branches under that company
- **Branch access** (RoleBranch) → only that branch
- **NO RoleOrg entries** → full access to ALL orgs/companies/branches within the role's tenant
- **UNION across ALL user's roles** → effective access scope

---

## Context Selection Flow (Role-First)

Login → Context Select Page:

1. **Role selector at the top** — user picks which role to operate as
2. **Role determines tenant** — each role belongs to one tenant; tenant auto-fills
3. **Role determines available orgs/companies/branches:**
   - If role has `fullAccess` (no RoleOrg) → show ALL orgs/co/branches under that tenant
   - If role has specific RoleOrg/Co/Branch → show only those
4. **Cascading selection** within the role's scope picks the exact context
5. **Single profile auto-route** — if only one possible combination → skip page

### API Enhancement: `roleScopes` in options response

```json
{
  "tenants": [{"id":"t1","name":"ACME"}],
  "organizations": [{"id":"o1","name":"ACME-GLOBAL","parentId":"t1"}],
  "companies": [{"id":"c1","name":"ACME-INC","parentId":"o1"}],
  "branches": [{"id":"b1","name":"HO","parentId":"c1"}],
  "roles": ["tnt_admin", "user", "sales_executive"],
  "roleScopes": {
    "tnt_admin": {
      "fullAccess": true,
      "tenantId": "t1",
      "organizationIds": [],
      "companyIds": [],
      "branchIds": []
    },
    "user": {
      "fullAccess": false,
      "tenantId": "t1",
      "organizationIds": ["o1"],
      "companyIds": ["c1"],
      "branchIds": ["b1"]
    }
  }
}
```

Frontend reads `roleScopes` to dynamically filter dropdowns when role changes.

---

## New Entities

### `RoleOrganization` → `identity_role_organizations`
- `role` (ManyToOne → Role), `organization` (ManyToOne → Organization), UNIQUE(role_id, org_id)

### `RoleCompany` → `identity_role_companies`
- `role` (ManyToOne → Role), `company` (ManyToOne → Company), UNIQUE(role_id, co_id)

### `RoleBranch` → `identity_role_branches`
- `role` (ManyToOne → Role), `branch` (ManyToOne → Branch), UNIQUE(role_id, branch_id)

Repositories: `findByRoleId(UUID)`, `findByRoleIdIn(List<UUID>)`

---

## Removed

| File | Reason |
|------|--------|
| `UserOrganization.java` + repo | Replaced by RoleOrganization |
| `UserCompany.java` + repo | Replaced by RoleCompany |
| `UserBranch.java` + repo | Replaced by RoleBranch |
| `TenantFilterAspect.java` | No longer needed |
| `EnableTenantFilter.java` | No longer needed |
| `@Filter(name = "tenantFilter")` on 5 entities | Replaced by AccessScopeService |
| `@FilterDef` on Organization | No longer needed |

---

## New Service: `AccessScopeService`

```
getAccessibleOrganizationIds(userId):
  for each role of user:
    ents = roleOrgRepo.findByRoleId(roleId)
    if ents is empty → all orgs under role.tenant
    else → ents' org IDs
  return UNION

getAccessibleCompanyIds(userId):
  for each role of user:
    ents = roleCoRepo.findByRoleId(roleId)
    if ents not empty → those company IDs
    else → all companies under accessible orgs for this role
  return UNION

getAccessibleBranchIds(userId):
  for each role of user:
    ents = roleBranchRepo.findByRoleId(roleId)
    if ents not empty → those branch IDs
    else → all branches under accessible companies for this role
  return UNION
```

### Additional methods (for role-first flow):

```
getRoleScopes(userId) → Map<roleCode, RoleScope>
  where RoleScope = { fullAccess, tenantId, orgIds, coIds, branchIds }

getAccessForRole(roleId) → RoleScope
  Builds the scope for a single role (used by frontend options endpoint)
```

---

## AdminService Rewrite

All methods use `AccessScopeService` instead of `@EnableTenantFilter`:

- `getAllOrganizations()` → `orgRepo.findAllById(scope.getAccessibleOrganizationIds(userId))`
- `getAllCompanies()` → `coRepo.findAllByIdIn(scope.getAccessibleCompanyIds(userId))`
- `getAllBranches()` → `branchRepo.findAllByIdIn(scope.getAccessibleBranchIds(userId))`
- `getAllDepartments()` → `deptRepo.findByBranchIdIn(scope.getAccessibleBranchIds(userId))`
- `getOrganization(id)` → verify `id` in accessible list
- All CRUD methods check access scope before operating

---

## RuntimeContextService Rewrite

**`getAvailableOptions(userId)`** — returns `ContextOptionsResponse` with `roleScopes`:
- Tenants: distinct tenant IDs from accessible orgs
- Organizations: from `AccessScopeService`
- Companies: from `AccessScopeService`
- Branches: from `AccessScopeService`
- Roles: all roles assigned to user
- `roleScopes`: for each role, compute its scope (orgIds, coIds, branchIds, fullAccess)
- Departments: from `deptRepo.findByBranchIdIn(accessibleBranches)`

**`switchContext(userId, request)`** — validate against role's scope:
- If role has `fullAccess` → allow any org/co/branch under the role's tenant
- If role has restricted scope → verify selection matches
- Persist to UserPreference

**`resolve(userId)`** — remove sys_admin nulling:
- Baseline: first role → first accessible org → first company → first branch
- UserPreference overrides applied on top

---

## Admin Form Auto-Derivation

| Create | Tenant | Parent |
|--------|--------|--------|
| Organization | Current context tenantId | — |
| Company | Current context tenantId | User-selected Organization |
| Branch | Current context tenantId | User-selected Company (Org from Company) |
| Department | Current context tenantId | User-selected Branch |

---

## Seed Data

### System Bootstrap
- Tenant: SYS / System → Org: SYS-ORG
- Role: sys_admin (NO RoleOrg = full System) → admin user
- Template roles (tnt_admin, user, viewer) → NO RoleOrg

### ACME
- jane.smith → tnt_admin (NO RoleOrg = full ACME)
- john.doe, alice.johnson → user (RoleOrg: ACME-GLOBAL, RoleCo: ACME-INC, RoleBranch: HO)
- multi-branch.user → sales_exec (HO) + warehouse_op (NB)
- multi-role.user → manager, user, viewer (all ACME-GLOBAL scope)

### GLOBEX
- diana.prince → tnt_admin (NO RoleOrg = full GLOBEX)
- bob.wilson, charlie.brown → user (GLOBEX-CORP, GLOBEX-LTD, GX-HQ)

---

## Test Users Summary

| Username | Password | Role | Context Flow |
|----------|----------|------|-------------|
| admin | Admin@123 | sys_admin (SYS) | Role → System Org → auto-route (1 profile) |
| auto.user | User@123 | user (ACME) | Role → ACME-GLOBAL → ACME-INC → HO → auto-route |
| super.user | User@123 | sys_admin (SYS) | Same as admin |
| jane.smith | User@123 | tnt_admin (ACME) | Role → full ACME → pick anything |
| john.doe | User@123 | user (ACME) | Role → ACME-GLOBAL → ACME-INC → HO → auto-route |
| multi-co.user | User@123 | user (ACME) | Role → ACME-GLOBAL → pick ACME-INC or ACME-EU |
| multi-branch.user | User@123 | sales_exec OR warehouse_op | Role determines branch. 2 roles = 2 profiles |
| multi-role.user | User@123 | manager OR user OR viewer | Pick role → auto-rest (all same scope) |
| diana.prince | User@123 | tnt_admin (GLOBEX) | Role → full GLOBEX → pick everything |

---

## Implementation Order

1. Create RoleOrg/RoleCo/RoleBranch entities + repos (6 files)
2. Create AccessScopeService (1 file)
3. Add `roleScopes` to ContextOptionsResponse DTO
4. Rewrite RuntimeContextService (role-scoped options + resolve)
5. Rewrite AdminService (use AccessScopeService)
6. Remove @Filter from 5 entities
7. Delete TenantFilterAspect + EnableTenantFilter (2 files)
8. Delete UserOrg/UserCo/UserBranch (6 files)
9. Rewrite IdentitySeedData (1 file)
10. Update ContextSelectPage (role-first UI with roleScopes)
11. Update ContextSwitcher
12. Restructure frontend (routes/identity/ → modules/identity/)
13. Update imports + compile + test

---

## Files Summary

| | Action | File |
|---|--------|------|
| 1 | CREATE | `entity/RoleOrganization.java` |
| 2 | CREATE | `entity/RoleCompany.java` |
| 3 | CREATE | `entity/RoleBranch.java` |
| 4 | CREATE | `repo/RoleOrganizationRepository.java` |
| 5 | CREATE | `repo/RoleCompanyRepository.java` |
| 6 | CREATE | `repo/RoleBranchRepository.java` |
| 7 | CREATE | `service/AccessScopeService.java` |
| 8 | MODIFY | `dto/ContextOptionsResponse.java` (add roleScopes) |
| 9 | MODIFY | `dto/ContextOption.java` (add scope fields) |
| 10-14 | MODIFY | entity/*.java (remove @Filter) |
| 15 | MODIFY | `service/AdminService.java` |
| 16 | MODIFY | `service/RuntimeContextService.java` |
| 17 | MODIFY | `service/RoleAdminService.java` |
| 18 | MODIFY | `security/ContextFilter.java` |
| 19-20 | DELETE | `TenantFilterAspect.java`, `EnableTenantFilter.java` |
| 21-26 | DELETE | UserOrg/UserCo/UserBranch entities + repos |
| 27 | REWRITE | `IdentitySeedData.java` |
| 28 | MODIFY | `ContextSelectPage.tsx` (role-first UI) |
| 29 | MODIFY | `ContextSwitcher.tsx` |
| 30 | MOVE | `routes/identity/` → `modules/identity/` |
| 31 | MODIFY | `AppRoutes.tsx` (update paths) |
| 32 | MODIFY | `ContextGuard.tsx` |
