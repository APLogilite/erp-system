# Task ID-STR-001-011: Compile + Fix + Test

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Run full compilation and type-checking on both backend and frontend. Fix all errors. Then do integration testing by logging in as each test user and verifying their access scope matches expectations.

## Steps

### 1. Backend Compile
```bash
cd backend && mvn compile
```
- Fix any compilation errors (missing imports, deleted references, etc.)
- Pay special attention to:
  - `RuntimeContextService.java` — old references to UserOrg/UserCo/UserBranch
  - `AdminService.java` — old `@EnableTenantFilter` references
  - `IdentitySeedData.java` — old `assignOrg/assignCompany/assignBranch` calls
  - Any remaining `@Filter` references

### 2. Frontend Lint + Typecheck
```bash
cd frontend && pnpm lint && pnpm typecheck
```
- Fix import paths after folder restructure
- Fix type errors in ContextSelectPage (new roleScopes interface)
- Fix type errors in ContextSwitcher

### 3. Integration Tests

#### Test each user's login + context options

| User | Expected accessible data | Verify |
|------|------------------------|--------|
| admin | System Org only | `/context/options` shows SYS-ORG, no ACME/GLOBEX |
| auto.user | ACME-GLOBAL → ACME-INC → HO | Single profile → auto-route |
| jane.smith | ALL ACME data (fullAccess) | `roleScopes.tnt_admin.fullAccess = true` |
| john.doe | ACME-GLOBAL → ACME-INC → HO | Restricted by RoleOrg/Co/Br |
| multi-branch.user | 2 roles → HO + NB branches | Pick role → different branches |
| multi-role.user | 3 roles → same scope | Pick role → same org/co/branch |
| diana.prince | ALL GLOBEX data (fullAccess) | `roleScopes.tnt_admin.fullAccess = true` |
| bob.wilson | GLOBEX-CORP → GLOBEX-LTD → GX-HQ | Restricted |
| charlie.brown | Same as bob | Verify |

#### Test context switch + persistence
- Switch context → verify `/context/current` returns correct data
- New login → verify persistence still works

#### Test admin API filtering
- Login as jane.smith → `GET /identity/organizations` → only ACME orgs
- Login as bob.wilson → `GET /identity/organizations` → only GLOBEX orgs
- Login as admin → `GET /identity/organizations` → only SYS-ORG

## Fix Cycle
1. Fix all compilation errors
2. Fix all lint/type errors
3. Run integration tests
4. Fix any logic errors found during testing
5. Re-compile and re-test until everything passes
