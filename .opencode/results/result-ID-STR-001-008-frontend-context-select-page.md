# Result ID-STR-001-008: Frontend ContextSelectPage — Role-First UI

## Status: ✅ Complete

## File Modified

### `routes/identity/context/ContextSelectPage.tsx` — Rewritten

#### Key Changes
- **Role selector at top** — determines tenant + access scope
- **Tenant auto-filled** from selected role's `roleScope.tenantId` (disabled, no choice)
- **Orgs/Companies/Branches filtered** by `roleScopes[selectedRole].organizationIds/companyIds/branchIds`
- **`fullAccess` flag** — when true, shows ALL orgs/co/branches under the role's tenant
- **Single-option auto-select + disable** — same as before, added to role-first flow
- **Auto-route single profile** — same logic, uses `roleScopes` to compute profiles
- **Validation** — shows "Please select: Role/Org/Company/Branch" warning

#### UI Flow
```
Role (selector) → Tenant (auto) → Organization (filtered) → Company (filtered) → Branch (filtered) → Enter
```

## Validation
- `pnpm lint` ✅
- `pnpm typecheck` ✅
