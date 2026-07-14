---
id: TASK-025

title: Build Header Form Search Bar (Ctrl+K) (Frontend)

type: UI

status: COMPLETED

priority: Medium

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-025

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 6

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-019

depends_on:
  - TASK-019

blocks: []

labels: [frontend, component, search, header]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-025.md

test_report: ai/tests/TEST-TASK-025.md

history:
  - created
  - 2026-07-08 — Planning audit: status → READY_FOR_TEST. Implementation found on disk (FormSearchBar.tsx 89L). No change summary found — Developer should create one.
  - 2026-07-08 — Documentation audit: created CHANGE-TASK-025.md (change_summary restored)

---

# Goal

Build the global form search bar in the application header (Ctrl+K / Cmd+K) that allows users to quickly find and navigate to forms.

---

# Description

Create `FormSearchBar` component in `frontend/src/core/runtime/components/`.

## Component API
- No props needed — it accesses `useForm()` or a dedicated list of accessible forms

## Behavior
- Search bar placed in the application header/top bar
- Keyboard shortcut: `Ctrl+K` (Windows/Linux) or `Cmd+K` (Mac) focuses the search bar
- As user types, dropdown shows matching forms:
  - Match against form label (fuzzy/partial match)
  - Match against form code (prefix match)
  - Results filtered to only show forms the user has access to
- Each result shows: form label, form code (secondary), table label
- Click a result → navigate to that form's list view
- Press `Enter` with a single match → navigate directly
- Type a form code + `Enter` → navigate directly (exact match)
- `Escape` closes the dropdown and blurs
- Click outside closes the dropdown

## Data Source
- Uses the cached form list from `GET /api/runtime/forms`
- Search is performed client-side (no additional API calls)
- Form list is loaded on app startup and cached for the session

## Implementation
- MUI `TextField` with `InputAdornment` (search icon)
- Popper/Dropdown with `Autocomplete`-like behavior
- Custom hook: `useFormSearch()` for the search logic
- Keyboard event listener at the document level for Ctrl+K

---

# Acceptance Criteria

- [ ] Search bar is visible in the header/navbar
- [ ] Ctrl+K / Cmd+K focuses the search bar
- [ ] Typing shows filtered dropdown of matching forms
- [ ] Results include form label, code, and table label
- [ ] Only forms the user has access to appear in results
- [ ] Clicking a result navigates to that form
- [ ] Escape closes the dropdown
- [ ] Client-side search is instant (< 100ms)
- [ ] Works on both desktop and tablet

---

# Technical Notes

- Use MUI `TextField` + `Popper` for the search UI
- Use Fuse.js for fuzzy matching if partial matching is needed
- The form list is loaded once via React Query and cached
- Register Ctrl+K handler with `useEffect` — prevent default browser behavior

---

# Files Expected

- `frontend/src/core/runtime/components/FormSearchBar.tsx`
- `frontend/src/core/runtime/hooks/useFormSearch.ts`
- Modified: `frontend/src/app/AppHeader.tsx` or equivalent header component
