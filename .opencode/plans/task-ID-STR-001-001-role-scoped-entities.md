# Task ID-STR-001-001: Role-Scoped Entities + Repositories

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Create 3 new entity classes and 3 repository interfaces for role-scoped access control. These replace the old UserOrg/UserCo/UserBranch pattern.

## Files to Create

### 1. `entity/RoleOrganization.java`
- Package: `com.erp.platform.identity.entity`
- Table: `identity_role_organizations`
- Extends: `BaseEntity`
- Fields:
  - `role` (ManyToOne → Role, FetchType.LAZY, JoinColumn `role_id`, nullable false)
  - `organization` (ManyToOne → Organization, FetchType.LAZY, JoinColumn `organization_id`, nullable false)
- UniqueConstraint: `(role_id, organization_id)`

### 2. `entity/RoleCompany.java`
- Package: `com.erp.platform.identity.entity`
- Table: `identity_role_companies`
- Extends: `BaseEntity`
- Fields:
  - `role` (ManyToOne → Role, FetchType.LAZY, JoinColumn `role_id`, nullable false)
  - `company` (ManyToOne → Company, FetchType.LAZY, JoinColumn `company_id`, nullable false)
- UniqueConstraint: `(role_id, company_id)`

### 3. `entity/RoleBranch.java`
- Package: `com.erp.platform.identity.entity`
- Table: `identity_role_branches`
- Extends: `BaseEntity`
- Fields:
  - `role` (ManyToOne → Role, FetchType.LAZY, JoinColumn `role_id`, nullable false)
  - `branch` (ManyToOne → Branch, FetchType.LAZY, JoinColumn `branch_id`, nullable false)
- UniqueConstraint: `(role_id, branch_id)`

### 4. `repository/RoleOrganizationRepository.java`
- Package: `com.erp.platform.identity.repository`
- Methods:
  - `List<RoleOrganization> findByRoleId(UUID roleId)`
  - `List<RoleOrganization> findByRoleIdIn(List<UUID> roleIds)`

### 5. `repository/RoleCompanyRepository.java`
- Package: `com.erp.platform.identity.repository`
- Methods:
  - `List<RoleCompany> findByRoleId(UUID roleId)`
  - `List<RoleCompany> findByRoleIdIn(List<UUID> roleIds)`

### 6. `repository/RoleBranchRepository.java`
- Package: `com.erp.platform.identity.repository`
- Methods:
  - `List<RoleBranch> findByRoleId(UUID roleId)`
  - `List<RoleBranch> findByRoleIdIn(List<UUID> roleIds)`

## Validation
- `mvn compile` passes
- Tables are auto-created by Hibernate ddl-auto=update
