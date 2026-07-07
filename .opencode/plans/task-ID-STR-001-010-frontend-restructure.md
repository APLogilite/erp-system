# Task ID-STR-001-010: Frontend Folder Restructure

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Move `src/routes/identity/` directory to `src/modules/identity/` to create a proper identity module. Update all import paths in the app to point to the new location.

## Files to Move

### Move Directory
```
src/routes/identity/ → src/modules/identity/
```

This affects:
```
modules/identity/
├── admin/
│   ├── AdminDashboardPage.tsx
│   ├── AdminListPage.tsx
│   ├── audit/AuditPage.tsx
│   ├── branches/BranchesAdminPage.tsx
│   ├── companies/CompaniesAdminPage.tsx
│   ├── departments/DepartmentsAdminPage.tsx
│   ├── organizations/OrganizationsAdminPage.tsx
│   ├── permissions/PermissionsAdminPage.tsx
│   ├── roles/RolesAdminPage.tsx
│   ├── sessions/SessionsAdminPage.tsx
│   ├── tenants/TenantsAdminPage.tsx
│   └── users/UsersAdminPage.tsx
├── context/
│   ├── ContextSelectPage.tsx
│   └── ContextSwitcher.tsx
├── profile/ProfilePage.tsx
├── preferences/PreferencesPage.tsx
└── sessions/SessionsPage.tsx
```

## Files to Modify — Update Import Paths

### `src/routes/AppRoutes.tsx`
All imports from `@/routes/identity/...` change to `@/modules/identity/...`

### Check for other files importing from `routes/identity/`
- Grep for `@/routes/identity` across the entire frontend
- Update all matches to `@/modules/identity`

## Path Alias
The `@/` alias in `vite.config.ts` and `tsconfig.json` already maps to `src/`.
So `@/modules/identity/...` resolves to `src/modules/identity/...` — no config changes needed.

## Validation
- `pnpm lint` passes (all imports resolve correctly)
- `pnpm typecheck` passes
- App runs without import errors
