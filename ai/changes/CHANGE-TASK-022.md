---
id: CHANGE-TASK-022

task_id: TASK-022

parent_prd: PRD-001

branch: feature/TASK-022

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 5h

related_commits: [5f85bbe, 38f34ea]

related_files:
  - frontend/src/core/runtime/components/FormToolbar.tsx
  - frontend/src/core/runtime/components/RecordNavigator.tsx
  - frontend/src/core/runtime/components/UnsavedChangesDialog.tsx
  - frontend/src/core/runtime/hooks/useKeyboardShortcuts.ts
  - frontend/src/core/runtime/hooks/useDirtyTracking.ts

review_required: true

test_required: true

---

# Summary

Built the form toolbar component system with record actions (Create, Save, Save & New, Discard, Refresh, Delete) and Previous/Next record navigation. Includes keyboard shortcuts (Ctrl+S, Escape, F5, Alt+Left/Right), unsaved-changes confirmation dialog, dirty tracking, and mode-sensitive rendering (list/create/edit/view). The toolbar is sticky-positioned at the top of the form view.

---

# Business Requirements Implemented

- FR-022: Form Toolbar — record actions toolbar with keyboard shortcuts and unsaved-changes protection
- Mode-sensitive rendering: different buttons shown for list, create, edit, and view modes
- Keyboard shortcuts: Ctrl+S (Save), Ctrl+Shift+S (Save & New), Escape (Discard), F5 (Refresh), Alt+Left/Right (Navigate)
- Unsaved changes confirmation dialog on Discard/Refresh when form is dirty
- Record context display: "Record X of Y" with Previous/Next navigation
- Responsive design: collapses to icon-only buttons on small screens

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/components/FormToolbar.tsx` | Main toolbar: mode-sensitive button layout (Create, Save, Save&New, Discard, Refresh, Delete), sticky AppBar |
| `frontend/src/core/runtime/components/RecordNavigator.tsx` | Previous/Next navigation with "Record X of Y" context display |
| `frontend/src/core/runtime/components/UnsavedChangesDialog.tsx` | Confirmation dialog with Discard/Keep editing options |
| `frontend/src/core/runtime/hooks/useKeyboardShortcuts.ts` | Keyboard event listener hook: maps key combos to callbacks, prevents browser defaults |
| `frontend/src/core/runtime/hooks/useDirtyTracking.ts` | Form dirty state tracking: compares current values against initial snapshot |

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

None

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
| FormToolbar | `FormToolbar` | Main toolbar component with mode-sensitive button layout |
| RecordNavigator | `RecordNavigator` | Previous/Next buttons with record index display |
| UnsavedChangesDialog | `UnsavedChangesDialog` | Confirmation dialog for discarding changes |
| useKeyboardShortcuts | `useKeyboardShortcuts()` | Registers keyboard shortcuts with cleanup |
| useDirtyTracking | `useDirtyTracking()` | Tracks whether form values differ from initial state |

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

Uses existing: MUI (`AppBar`, `Toolbar`, `Button`, `IconButton`, `Tooltip`, `Dialog`, `useMediaQuery`, `useTheme`), MUI Icons (`Add`, `Save`, `Undo`, `Delete`, `Refresh`, `ContentCopy`), React hooks (`useCallback`, `useEffect`, `useState`)

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend, 0 errors)

## Lint

PASS — `eslint --max-warnings=0` on toolbar component files

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] Toolbar renders correctly in all modes (list, create, edit, view)
- [x] Save button disabled when not dirty
- [x] Discard button shows confirmation dialog when dirty
- [x] Previous/Next navigates through records
- [x] "Record X of Y" updates correctly
- [x] All keyboard shortcuts registered (Ctrl+S, Ctrl+Shift+S, Escape, F5, Alt+Left, Alt+Right)
- [x] Ctrl+S prevented from triggering browser save dialog
- [x] Toolbar uses sticky AppBar positioning
- [x] Delete shows confirmation dialog
- [x] Responsive: collapses to icon-only on small screens
- [x] TypeScript compilation succeeds

---

# Breaking Changes

None. New components with no existing consumers.

---

# Known Issues

1. **Save & New**: The "Save & New" button calls `onSaveAndNew()` but the actual navigation to a new record after save requires integration with the form's submit flow. The callback is defined but the consumer must handle the redirect.
2. **Delete confirmation**: Currently uses `window.confirm()` as a fallback if `UnsavedChangesDialog` is bypassed. Should be replaced with a proper MUI Dialog in a future iteration.

---

# Future Improvements

- Add "Duplicate" action for cloning records
- Add customizable toolbar via form metadata (hide/show specific buttons)
- Add keyboard shortcut hints in tooltips
- Replace `window.confirm()` delete fallback with MUI Dialog

---

# Developer Notes

- **Keyboard shortcut cleanup**: `useKeyboardShortcuts` returns a cleanup function used in `useEffect`. All event listeners are properly removed on unmount.
- **Browser default prevention**: Ctrl+S, F5, and Alt+Left/Right all call `e.preventDefault()` to prevent browser behavior.
- **Dirty tracking**: `useDirtyTracking` performs a shallow comparison of initial vs current values. For complex nested objects, deep comparison may be needed.
- **Sticky positioning**: Toolbar uses MUI `AppBar` with `position="sticky"` and `top: 0` for fixed-at-top behavior during scroll.

---

# QA Handoff

Suggested test focus:
1. Toolbar renders ALL modes correctly (list, create, edit, view)
2. Save button greyed out when form is not dirty; active when dirty
3. Discard while dirty shows confirmation dialog
4. Discard while clean proceeds without dialog
5. Keyboard shortcuts: Ctrl+S saves, Escape discards (with confirm if dirty), F5 refreshes
6. Previous/Next navigation with boundary disabling
7. Record counter updates correctly
8. Responsive behavior on narrow viewports
9. Delete button shows/hides based on `canDelete` prop

Potential risk areas:
- Keyboard shortcuts conflicting with browser/OS shortcuts
- Dirty tracking false positives with complex field types
- Sticky positioning conflicts with other fixed elements
