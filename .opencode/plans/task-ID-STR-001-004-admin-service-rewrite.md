# Task ID-STR-001-004: AdminService Rewrite

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Replace all `@EnableTenantFilter` + `findAll()` patterns in AdminService and RoleAdminService with `AccessScopeService`-driven queries. Each method verifies the caller has access to the requested entities.

## Files to Modify

### 1. `service/AdminService.java`

#### Remove
- All `@EnableTenantFilter` annotations
- `import com.erp.platform.identity.sdk.annotation.EnableTenantFilter;`

#### Add
- Inject `AccessScopeService`
- Helper `currentUserId()` → `RuntimeContextHolder.get().getUserId()`

#### Rewrite Methods

| Method | New Implementation |
|--------|-------------------|
| `getAllOrganizations()` | `orgRepo.findAllById(scope.getAccessibleOrganizationIds(currentUserId()))` |
| `getAllCompanies()` | `coRepo.findAllByIdIn(scope.getAccessibleCompanyIds(currentUserId()))` |
| `getAllBranches()` | `branchRepo.findAllByIdIn(scope.getAccessibleBranchIds(currentUserId()))` |
| `getAllDepartments()` | `deptRepo.findByBranchIdIn(scope.getAccessibleBranchIds(currentUserId()))` |
| `getOrganization(UUID id)` | Verify `id in scope.getAccessibleOrganizationIds(...)` → else throw SecurityException |
| `getCompany(UUID id)` | Verify `id in scope.getAccessibleCompanyIds(...)` |
| `getBranch(UUID id)` | Verify `id in scope.getAccessibleBranchIds(...)` |
| `getDepartment(UUID id)` | Verify `dept.branch.id in scope.getAccessibleBranchIds(...)` |
| `createOrganization(Organization o)` | Auto-derive tenant; no explicit scope check needed (creating within own context) |
| `createCompany(Company c)` | Same |
| `createBranch(Branch b)` | Same |
| `createDepartment(Department d)` | Same |
| `updateOrganization(...)` | Verify entity is in accessible scope before updating |
| `updateCompany(...)` | Same |
| `updateBranch(...)` | Same |
| `updateDepartment(...)` | Same |
| `deleteOrganization(...)` | Same |
| `deleteCompany(...)` | Same |
| `deleteBranch(...)` | Same |
| `deleteDepartment(...)` | Same |
| `getOrganizationTree(UUID tenantId)` | Filter orgs by accessible scope for that tenant |
| `getCompaniesByOrganization(UUID orgId)` | Verify org is accessible |
| `getBranchesByCompany(UUID companyId)` | Verify company is accessible |
| `getDepartmentsByBranch(UUID branchId)` | Verify branch is accessible |

### 2. `service/RoleAdminService.java`
- Remove `@EnableTenantFilter` from `getAllRoles()` (line 34)
- Use `AccessScopeService` or tenant from context to filter roles

## Validation
- `mvn compile` passes
- All admin list endpoints return only scope-filtered data
- CRUD operations enforce scope
