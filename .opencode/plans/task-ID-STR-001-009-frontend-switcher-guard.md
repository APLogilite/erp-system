# Task ID-STR-001-009: Frontend ContextSwitcher + ContextGuard

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Update `ContextSwitcher` to display the current role prominently and hide "Change Workspace" when only one profile exists. Update `ContextGuard` to check all context levels using the options + current context.

## Files to Modify

### 1. `routes/identity/context/ContextSwitcher.tsx`

#### Current Behavior
- Shows context chips + per-level switching submenu + profile cards + "Change Workspace" button

#### New Behavior
- **Role display at top**: large chip/icon showing current role name
- **Context chips**: Tenant, Org, Company, Branch (same as current)
- **Other roles**: show as secondary text if user has multiple roles
- **"Change Workspace" button**: navigates to `/select-context`
  - **HIDE this button** when user has only 1 profile (profiles.length <= 1)

#### Logic for single-profile detection
```ts
const { data: optionsRaw } = useQuery(['context', 'options'], ...);
const profiles = useMemo(() => computeProfiles(optionsRaw), [optionsRaw]);
// ... (same profile computation as ContextSelectPage)
const hasSingleProfile = profiles.length <= 1;
```

### 2. `core/router/guards/ContextGuard.tsx`

#### Current Behavior
- Fetches `/context/current` only
- Checks `tenantId` only

#### New Behavior
- Fetch BOTH `/context/current` AND `/context/options`
- For each level that has options → verify user has selected it via current context
- Skip levels with zero options
- Redirect to `/select-context` if any required level is missing

```ts
if (!current?.tenantId) → redirect
if (options.organizations.length > 0 && !current.organizationId) → redirect
if (options.companies.length > 0 && !current.companyId) → redirect
if (options.branches.length > 0 && !current.branchId) → redirect
if (options.roles.length > 0 && !current.roles?.[0]) → redirect
```

## Validation
- `pnpm lint` + `pnpm typecheck` pass
- Single-profile users: no "Change Workspace" button visible
- Multi-profile users: button visible → navigates to `/select-context`
- Guard redirects to `/select-context` when context levels are missing
