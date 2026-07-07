# Changes Summary — P0.5 ERP Identity & Context

## 1. Root Cause: Context Page Not Showing After Login

Two issues prevented the context selection page from appearing:

- **`GuestRoute`** read `location.state.from.pathname` (set by `ProtectedRoute`) and redirected to `/app/dashboard` instead of `/select-context`
- **`LoginPage`** did the same, navigating to the original redirect URL after login
- **Zustand persist hydration race**: On page refresh, the store had not hydrated yet, so `isAuthenticated` was `false`, triggering a redirect to login before the real auth state was restored

### Fixes
| File | Change |
|------|--------|
| `GuestRoute.tsx` | Always redirects to `/select-context` (ignores `from` param) |
| `LoginPage.tsx` | Always navigates to `/select-context` after login |
| `AuthGuard.tsx` | Waits for Zustand persist hydration (with 2s timeout fallback) before deciding auth state |
| `ProtectedRoute.tsx` | Same hydration-wait logic; stores original URL for redirect |

---

## 2. Context Hierarchy (5 Levels + Role) — Per P0.5 Spec

Implemented **Tenant → Organization → Company → Branch → Role** selection flow exactly as described in P0.5.

### Context Select Page (Profile-Based)
Instead of the old step-by-step cascading dropdowns, the page now shows **Context Profiles**:

- Each profile is a pre-computed combination: `{tenant, org, company, branch, role}`
- Profile card shows: **`role @ branch`** as label, **`Tenant / Org / Company / Branch`** as path
- User clicks one profile → single-step context switch
- **Scenario A (single profile):** Auto-selects and skips the page entirely — user lands directly on dashboard
- **Scenario B (multiple profiles):** Shows all profiles as clickable cards
- **No Department in selection flow** — per P0.5 spec, department is a runtime attribute, not a user selection step

### Context Switcher (Top-Bar)
Updated to show two modes:
1. **Context Profiles tab** — quick switch to another full profile
2. **Per-level chips** — change individual levels (Tenant, Org, Company, Branch, Role)

### Backend API Changes

| File | Change |
|------|--------|
| `ContextOption.java` | Added `parentId` field for client-side cascading |
| `ContextOptionsResponse.java` | Added `departments` list (available but not in profile flow) |
| `ContextSwitchRequest.java` | Added `departmentId` field |
| `BranchRepository.java` | Added `findByCompanyIdIn(List<UUID>)` |
| `DepartmentRepository.java` | Added `findByBranchIdIn(List<UUID>)` |
| `RuntimeContextService.java` | `getAvailableOptions()` populates branches/departments with `parentId`; `switchContext()` handles departmentId |

---

## 3. Critical Fix: Context Persistence

**Problem:** The old `switchContext()` stored the selected context in a `ThreadLocal` only — it was lost on every subsequent request. The `/context/current` endpoint called `resolve()` which rebuilt the baseline from DB, ignoring the switch entirely.

**Solution:** Store context selections at the user level in `UserPreference` so they survive across sessions, logouts, and server restarts.

### Changes

| File | Change |
|------|--------|
| `UserPreference.java` | Added columns: `active_tenant_id`, `active_organization_id`, `active_company_id`, `active_branch_id`, `active_department_id`, `active_role_code` |
| `RuntimeContextService.java` | `resolve()` now reads active selections from `UserPreference` after building baseline and applies as overrides; `switchContext()` persists selections to `UserPreference` before returning |
| `ContextController.java` | `/context/current` now calls `resolve()` (which reads persisted overrides, returning the correct active context) |
| `ContextFilter.java` | Every request resolves context with persisted overrides applied |

### Flow
```
Login → resolve() → baseline (null for sys_admin) + UserPreference overrides
     ↓
ContextSelectPage → user picks profile → switchContext() persists to UserPreference
     ↓
Every subsequent request → resolve() → baseline + stored overrides → correct context
```

---

## 4. Database Schema

| File | Change |
|------|--------|
| `db-setup-template.sql` | Full schema with all tables, constraints, FKs, indexes |
| `identity_companies` | Added `tenant_id` (UUID NOT NULL) + FK |
| `identity_branches` | Added `tenant_id` (UUID NOT NULL) + FK |
| `identity_departments` | Added `tenant_id` (UUID NOT NULL) + FK |
| `identity_user_preferences` | Added 6 context override columns |

---

## 5. Files Changed (Full List)

### Backend (11 files)
```
backend/src/main/java/com/erp/platform/identity/
├── controller/ContextController.java
├── dto/ContextOption.java
├── dto/ContextOptionsResponse.java
├── dto/ContextSwitchRequest.java
├── entity/UserPreference.java
├── entity/UserSession.java
├── repository/BranchRepository.java
├── repository/DepartmentRepository.java
├── repository/UserSessionRepository.java
├── security/ContextFilter.java
└── service/RuntimeContextService.java
```

### Frontend (6 files)
```
frontend/src/
├── core/router/guards/AuthGuard.tsx
├── core/router/guards/GuestRoute.tsx
├── core/router/guards/ProtectedRoute.tsx
├── routes/auth/LoginPage.tsx
├── routes/identity/context/ContextSelectPage.tsx
└── routes/identity/context/ContextSwitcher.tsx
```

### Docs (1 file)
```
docs/changes-summary-P0.5.md
```
