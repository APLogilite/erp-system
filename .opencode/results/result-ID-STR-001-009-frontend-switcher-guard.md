# Result ID-STR-001-009: Frontend ContextSwitcher + ContextGuard

## Status: ✅ Complete

## Files Modified

### `routes/identity/context/ContextSwitcher.tsx`
- **Role at top** — prominent badge + role name in popup
- **"Change Workspace" button hidden** when `profileCount ≤ 1` (single-profile users don't see it)
- **Profile count** computed from `roleScopes` API data (fullAccess + org/co/br IDs)
- **Other roles** shown as secondary text if multiple roles exist

### `core/router/guards/ContextGuard.tsx`
*(Already updated in a prior session — verified correct)*
- Fetches both `/context/current` AND `/context/options`
- Checks all levels: tenantId, orgId, companyId, branchId, role
- Skips levels with zero available options
- Redirects to `/select-context` if any required level missing

## Validation
- `pnpm lint` ✅
- `pnpm typecheck` ✅
