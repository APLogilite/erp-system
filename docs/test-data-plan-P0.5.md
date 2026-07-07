# Test Data Plan — P0.5 Identity & Context Scenarios

## Purpose

Create seed data covering all identity hierarchy scenarios so we can validate:
1. Context selection (cascading dropdowns, auto-route for single profile)
2. Tenant data isolation (user only sees their assigned tenants/orgs/companies/branches)
3. Admin page filtering (IDENTITY & ADMINISTRATION only shows data for active tenant)
4. Cross-tenant security (no data leakage between ACME and GLOBEX)

---

## Schema: New Entity

### `UserBranch` → `identity_user_branches`

Users are directly assigned to branches (same pattern as `UserCompany`).

```
identity_user_branches
├── id              UUID PK (BaseEntity)
├── user_id         FK → identity_users (NOT NULL)
├── branch_id       FK → identity_branches (NOT NULL)
├── is_default      BOOLEAN
├── UNIQUE(user_id, branch_id)
```

This changes `getAvailableOptions()` to return only branches the user is directly assigned to (via `UserBranch`), rather than all branches under their companies.

---

## New Hierarchy Data

### Org: ACME-APAC

| Field | Value |
|-------|-------|
| Code | ACME-APAC |
| Name | Acme APAC |
| Tenant | ACME |
| Level | 0 |
| Path | /ACME-APAC |

### Company: APAC-INC

| Field | Value |
|-------|-------|
| Code | APAC-INC |
| Name | Acme APAC Inc. |
| Org | ACME-APAC |
| Tenant | ACME |
| Currency | INR |
| Tax ID | TAX-IN-001 |
| Reg No | REG-IN-001 |
| Address | 42 Tech Park, Mumbai, India |
| Phone | +91-22-555-0100 |
| Email | info@apac-inc.in |

### Branch: APAC-HQ

| Field | Value |
|-------|-------|
| Code | APAC-HQ |
| Name | APAC Headquarters |
| Company | APAC-INC |
| Tenant | ACME |
| City | Mumbai |
| Is Head Office | true |

### Branches with clearly named departments for tenant isolation validation

| Branch | Departments (new in **bold**) | Naming Convention |
|--------|-------------------------------|-------------------|
| HO (ACME-INC) | ENG, SALES, FIN, **LEGAL, IT, HR** | Plain codes (ACME-specific) |
| NB (ACME-INC) | SUPPORT, **LOGISTICS, SERVICE** | Plain codes |
| EU-HQ (ACME-EU) | EU-DEV, **EU-SALES, EU-MARKETING** | EU- prefix |
| APAC-HQ (APAC-INC) | **APAC-ENG, APAC-HR** | APAC- prefix |
| GX-HQ (GLOBEX-LTD) | GX-OPS, **GX-FINANCE, GX-HR, GX-SALES** | GX- prefix (GLOBEX) |

**Validation rule:** If any ACME user sees a `GX-*` department, or any GLOBEX user sees an un-prefixed department like `ENG`, the tenant filter is broken.

---

## Roles

| Code | Name | System | Tenant |
|------|------|:------:|--------|
| sys_admin | System Administrator | Yes | null |
| tnt_admin | Tenant Administrator | Yes | ACME (existing) |
| user | Regular User | Yes | null |
| viewer | Read Only | Yes | null |
| **manager** | **Manager** | **Yes** | **null** |
| **sales_executive** | **Sales Executive** | **Yes** | **null** |
| **warehouse_op** | **Warehouse Operator** | **Yes** | **null** |
| **hr_manager** | **HR Manager** | **Yes** | **null** |

---

## Users

All passwords: **User@123** (admin is **Admin@123**)

