# User Test Matrix — Identity Structure v1 (ID-STR-001)

## Credentials Quick Reference

| # | Username | Password |
|---|----------|----------|
| 1 | admin | Admin@123 |
| 2 | john.doe | User@123 |
| 3 | jane.smith | User@123 |
| 4 | bob.wilson | User@123 |
| 5 | alice.johnson | User@123 |
| 6 | charlie.brown | User@123 |
| 7 | diana.prince | User@123 |
| 8 | auto.user | User@123 |
| 9 | super.user | User@123 |
| 10 | multi-org.user | User@123 |
| 11 | multi-co.user | User@123 |
| 12 | multi-branch.user | User@123 |
| 13 | multi-role.user | User@123 |

---

## User Access Matrix

### 1. admin — System Administrator
| Property | Value |
|----------|-------|
| Role | sys_admin (SYS tenant) |
| Access Type | **FullAccess** (NO RoleOrg entries) |
| Can See | SYS Tenant only → SYS-ORG |
| Cannot See | ACME tenant data, GLOBEX tenant data |
| Use Case | Infrastructure management, create new tenants |

### 2. john.doe — ACME Regular User
| Property | Value |
|----------|-------|
| Role | user (ACME tenant) |
| Access Type | **Restricted** (RoleOrg: ACME-GLOBAL, RoleCo: ACME-INC, RoleBranch: HO) |
| Can See | ACME tenant → ACME-GLOBAL org → ACME-INC → HO branch |
| Context Flow | Role auto-selected → Org auto → Co auto → Branch auto → **auto-route** (single profile) |

### 3. jane.smith — ACME Tenant Admin
| Property | Value |
|----------|-------|
| Role | tnt_admin (ACME tenant) |
| Access Type | **FullAccess** (NO RoleOrg — sees all ACME) |
| Can See | ACME tenant → ACME-GLOBAL, ACME-APAC orgs → all 3 companies → all 4 branches |
| Context Flow | Role auto-selected → all ACME data shown → pick org/co/branch |

### 4. bob.wilson — GLOBEX Read-Only
| Property | Value |
|----------|-------|
| Role | user (GLOBEX tenant) |
| Access Type | **Restricted** (RoleOrg: GLOBEX-CORP, RoleCo: GLOBEX-LTD, RoleBranch: GX-HQ) |
| Can See | GLOBEX tenant → GLOBEX-CORP org → GLOBEX-LTD → GX-HQ |
| Context Flow | Same as john.doe — single profile auto-route |

### 5. alice.johnson — ACME Regular User (duplicate of john.doe)
| Property | Value |
|----------|-------|
| Role | user (ACME tenant) |
| Access Type | Same as john.doe — ACME-GLOBAL → ACME-INC → HO |
| Context Flow | Same as john.doe |

### 6. charlie.brown — GLOBEX Regular User
| Property | Value |
|----------|-------|
| Role | user (GLOBEX tenant) |
| Access Type | Same as bob.wilson — GLOBEX-CORP → GLOBEX-LTD → GX-HQ |
| Context Flow | Same as bob.wilson |

### 7. diana.prince — GLOBEX Tenant Admin
| Property | Value |
|----------|-------|
| Role | tnt_admin (GLOBEX tenant) |
| Access Type | **FullAccess** (NO RoleOrg — sees all GLOBEX) |
| Can See | GLOBEX tenant → GLOBEX-CORP org → GLOBEX-LTD company → GX-HQ branch |
| Context Flow | Role auto-selected → all GLOBEX data shown → pick org/co/branch |

### 8. auto.user — Auto-Route Test
| Property | Value |
|----------|-------|
| Role | user (ACME tenant) |
| Access Type | Same as john.doe — single profile at every level |
| Can See | Same as john.doe |
| Context Flow | **Should auto-route without showing selection page** |

### 9. super.user — Redundant sys_admin
| Property | Value |
|----------|-------|
| Role | sys_admin (SYS tenant) |
| Access Type | Same as admin — SYS tenant only |
| Context Flow | Same as admin |

