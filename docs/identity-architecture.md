# Identity Platform — Architecture

## Overview

The Identity Platform is the foundation of the ERP. It provides multi-tenant authentication, fine-grained authorization, organization hierarchy, and session management — completely independent from business modules.

## Design Principle

Three concerns are strictly separated:

```
Authentication   → Who are you?      (UserAccount, credentials, sessions)
Authorization    → What can you do?   (Roles, Permissions, resource-level access)
Context          → Where are you?     (Tenant, Organization, Company, Branch, Department, Preferences)
```

## Package Structure

```
com.erp.platform.identity
    ├── entity/          # JPA entities
    │   ├── Tenant.java
    │   ├── Organization.java
    │   ├── Company.java
    │   ├── Branch.java
    │   ├── Department.java
    │   ├── UserAccount.java
    │   ├── Role.java
    │   ├── Permission.java
    │   ├── UserRole.java
    │   ├── RolePermission.java
    │   ├── UserOrganization.java
    │   ├── UserCompany.java
    │   ├── UserSession.java
    │   └── UserPreference.java
    ├── repository/      # Spring Data JPA repositories
    │   ├── TenantRepository.java
    │   ├── OrganizationRepository.java
    │   ├── CompanyRepository.java
    │   ├── BranchRepository.java
    │   ├── DepartmentRepository.java
    │   ├── UserAccountRepository.java
    │   ├── RoleRepository.java
    │   ├── PermissionRepository.java
    │   ├── UserRoleRepository.java
    │   ├── RolePermissionRepository.java
    │   ├── UserOrganizationRepository.java
    │   ├── UserCompanyRepository.java
    │   ├── UserSessionRepository.java
    │   └── UserPreferenceRepository.java
    └── dto/              # Data transfer objects (non-entity)
        └── RuntimeContext.java
```

## Entity Relationship Diagram

```
Tenant (1) ──── (N) Organization
Organization (1) ──── (N) Company
Company (1) ──── (N) Branch
Branch (1) ──── (N) Department
Organization ── parent ──▶ Organization          (self-referential, hierarchy)
Department ── parent ──▶ Department             (self-referential, hierarchy)

UserAccount (N) ──── (N) Role                   [via UserRole]
Role (N) ──── (N) Permission                    [via RolePermission]
UserAccount (N) ──── (N) Organization           [via UserOrganization]
UserAccount (N) ──── (N) Company                [via UserCompany]
UserAccount (1) ──── (1) UserPreference
UserAccount (1) ──── (N) UserSession
```

## Relationships Detail

| Entity | Relation | Target | Cardinality |
|--------|----------|--------|-------------|
| Tenant | 1:N | Organization | One tenant → many orgs |
| Organization | N:1 | Tenant | Belongs to one tenant |
| Organization | self | Organization (parent) | Tree hierarchy via parent_id |
| Organization | 1:N | Company | One org → many companies |
| Company | N:1 | Organization | Belongs to one org |
| Company | 1:N | Branch | One company → many branches |
| Branch | N:1 | Company | Belongs to one company |
| Branch | 1:N | Department | One branch → many departments |
| Department | N:1 | Branch | Belongs to one branch |
| Department | self | Department (parent) | Tree hierarchy via parent_id |
| UserAccount | N:M | Role | Via UserRole join table |
| Role | N:M | Permission | Via RolePermission join table |
| UserAccount | N:M | Organization | Via UserOrganization join table |
| UserAccount | N:M | Company | Via UserCompany join table |
| UserAccount | 1:1 | UserPreference | One preference per user |
| UserAccount | 1:N | UserSession | One user → many sessions |

## Permission Model

Permissions support these resource types:

| Resource Type | Examples |
|---------------|----------|
| MODULE | product, sales, accounting, manufacturing |
| MENU | sidebar navigation items |
| WINDOW | CRUD form/table views |
| TAB | sub-tabs within windows |
| FIELD | individual form fields (read/write/hidden) |
| ACTION | create, edit, delete, approve, submit |
| WORKFLOW | workflow transitions |
| PROCESS | background jobs, batch operations |
| REPORT | report generation and export |
| DASHBOARD | KPI dashboards and widgets |
| PLUGIN | extension modules |

Each Permission has: `resource_type` + `resource` + `action` (e.g. `MODULE.product.READ`, `ACTION.sales_order.APPROVE`).

## RuntimeContext

`RuntimeContext` is a non-entity POJO that captures the active session state:

```
┌─────────────────────────────────────┐
│         RuntimeContext              │
├─────────────────────────────────────┤
│ userId, username, email, displayName│  ← Who (Authentication)
│ tenantId, tenantCode, tenantName   │  ← Which tenant
│ organizationId, orgCode, orgName   │  ← Which org
│ companyId, companyCode, companyName│  ← Which company
│ branchId, branchCode, branchName   │  ← Which branch
│ departmentId, deptCode, deptName   │  ← Which department
│ roles, permissions                 │  ← What can you do (Authorization)
│ language, timezone, currency       │  ← Preferences
│ dateFormat, numberFormat, theme    │  ← Display settings
│ ipAddress, sessionId               │  ← Session metadata
└─────────────────────────────────────┘
```

It will be attached to every authenticated request via a request-scoped bean.

## Login Flow (Sequence)

```
User          Frontend          Backend              Identity DB
 │               │                 │                     │
 │  Login form   │                 │                     │
 │──────────────▶│                 │                     │
 │               │  POST /auth     │                     │
 │               │────────────────▶│                     │
 │               │                 │  Validate creds     │
 │               │                 │────────────────────▶│
 │               │                 │◀────────────────────│
 │               │                 │                     │
 │               │                 │  Load user+roles+   │
 │               │                 │  perms+prefs+orgs   │
 │               │                 │────────────────────▶│
 │               │                 │◀────────────────────│
 │               │                 │                     │
 │               │                 │  Build RuntimeCtx   │
 │               │                 │  Generate JWT       │
 │               │                 │  Create UserSession │
 │               │ ◀───────────────│                     │
 │               │                 │                     │
 │  Dashboard    │  Store token    │                     │
 │◀──────────────│  Init context   │                     │
 │               │                 │                     │
 │  Every req    │  Bearer token   │                     │
 │──────────────▶│────────────────▶│                     │
 │               │                 │  Resolve RuntimeCtx │
 │               │                 │  Authorize action   │
 │               │                 │  Execute business   │
 │               │ ◀───────────────│                     │
 │◀──────────────│                 │                     │
```

## Database Schema

Managed via Flyway migration `V1__init_identity_schema.sql` at `src/main/resources/db/migration/`.

Tables: `identity_tenants`, `identity_organizations`, `identity_companies`, `identity_branches`, `identity_departments`, `identity_users`, `identity_roles`, `identity_permissions`, `identity_user_roles`, `identity_role_permissions`, `identity_user_organizations`, `identity_user_companies`, `identity_user_sessions`, `identity_user_preferences`.

All tables use:
- UUID primary keys
- Created/updated timestamps + audit fields (created_by, updated_by)
- Soft delete support (is_active, deleted_at)
- Proper foreign key constraints
- Performance indexes on lookup columns

## Related Documents

- `docs/identity-standards.md` — Frozen platform contracts (JWT claims, naming, password policy, sessions, audit, caching, etc.)

## Future Extensions

- SSO via SAML/OIDC
- LDAP/AD synchronization
- OAuth2 authorization server
- SCIM provisioning
- Two-factor authentication
- Passwordless (WebAuthn)
