# Final Implementation Approach — P0.5 Context Validation & Admin Fixes

## 1. Fix Null Parent Relations in Admin API

**Root Cause:** `JacksonConfig.java` has `Hibernate6Module` with `FORCE_LAZY_LOADING = false`. All parent `@ManyToOne` relationships use `FetchType.LAZY`, and no queries use JOIN FETCH. Result: lazy proxies serialize as null.

**Fix:** Add `@Query` with JOIN FETCH to each admin repository, then use those methods in `AdminService`.

### Repository Changes

| Repository | New Method | JPQL Query |
|---|---|---|
| `OrganizationRepository` | `findAllWithTenant()` | `SELECT o FROM Organization o JOIN FETCH o.tenant` |
| `CompanyRepository` | `findAllWithOrganization()` | `SELECT c FROM Company c JOIN FETCH c.organization o JOIN FETCH o.tenant` |
| `BranchRepository` | `findAllWithCompany()` | `SELECT b FROM Branch b JOIN FETCH b.company c JOIN FETCH c.organization o JOIN FETCH o.tenant` |
| `DepartmentRepository` | `findAllWithBranch()` | `SELECT d FROM Department d JOIN FETCH d.branch b JOIN FETCH b.company` |

### AdminService Changes

| Method | Old Call | New Call |
|---|---|---|
| `getAllOrganizations()` | `organizationRepository.findAll()` | `organizationRepository.findAllWithTenant()` |
| `getAllCompanies()` | `companyRepository.findAll()` | `companyRepository.findAllWithOrganization()` |
| `getAllBranches()` | `branchRepository.findAll()` | `branchRepository.findAllWithCompany()` |
| `getAllDepartments()` | `departmentRepository.findAll()` | `departmentRepository.findAllWithBranch()` |

---

## 2. Frontend Admin Pages — Add Ancestor Columns

| Page | New Column | Data Path |
|---|---|---|
| `CompaniesAdminPage.tsx` | Tenant | `c.organization?.tenant?.name ?? '—'` |
| `BranchesAdminPage.tsx` | Organization | `b.company?.organization?.name ?? '—'` |
| `BranchesAdminPage.tsx` | Tenant | `b.company?.organization?.tenant?.name ?? '—'` |
| `DepartmentsAdminPage.tsx` | Company | `d.branch?.company?.name ?? '—'` |

---

## 3. Frontend ContextGuard — Multi-Level Validation

**Current:** Checks only `tenantId`.

**New:** Fetch `/context/current` AND `/context/options`. For each level that has available options, verify the user has selected one. Levels with zero options are skipped.

```js
if (!current?.tenantId) → redirect
if (options.organizations.length > 0 && !current.organizationId) → redirect
if (options.companies.length > 0 && !current.companyId) → redirect
if (options.branches.length > 0 && !current.branchId) → redirect
if (options.roles.length > 0 && !current.roles?.[0]) → redirect
```

---

## 4. Frontend ContextSelectPage — Disable Single-Option Dropdowns

Add `disabled` prop to `SelectField`. When a level has exactly one option AND it's the selected value, the dropdown becomes grayed-out/non-interactive.

```tsx
<SelectField ... disabled={options.length === 1} />
```

Add required-level indicator and validation message if user tries to submit with missing selections.

---

## 5. Frontend ContextSwitcher — Simplify to Info + Button

**Current:** Profile cards + per-level switching submenu.

**New:** Show current context chips (as-is). Show current role prominently. Add a "Change Workspace" button that navigates to `/select-context`. Remove profile cards and per-level submenu entirely.