### 10. multi-org.user — Multi-Org Test
| Property | Value |
|----------|-------|
| Role | user (ACME tenant) |
| Access Type | RoleOrg: ACME-GLOBAL |
| Can See | ACME → ACME-GLOBAL org → (all companies under ACME-GLOBAL) → (all branches under those) |
| Context Flow | Role auto → ACME-GLOBAL auto → pick company (ACME-INC or ACME-EU) → pick branch |

### 11. multi-co.user — Multi-Company Test
| Property | Value |
|----------|-------|
| Role | user (ACME tenant) |
| Access Type | RoleOrg: ACME-GLOBAL, RoleCo: ACME-INC, ACME-EU |
| Can See | ACME → ACME-GLOBAL → ACME-INC + ACME-EU → their branches |
| Context Flow | Role auto → Org auto → pick company → branch auto |

### 12. multi-branch.user — Multi-Branch + Multi-Role Test
| Property | Value |
|----------|-------|
| Role 1 | sales_executive (ACME) → RoleBranch: HO |
| Role 2 | warehouse_op (ACME) → RoleBranch: NB |
| Can See | ACME → ACME-GLOBAL → ACME-INC → HO (via role 1) or NB (via role 2) |
| Context Flow | **Pick role first** → role determines branch. 2 roles = 2 possible profiles |

### 13. multi-role.user — Multi-Role Test
| Property | Value |
|----------|-------|
| Role 1 | manager (ACME) → ACME-GLOBAL → ACME-INC → HO |
| Role 2 | user (ACME) → same scope |
| Role 3 | viewer (ACME) → same scope |
| Can See | Same org/co/branch for all roles, different permissions per role |
| Context Flow | Pick role (manager/user/viewer) → same org/co/branch auto-filled |

---

## Tenant Isolation Validation

| User | Active Context | Admin > Orgs sees | Admin > Branches sees | Should NOT see |
|------|---------------|-------------------|----------------------|----------------|
| admin | SYS tenant | SYS-ORG only | (none) | ACME-GLOBAL, ACME-APAC, GLOBEX-CORP |
| john.doe | ACME | ACME-GLOBAL | HO | GLOBEX-CORP, GX-HQ |
| jane.smith | ACME | ACME-GLOBAL, ACME-APAC | HO, NB, EU-HQ, APAC-HQ | GLOBEX-CORP, GX-HQ |
| bob.wilson | GLOBEX | GLOBEX-CORP | GX-HQ | ACME-GLOBAL, ACME-APAC |
| diana.prince | GLOBEX | GLOBEX-CORP | GX-HQ | ACME-GLOBAL |
| multi-branch.user | ACME → sales_exec → HO | ACME-GLOBAL | HO | NB, EU-HQ, APAC-HQ, GX-HQ |
| multi-branch.user | ACME → warehouse_op → NB | ACME-GLOBAL | NB | HO, EU-HQ, APAC-HQ, GX-HQ |

## Profile-Based Context Selection

When user has multiple profiles (role+org+co+branch combinations), the Context Select Page shows dropdowns filtered by `roleScopes`. Single-profile users auto-route.

| User | Profiles | Sees Selection Page? |
|------|----------|---------------------|
| admin | 1 (SYS-ORG × sys_admin) | No → auto-route |
| john.doe | 1 (ACME-GLOBAL × ACME-INC × HO × user) | No → auto-route |
| jane.smith | 3 (ACME-GLOBAL→ACME-INC/HO × ACME-EU/EU-HQ × ACME-APAC→APAC-INC/APAC-HQ × tnt_admin) | Yes → cascading |
| bob.wilson | 1 (GLOBEX-CORP × GLOBEX-LTD × GX-HQ × user) | No → auto-route |
| multi-org.user | 1 (ACME-GLOBAL × user — RoleOrg only, no RoleCo restriction, sees all companies) | No → auto-route |
| multi-co.user | 1 (ACME-GLOBAL × ACME-INC + ACME-EU × user) | No → auto-route |
| multi-branch.user | 2 (sales_exec→HO, warehouse_op→NB) | **Yes** → pick role determines branch |
| multi-role.user | 1 (ACME-GLOBAL × ACME-INC × HO × manager/user/viewer) | No → auto-route |
| diana.prince | 1 (GLOBEX-CORP × GLOBEX-LTD × GX-HQ × tnt_admin) | No → auto-route |