| # | Username | Display Name | Password | Scenario | Tenants | Orgs | Companies | Branches | Roles |
|---|----------|-------------|----------|----------|---------|------|-----------|----------|-------|
| — | admin | System Administrator | Admin@123 | Super-user (multi-tenant) | ACME, GLOBEX | ACME-GLOBAL, ACME-APAC, GLOBEX-CORP | ACME-INC*, ACME-EU, GLOBEX-LTD | HO*, NB, EU-HQ, GX-HQ | sys_admin, tnt_admin, user, viewer |
| — | john.doe | John Doe | User@123 | Single ACME user | ACME | ACME-GLOBAL | ACME-INC* | HO | user |
| — | jane.smith | Jane Smith | User@123 | ACME tnt_admin | ACME | ACME-GLOBAL | ACME-EU* | EU-HQ | tnt_admin |
| — | bob.wilson | Bob Wilson | User@123 | GLOBEX viewer | GLOBEX | GLOBEX-CORP | GLOBEX-LTD* | GX-HQ | viewer |
| — | alice.johnson | Alice Johnson | User@123 | ACME regular | ACME | ACME-GLOBAL | ACME-INC* | HO | user |
| — | charlie.brown | Charlie Brown | User@123 | GLOBEX regular | GLOBEX | GLOBEX-CORP | GLOBEX-LTD* | GX-HQ | user |
| — | diana.prince | Diana Prince | User@123 | GLOBEX tnt_admin | GLOBEX | GLOBEX-CORP | GLOBEX-LTD* | GX-HQ | tnt_admin |
| **1** | **auto.user** | **Auto User** | **User@123** | **A — single option auto-route** | **ACME** | **ACME-GLOBAL** | **ACME-INC*** | **HO*** | **user** |
| **2** | **super.user** | **Super User** | **User@123** | **B — multi-tenant** | **ACME, GLOBEX** | **ACME-GLOBAL, GLOBEX-CORP** | **ACME-INC*, GLOBEX-LTD*** | **HO*, GX-HQ*** | **sys_admin, tnt_admin** |
| **3** | **multi-org.user** | **Multi Org** | **User@123** | **C — single tenant, multi-org** | **ACME** | **ACME-GLOBAL, ACME-APAC** | **ACME-INC*, APAC-INC*** | **HO*, APAC-HQ*** | **user** |
| **4** | **multi-co.user** | **Multi Company** | **User@123** | **D — single org, multi-company** | **ACME** | **ACME-GLOBAL** | **ACME-INC*, ACME-EU** | **HO*, EU-HQ*** | **user** |
| **5** | **multi-branch.user** | **Multi Branch** | **User@123** | **E — same co, 2 branches, 2 roles** | **ACME** | **ACME-GLOBAL** | **ACME-INC*** | **HO*, NB** | **sales_executive, warehouse_op** |
| **6** | **multi-role.user** | **Multi Role** | **User@123** | **F — single branch, 3 roles** | **ACME** | **ACME-GLOBAL** | **ACME-INC*** | **HO*** | **manager, user, viewer** |

**`*` = is_default for that user's company/branch assignment**

---

## Scenario Expectations

| # | User | Auto-Selected Levels | User Must Pick | Expected Behavior |
|---|------|---------------------|----------------|-------------------|
| A | auto.user | All 5 (tenant, org, co, branch, role) | (nothing) | Skips selection page → lands on dashboard instantly |
| B | super.user | None fully auto (2 tenants) | Tenant | After picking tenant → cascading org/co/branch auto-select, role pick |
| C | multi-org.user | Tenant, Role | Org | After picking org → cascading co/branch auto-select |
| D | multi-co.user | Tenant, Org, Role | Company | After picking company → branch auto-select |
| E | multi-branch.user | Tenant, Org, Company | Branch, Role | Pick branch → cascading role options shown |
| F | multi-role.user | Tenant, Org, Company, Branch | Role | Only role dropdown shown with 3 options |

---

## Tenant Isolation Validation

| User | Active Context | Admin > Orgs sees | Admin > Depts sees | Should NOT see |
|------|---------------|-------------------|-------------------|----------------|
| admin (no context) | tenant=null | ALL (all tenants) | ALL depts | (nothing) |
| admin (switch to ACME) | tenant=ACME | ACME-GLOBAL, ACME-APAC | ENG, SALES, FIN, LEGAL, IT, HR, SUPPORT, LOGISTICS, SERVICE, EU-DEV, EU-SALES, EU-MARKETING, APAC-ENG, APAC-HR | GLOBEX-CORP, GX-OPS, GX-FINANCE, GX-HR, GX-SALES |
| diana.prince | tenant=GLOBEX | GLOBEX-CORP | GX-OPS, GX-FINANCE, GX-HR, GX-SALES | Everything ACME |
| jane.smith | tenant=ACME | ACME-GLOBAL, ACME-APAC | All ACME depts | All GLOBEX depts |
| multi-branch.user | tenant=ACME | ACME-GLOBAL, ACME-APAC | All ACME depts | GLOBEX data |
| john.doe | tenant=ACME | ACME-GLOBAL, ACME-APAC | All ACME depts | GLOBEX data |
| charlie.brown | tenant=GLOBEX | GLOBEX-CORP | Only GX-* depts | Everything ACME |

