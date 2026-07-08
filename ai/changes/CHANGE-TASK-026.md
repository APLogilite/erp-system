---
id: CHANGE-TASK-026

task_id: TASK-026

parent_prd: PRD-001

branch: feature/TASK-026

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 3h

related_commits: [b65d770, af13809]

related_files:
  - frontend/src/core/runtime/components/FormNavigationMenu.tsx
  - frontend/src/core/runtime/hooks/useAccessibleForms.ts
  - frontend/src/components/layouts/Sidebar/Sidebar.tsx

review_required: true

test_required: true

---

# Summary

Built the role-based navigation menu that dynamically populates with forms the current user has access to based on their tenant and role assignments. The `FormNavigationMenu` component fetches accessible forms, groups them by model/table, and renders them as navigation items in the sidebar. Integrated into the existing Sidebar layout component.

---

# Business Requirements Implemented

- FR-025: Role-Based Navigation — dynamic navigation menu populated from accessible forms
- Forms fetched via `GET /api/runtime/forms` endpoint (role-aware, returns only accessible forms)
- Forms grouped by model/table label for logical organization
- User-specific: different users see different forms based on tenant role assignments
- Integration with existing Sidebar navigation component
- Session-level caching via React Query with `staleTime: Infinity`

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/components/FormNavigationMenu.tsx` | Navigation menu component: fetches accessible forms, groups by model, renders MUI List with navigation links |
| `frontend/src/core/runtime/hooks/useAccessibleForms.ts` | React Query hook: fetches `GET /api/runtime/forms`, caches for session lifetime |

---

# Files Modified

| File | Summary |
|------|---------|
| `frontend/src/components/layouts/Sidebar/Sidebar.tsx` | Added `FormNavigationMenu` import and rendering in sidebar layout beneath existing navigation items |

---

# Files Removed

None

---

# Database Changes

None (frontend only)

---

# API Changes

None (consumes existing `GET /api/runtime/forms` endpoint)

---

# Routes

None

---

# Classes Added

None (React components and hooks)

---

# Classes Updated

None

---

# Methods Added

| Component/Hook | Export | Purpose |
|----------------|--------|---------|
| FormNavigationMenu | `FormNavigationMenu` | Renders dynamic form navigation grouped by model |
| useAccessibleForms | `useAccessibleForms()` | React Query hook for fetching and caching accessible forms list |

---

# Methods Updated

None

---

# Models

None

---

# Services

None

---

# Repositories

None

---

# DTOs

None

---

# Requests

None

---

# Policies

None

---

# Events

None

---

# Jobs

None

---

# Configuration

None

---

# Dependencies

Uses existing: MUI (`List`, `ListItem`, `ListItemButton`, `ListItemIcon`, `ListItemText`, `Typography`, `Collapse`, `Divider`, `Skeleton`, `Alert`), MUI Icons (`Dashboard`, `ExpandLess`, `ExpandMore`), React Router (`useNavigate`), React Query (`useQuery`), existing `apiClient` for HTTP calls

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend, 0 errors)

## Lint

PASS — `eslint --max-warnings=0` on navigation component files

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] Navigation menu fetches forms on mount
- [x] Forms grouped by model/table label
- [x] Menu items show form labels
- [x] Clicking menu item navigates to correct form list route
- [x] Forms cached for session (single fetch per session)
- [x] Loading skeleton shown while fetching
- [x] Error state with retry on fetch failure
- [x] Empty state when no accessible forms
- [x] Integrated into existing Sidebar layout
- [x] TypeScript compilation succeeds

---

# Breaking Changes

None. Additive change to existing Sidebar component.

---

# Known Issues

1. **Form grouping**: Currently groups by `modelLabel`. If no model label exists, forms are shown in an "Other" group. A future enhancement could allow Tenant Admins to configure custom grouping.
2. **Icon support**: Forms do not have configurable icons yet. All menu items use a default `Dashboard` icon. Future metadata field for `form.icon` would enable this.
3. **Sidebar modification**: The Sidebar component was modified directly rather than using a plugin/extension pattern. This makes future sidebar updates slightly more complex.

---

# Future Improvements

- Add configurable form icons (add `icon` field to form metadata)
- Allow Tenant Admin to configure menu grouping and ordering
- Add search/filter within the navigation menu for forms
- Add recent/favorite forms section
- Use a sidebar extension/plugin pattern instead of direct modification

---

# Developer Notes

- **React Query caching**: `useAccessibleForms` uses `staleTime: Infinity` and `gcTime: 30 * 60 * 1000`. The form list is fetched once per app load and only refreshed on full page reload or explicit invalidation.
- **Sidebar integration**: `FormNavigationMenu` is conditionally rendered in the sidebar — only shown when the user has accessible dynamic forms. This prevents an empty "Forms" section when no forms are assigned.
- **Route pattern**: Menu items navigate to `/forms/:formCode` which is handled by the dynamic form router. The `formCode` comes from the form definition metadata.
- **Error handling**: If the forms fetch fails, a retry button is shown. No forms = informational empty state, not an error.

---

# QA Handoff

Suggested test focus:
1. Different users (System Admin vs Tenant Admin) see different form lists
2. Forms grouped correctly by model/table
3. Clicking form navigates to correct route
4. Forms list is cached (navigate away and back — no re-fetch)
5. Loading skeleton displays during initial fetch
6. Empty state when user has no accessible forms
7. Error state and retry works on API failure
8. Sidebar integration doesn't break existing navigation

Potential risk areas:
- User with many forms (50+) may have a long sidebar list — may need scroll or collapse
- Role changes mid-session won't update until page reload
- Sidebar component modification may conflict with future layout changes
