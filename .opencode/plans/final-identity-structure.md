# Final Identity Structure — Plan ref: ID-STR-001

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

### Context Selection (Role-First)
1. Role selector at top → determines tenant + access scope
2. Cascading selection within role's scope
3. `roleScopes` map in options response enables client-side filtering

---

## Task Breakdown

### Task 001: Role-Scoped Entities + Repos
**Files:** 6 creates
- `entity/RoleOrganization.java` — role_id FK, org_id FK, UNIQUE
- `entity/RoleCompany.java` — role_id FK, co_id FK, UNIQUE
- `entity/RoleBranch.java` — role_id FK, branch_id FK, UNIQUE
- `repo/RoleOrganizationRepository.java` — findByRoleId, findByRoleIdIn
- `repo/RoleCompanyRepository.java` — findByRoleId, findByRoleIdIn
- `repo/RoleBranchRepository.java` — findByRoleId, findByRoleIdIn

### Task 002: AccessScopeService
**Files:** 1 create
- `service/AccessScopeService.java` — getAccessibleOrganizationIds, getAccessibleCompanyIds, getAccessibleBranchIds, getRoleScopes, getAccessForRole

### Task 003: DTO + RuntimeContextService Rewrite
**Files:** 1 modify, 1 modify
- `dto/ContextOptionsResponse.java` — add `roleScopes: Map<String, RoleScope>` field
- `service/RuntimeContextService.java` — getAvailableOptions uses AccessScopeService + returns roleScopes; switchContext validates against role scope; resolve removes sys_admin nulling

### Task 004: AdminService Rewrite
**Files:** 1 modify, 1 modify
- `service/AdminService.java` — replace @EnableTenantFilter + findAll() with AccessScopeService queries; all CRUD validates against scope
- `service/RoleAdminService.java` — remove @EnableTenantFilter

### Task 005: Remove Filter Infrastructure
**Files:** 5 modify, 2 delete, 1 modify
- `entity/Organization.java` — remove @Filter, @FilterDef
- `entity/Company.java` — remove @Filter
- `entity/Branch.java` — remove @Filter
- `entity/Department.java` — remove @Filter
- `entity/Role.java` — remove @Filter
- `security/TenantFilterAspect.java` — DELETE
- `sdk/annotation/EnableTenantFilter.java` — DELETE
- `security/ContextFilter.java` — simplify (remove multi-level block)

### Task 006: Remove Old User Entity Tables
**Files:** 6 delete
- `entity/UserOrganization.java` — DELETE
- `entity/UserCompany.java` — DELETE
- `entity/UserBranch.java` — DELETE
- `repo/UserOrganizationRepository.java` — DELETE
- `repo/UserCompanyRepository.java` — DELETE
- `repo/UserBranchRepository.java` — DELETE

### Task 007: Seed Data Rewrite
**Files:** 1 rewrite
- `IdentitySeedData.java` — System Tenant + Org + admin role; all roles scoped to their tenants; RoleOrg/Co/Br assignments for test users; remove all UserOrg/UserCo/UserBranch seed calls

### Task 008: Frontend ContextSelectPage — Role-First UI
**Files:** 1 modify
- `ContextSelectPage.tsx` — role selector at top; use roleScopes to filter org/co/branch dropdowns; single-profile auto-route; disabled single-option fields

### Task 009: Frontend ContextSwitcher + ContextGuard
**Files:** 2 modify
- `ContextSwitcher.tsx` — show current role; hide "Change Workspace" when single profile
- `ContextGuard.tsx` — multi-level check using options + current context

### Task 010: Frontend Folder Restructure
**Files:** ~15 move + import updates
- `src/routes/identity/*` → `src/modules/identity/*`
- `AppRoutes.tsx` — update import paths

### Task 011: Compile + Fix + Test
**Files:** 0 new — fix compilation errors
- Backend: `mvn compile` → fix issues
- Frontend: `pnpm lint + typecheck` → fix issues
- Integration: login as each test user, verify scope matches expected

---

## Entity Lifecycle

### RoleOrganization
```
RoleOrganization
├── id: UUID (PK)
├── role: ManyToOne → Role (NOT NULL)
├── organization: ManyToOne → Organization (NOT NULL)
├── UNIQUE(role_id, organization_id)
```

### RoleCompany
```
RoleCompany
├── id: UUID (PK)
├── role: ManyToOne → Role (NOT NULL)
├── company: ManyToOne → Company (NOT NULL)
├── UNIQUE(role_id, company_id)
```

### RoleBranch
```
RoleBranch
├── id: UUID (PK)
├── role: ManyToOne → Role (NOT NULL)
├── branch: ManyToOne → Branch (NOT NULL)
├── UNIQUE(role_id, branch_id)
```

---

## API: ContextOptionsResponse with roleScopes

```json
{
  "tenants": [...],
  "organizations": [...],
  "companies": [...],
  "branches": [...],
  "roles": ["tnt_admin", "user"],
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
