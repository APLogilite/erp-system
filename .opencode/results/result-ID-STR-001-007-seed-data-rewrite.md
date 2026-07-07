# Result ID-STR-001-007: Seed Data Rewrite

## Status: ✅ Complete

## File Rewritten

### `IdentitySeedData.java` — Full Rewrite

#### Key Changes

| Aspect | Before | After |
|--------|--------|-------|
| Bootstrap tenant | ACME first | **SYS (System)** is first tenant, bootstrap |
| System bootstrap | None | SYS-ORG org, sys_admin role (NO RoleOrg = full System) |
| User assignments | UserOrg/UserCo/UserBranch | **RoleOrg/RoleCo/RoleBranch** — access through roles |
| User-Org assignment | Direct assignOrg() | Removed — access via role scope |
| User-Co assignment | Direct assignCompany() | Removed — access via role scope |
| User-Branch assignment | Direct assignBranch() | Removed — access via role scope |
| Role scopes | Not used | **RoleOrg, RoleCo, RoleBranch** tables populated |

#### New Entity Data

| Entity | SYS Tenant | ACME Tenant | GLOBEX Tenant |
|--------|-----------|-------------|---------------|
| Orgs | SYS-ORG | ACME-GLOBAL, ACME-APAC | GLOBEX-CORP |
| Roles | sys_admin, tnt_admin, user, viewer, manager, sales_exec, warehouse_op, hr_manager | user, viewer, manager, sales_exec, warehouse_op (cloned, with RoleOrg/Co/Br) | user, viewer (cloned, with RoleOrg/Co/Br) |

#### Test Users Access Scope

| User | Role | Effective Access |
|------|------|-----------------|
| admin | sys_admin (SYS) | Full SYS tenant (NO RoleOrg) |
| auto.user | user (ACME) | ACME-GLOBAL → ACME-INC → HO |
| super.user | sys_admin (SYS) | Full SYS tenant |
| jane.smith | tnt_admin (SYS) | Full SYS tenant (needs role in ACME to see ACME data) |
| john.doe | user (ACME) | ACME-GLOBAL → ACME-INC → HO |
| multi-branch.user | sales_exec + warehouse_op | HO (via sales) + NB (via warehouse) |
| multi-role.user | manager + user + viewer | Same ACME scope, different role permissions |
| diana.prince | tnt_admin (SYS) | Full SYS tenant |

## Validation
- `mvn compile` ✅
- On fresh DB, all 13 users + roles + scopes created
- Users without RoleOrg entries (like admin with sys_admin) get full tenant access
