# Task ID-STR-001-007: Seed Data Rewrite

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Rewrite `IdentitySeedData.java` to use the new role-scoped access model. Add System Tenant with admin role, assign RoleOrg/Co/Br entries to roles instead of UserOrg/Co/Br entries, and create all test users with proper role-scoped access.

## Files to Modify

### `IdentitySeedData.java` — Full Rewrite

#### Import Changes
- Remove: `UserBranchRepository`, `UserCompanyRepository`, `UserOrganizationRepository`
- Add: `RoleOrganizationRepository`, `RoleCompanyRepository`, `RoleBranchRepository`

#### Constructor — update params to include new repos, exclude deleted ones

#### Seed Data Structure (order matters for FK dependencies)

##### 1. Tenants (3)
- `SYS` / System (default_language: en, default_timezone: UTC, default_currency: USD)
- `ACME` / Acme Corporation (existing)
- `GLOBEX` / Globex Industries (existing)

##### 2. Organizations (4)
- `SYS-ORG` / System Organization (tenant: SYS, level 0, path: /SYS-ORG)
- `ACME-GLOBAL` / Acme Global (tenant: ACME)
- `ACME-APAC` / Acme APAC (tenant: ACME)
- `GLOBEX-CORP` / Globex Corp (tenant: GLOBEX)

##### 3. Companies (4)
- `ACME-INC` / Acme Inc. (org: ACME-GLOBAL)
- `ACME-EU` / Acme Europe GmbH (org: ACME-GLOBAL)
- `APAC-INC` / Acme APAC Inc. (org: ACME-APAC)
- `GLOBEX-LTD` / Globex Ltd. (org: GLOBEX-CORP)

##### 4. Branches (5)
- `HO` / Head Office (company: ACME-INC)
- `NB` / North Branch (company: ACME-INC)
- `EU-HQ` / Europe HQ (company: ACME-EU)
- `APAC-HQ` / APAC Headquarters (company: APAC-INC)
- `GX-HQ` / Globex HQ (company: GLOBEX-LTD)

##### 5. Departments (18 — all existing)
HO: ENG, SALES, FIN, LEGAL, IT, HR
NB: SUPPORT, LOGISTICS, SERVICE
EU-HQ: EU-DEV, EU-SALES, EU-MARKETING
APAC-HQ: APAC-ENG, APAC-HR
GX-HQ: GX-OPS, GX-FINANCE, GX-HR, GX-SALES

##### 6. Permissions (35 — same as current)

##### 7. Roles (8)
- `sys_admin` (tenant: SYS, isSystem: true)
- `tnt_admin` (tenant: SYS, isSystem: true)
- `user` (tenant: SYS, isSystem: true)
- `viewer` (tenant: SYS, isSystem: true)
- `manager` (tenant: SYS, isSystem: true)
- `sales_executive` (tenant: SYS, isSystem: true)
- `warehouse_op` (tenant: SYS, isSystem: true)
- `hr_manager` (tenant: SYS, isSystem: true)

Note: All roles are created under SYS tenant as templates. When a new tenant is created, the system clones/creates roles specific to that tenant.

##### 8. Role-Permission Assignments (same as current)

##### 9. Role-Organization Assignments (NEW — replaces UserOrg)

| Role | Organization | Purpose |
|------|-------------|---------|
| sys_admin | SYS-ORG | System admin sees System data |
| tnt_admin | *(none)* | Full access to SYS tenant (no RoleOrg = full tenant) |
| user | *(none)* | Template — no access by default |
| viewer | *(none)* | Template — no access by default |

Note: ACME and GLOBEX roles will get their org assignments in step 11 after role-permission setup.

##### 10. Users (13 — existing 7 + 6 new)
Same as current seed data — `makeUser()` helper unchanged.

##### 11. User-Role Assignments + Role Scopes

For each user, create UserRole + RoleOrg/Co/Br for their roles:

| User | Role | RoleOrg | RoleCo | RoleBranch |
|------|------|---------|--------|------------|
| admin | sys_admin | SYS-ORG | — | — |
| auto.user | user (cloned to ACME) | ACME-GLOBAL | ACME-INC | HO |
| super.user | sys_admin (SYS clone) | *(none)* | — | — |
| jane.smith | tnt_admin (ACME) | *(none)* | — | — |
| john.doe | user (ACME) | ACME-GLOBAL | ACME-INC | HO |
| alice.johnson | user (ACME) | ACME-GLOBAL | ACME-INC | HO |
| multi-org.user | user (ACME) | ACME-GLOBAL, ACME-APAC | ACME-INC, APAC-INC | HO, APAC-HQ |
| multi-co.user | user (ACME) | ACME-GLOBAL | ACME-INC, ACME-EU | HO, EU-HQ |
| multi-branch.user | sales_executive (ACME) | ACME-GLOBAL | ACME-INC | HO |
| multi-branch.user | warehouse_op (ACME) | ACME-GLOBAL | ACME-INC | NB |
| multi-role.user | manager (ACME) | ACME-GLOBAL | ACME-INC | HO |
| multi-role.user | user (ACME) | ACME-GLOBAL | ACME-INC | HO |
| multi-role.user | viewer (ACME) | ACME-GLOBAL | ACME-INC | HO |
| bob.wilson | user (GLOBEX) | GLOBEX-CORP | GLOBEX-LTD | GX-HQ |
| charlie.brown | user (GLOBEX) | GLOBEX-CORP | GLOBEX-LTD | GX-HQ |
| diana.prince | tnt_admin (GLOBEX) | *(none)* | — | — |

Note: For roles that need to exist in ACME/GLOBEX tenants, the seed creates clones of the template roles under those tenants. E.g., `user` role exists in both SYS (template) and ACME (with specific scopes).

##### 12. User Preferences (13 — same as current)

#### New Helper Methods
- `assignRoleOrg(UserAccount u, Role r, Organization o)` — creates RoleOrganization entry
- `assignRoleCo(UserAccount u, Role r, Company c)` — creates RoleCompany entry
- `assignRoleBranch(UserAccount u, Role r, Branch b)` — creates RoleBranch entry

#### Remove Old Helper Methods
- `assignOrg(UserAccount u, Organization o)` — delete
- `assignCompany(UserAccount u, Company c, boolean isDefault)` — delete
- `assignBranch(UserAccount u, Branch b, boolean isDefault)` — delete

## Validation
- `mvn compile` passes
- On restart with empty DB, seed creates all data correctly
- Login as each user, call `/context/options`, verify accessible data matches plan
