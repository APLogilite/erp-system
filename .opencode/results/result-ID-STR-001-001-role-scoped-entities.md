# Result ID-STR-001-001: Role-Scoped Entities + Repositories

## Status: ✅ Complete

## Files Created

### `entity/RoleOrganization.java`
- Table: `identity_role_organizations`
- Fields: `role` (ManyToOne→Role), `organization` (ManyToOne→Organization)
- Constraint: UNIQUE(role_id, organization_id)
- Maps which organizations a role has access to

### `entity/RoleCompany.java`
- Table: `identity_role_companies`
- Fields: `role` (ManyToOne→Role), `company` (ManyToOne→Company)
- Constraint: UNIQUE(role_id, company_id)
- Maps which companies a role has access to (optional restriction within org)

### `entity/RoleBranch.java`
- Table: `identity_role_branches`
- Fields: `role` (ManyToOne→Role), `branch` (ManyToOne→Branch)
- Constraint: UNIQUE(role_id, branch_id)
- Maps which branches a role has access to (optional restriction within company)

### `repository/RoleOrganizationRepository.java`
- `findByRoleId(UUID)` — get RoleOrg entries for a specific role
- `findByRoleIdIn(List<UUID>)` — get RoleOrg entries for multiple roles

### `repository/RoleCompanyRepository.java`
- `findByRoleId(UUID)` — get RoleCo entries for a specific role
- `findByRoleIdIn(List<UUID>)` — get RoleCo entries for multiple roles

### `repository/RoleBranchRepository.java`
- `findByRoleId(UUID)` — get RoleBranch entries for a specific role
- `findByRoleIdIn(List<UUID>)` — get RoleBranch entries for multiple roles

## Validation
- `mvn compile` ✅
