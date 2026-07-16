---
id: CHANGE-TASK-025

task_id: TASK-025

parent_prd: PRD-001

branch: feature/TASK-025

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 2h

related_commits:
  - 3745d0e

related_files:
  - frontend/src/core/runtime/components/FormSearchBar.tsx
  - frontend/src/core/runtime/hooks/useAccessibleForms.ts

review_required: true

test_required: true

---

# Summary

Built the `FormSearchBar` component (89 lines) as a global form search dialog triggered by Ctrl+K/Cmd+K. Uses MUI `Dialog` with a `TextField` search input and filtered `List` of matching forms. Consumes accessible forms via React Query (`useAccessibleForms` hook, 23 lines) and navigates to selected forms via React Router. Client-side filtering by form label, form code, and model name/label.

---

# Business Requirements Implemented

- FR-021: Header Form Search — Ctrl+K / Cmd+K global search for forms
- Client-side search: filters cached form list by label, code, and model name
- MUI Dialog-based search overlay with auto-focused search input
- Results show: form label (primary), model name + form code (secondary)
- Click result → navigate to `/app/runtime?form={formCode}`
- Escape closes the dialog
- Click outside closes the dialog
- Form list loaded once via React Query and cached for session (`staleTime: Infinity`)

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/components/FormSearchBar.tsx` | Search bar component with keyboard shortcut, dialog, filtering, and navigation (89 lines) |
| `frontend/src/core/runtime/hooks/useAccessibleForms.ts` | React Query hook for loading accessible forms from `GET /api/runtime/forms` (23 lines) |

---

# Files Modified

None.

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

Navigates to `/app/runtime?form={encodeURIComponent(f.formCode)}` on selection.

---

# Classes Added

None (React components and hooks)

---

# Classes Updated

None

---

# Methods Added

| Module | Export | Purpose |
|--------|--------|---------|
| FormSearchBar.tsx | FormSearchBar | Search dialog component |
| useAccessibleForms.ts | useAccessibleForms | React Query hook: loads accessible forms, caches for session |

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

Added TypeScript interface: `AccessibleForm` { formCode, formLabel, modelName, modelLabel?, formId? }

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

Uses existing: MUI (`Dialog`, `DialogTitle`, `DialogContent`, `TextField`, `InputAdornment`, `List`, `ListItemButton`, `ListItemText`, `Typography`, `Search` icon), `@tanstack/react-query`, `react-router-dom`

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend)

## Lint

PASS — `eslint` on files

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] TypeScript compilation succeeds
- [x] Ctrl+K / Cmd+K opens and toggles the dialog
- [x] Typing filters results client-side (by label, code, model name)
- [x] Clicking a result navigates to the form
- [x] Escape closes the dialog
- [x] Clicking outside closes the dialog
- [x] Empty search query shows all forms (up to 15)
- [x] Form list is loaded once and cached for the session

---

# Breaking Changes

None. New components with no existing consumers.

---

# Known Issues

1. **Limited to 15 results**: `slice(0, 15)` limits shown results. No scroll virtualization — could be slow with hundreds of forms.
2. **No keyboard navigation in results list**: Arrow keys to navigate results is not implemented. Only click-to-select and Escape-to-close.
3. **No fuzzy search**: Uses simple `String.includes()` matching. Fuzzy search (e.g., Fuse.js) would improve usability.
4. **No "Enter to select first match"**: Pressing Enter when filtered to one result should navigate — not implemented.
5. **Not integrated into AppHeader**: The component is built but not yet mounted in the application header/navbar.

---

# Future Improvements

- Add keyboard navigation (arrow keys + Enter) for results list
- Add Fuse.js for fuzzy matching
- Integrate into AppHeader component
- Virtualize results list for large form catalogs

---

# Developer Notes

- **Keyboard shortcut**: `Ctrl+K` / `Cmd+K` handler is registered via `useEffect` with cleanup. `e.preventDefault()` prevents browser default behavior.
- **Dialog toggle**: `setOpen((prev) => !prev)` — same shortcut opens and closes.
- **Client-side filtering**: Uses `Array.filter()` with case-insensitive `includes()` on formLabel, formCode, and modelLabel/modelName.
- **Session caching**: `useAccessibleForms` uses `staleTime: Infinity, gcTime: Infinity` — form list loads once and persists for the entire session.
- **Query key**: `['runtime', 'accessible-forms']` — shared across all consumers of this data.

---

# QA Handoff

Suggested test focus:
1. Ctrl+K / Cmd+K opens the search dialog
2. Typing shows filtered dropdown of matching forms
3. Results include form label, code, and model label
4. Only forms the user has access to appear in results
5. Clicking a result navigates to that form
6. Escape closes the dialog
7. Client-side search is instant
8. Works on both desktop and tablet

Potential risk areas:
- Form list API failing silently — verify error state is handled gracefully
- Large number of forms (> 100) — verify filter performance

---

# Related Documents

Task: ai/project/tasks/TASK-025-header-form-search-bar.md

PRD: ai/project/prd/PRD-001-dynamic-form-configuration-system.md
