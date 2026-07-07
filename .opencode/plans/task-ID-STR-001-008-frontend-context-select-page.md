# Task ID-STR-001-008: Frontend ContextSelectPage — Role-First UI

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Rewrite `ContextSelectPage.tsx` with role-first selection flow. The role selector appears at the top, and when a role is selected, it determines the available tenant + org/company/branch options via the `roleScopes` map from the API.

## Files to Modify

### `routes/identity/context/ContextSelectPage.tsx` (later moved to `modules/identity/context/`)

#### Updated Interface — `ContextOptionsResponse`
```ts
interface ContextOptionsResponse {
  tenants: ContextOption[];
  organizations: ContextOption[];
  companies: ContextOption[];
  branches: ContextOption[];
  roles: string[];
  roleScopes: Record<string, RoleScope>;
}

interface RoleScope {
  fullAccess: boolean;
  tenantId: string;
  organizationIds: string[];
  companyIds: string[];
  branchIds: string[];
}
```

#### UI Layout (top to bottom)

1. **User greeting** (same as current — avatar + welcome)
2. **Role selector** (NEW — at the top, replaces bottom placement)
   - `TextField select` showing all roles the user has
   - Single-option auto-select + disable
3. **Tenant** (auto-filled from selected role's `roleScopes[role].tenantId` — no dropdown needed)
4. **Organization** (filtered by role scope)
   - If role `fullAccess` → show ALL orgs under that tenant
   - If not → show only `roleScopes[role].organizationIds`
   - Single-option auto-select + disable
5. **Company** (filtered by role scope + cascading parent)
   - First filter by role scope
   - Then filter by selected org's parentId
   - Single-option auto-select + disable
6. **Branch** (filtered by role scope + cascading parent)
   - First filter by role scope
   - Then filter by selected company's parentId
   - Single-option auto-select + disable
7. **Role display** (show selected role as chip — already selected above)
8. **"Enter Workspace" button** (same as current)

#### Filtering Logic (useMemo)

```ts
// When role changes, filter orgs:
const availableOrgIds = roleScopes[selectedRole]?.fullAccess
  ? options.organizations.map(o => o.id)  // all
  : roleScopes[selectedRole]?.organizationIds ?? [];

const filteredOrgs = options.organizations.filter(
  o => availableOrgIds.includes(o.id) && o.parentId === selectedTenant
);

// Same pattern for companies and branches
```

#### Cascading Clear

When role changes → clear org/co/branch selections. Same as current parent cascading.

#### Auto-Route (Single Profile)

If profiles.length === 1 → auto-switch and navigate to dashboard (same as current).

#### Validation

Show warning if selected role has limited scope and user hasn't picked within that scope.

## Validation
- `pnpm lint` + `pnpm typecheck` pass
- Login as `john.doe` → see only 1 role (user) → role auto-selected → org/co/branch auto-selected → auto-route
- Login as `multi-branch.user` → see 2 roles → pick one → filtered orgs/co/branches
- Login as `jane.smith` → see tnt_admin → fullAccess → all ACME orgs/co/branches available