**Filtering rules:** 
- `@Filter(condition = "tenant_id = :tenantId")` on Organization, Company, Branch, Department, Role
- sys_admin with null tenant → no filter → sees everything
- sys_admin with ACME context → filter applied → only ACME data
- Departments named with distinct prefixes make broken filters immediately visible

---

---

## Verification Results (All Passed ✓)

| User | Tenants | Orgs | Cos | Branches | Roles | Validated |
|------|---------|------|-----|----------|-------|:---------:|
| auto.user | ACME | ACME-GLOBAL | ACME-INC | HO | user | ✓ |
| super.user | ACME, GLOBEX | ACME-GLOBAL, GLOBEX-CORP | ACME-INC, GLOBEX-LTD | HO, GX-HQ | sys_admin, tnt_admin | ✓ |
| multi-org.user | ACME | ACME-GLOBAL, ACME-APAC | ACME-INC, APAC-INC | HO, APAC-HQ | user | ✓ |
| multi-co.user | ACME | ACME-GLOBAL | ACME-INC, ACME-EU | HO, EU-HQ | user | ✓ |
| multi-branch.user | ACME | ACME-GLOBAL | ACME-INC | HO, NB | sales_executive, warehouse_op | ✓ |
| multi-role.user | ACME | ACME-GLOBAL | ACME-INC | HO | manager, user, viewer | ✓ |
| john.doe | ACME | ACME-GLOBAL | ACME-INC | HO | user | ✓ |
| diana.prince | GLOBEX | GLOBEX-CORP | GLOBEX-LTD | GX-HQ | tnt_admin | ✓ |
| jane.smith | ACME | ACME-GLOBAL | ACME-EU | EU-HQ | tnt_admin | ✓ |
| bob.wilson | GLOBEX | GLOBEX-CORP | GLOBEX-LTD | GX-HQ | viewer | ✓ |
| charlie.brown | GLOBEX | GLOBEX-CORP | GLOBEX-LTD | GX-HQ | user | ✓ |
| alice.johnson | ACME | ACME-GLOBAL | ACME-INC | HO | user | ✓ |
| admin | ACME, GLOBEX | ACME-GLOBAL, ACME-APAC, GLOBEX-CORP | ACME-INC, ACME-EU, APAC-INC, GLOBEX-LTD | HO, NB, EU-HQ, APAC-HQ, GX-HQ | sys_admin, tnt_admin, user, viewer | ✓ |

**Key finding:** `diana.prince` (GLOBEX) sees only GLOBEX data — no ACME leakage. Tenant isolation is working correctly.

## User Credentials (Quick Reference)

| Username | Password | Role Highlights |
|----------|----------|----------------|
| **admin** | **Admin@123** | Super-user, all tenants |
| john.doe | User@123 | ACME regular user |
| jane.smith | User@123 | ACME tenant admin (German locale) |
| bob.wilson | User@123 | GLOBEX read-only |
| alice.johnson | User@123 | ACME, same as john.doe |
| charlie.brown | User@123 | GLOBEX regular |
| diana.prince | User@123 | GLOBEX tenant admin |
| **auto.user** | **User@123** | Single profile → auto-route |
| **super.user** | **User@123** | Multi-tenant |
| **multi-org.user** | **User@123** | Two orgs under ACME |
| **multi-co.user** | **User@123** | Two companies under ACME-GLOBAL |
| **multi-branch.user** | **User@123** | Two branches, two roles |
| **multi-role.user** | **User@123** | Three roles, single branch |
