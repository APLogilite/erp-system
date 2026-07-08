---
id: CHANGE-TASK-024

task_id: TASK-024

parent_prd: PRD-001

branch: feature/TASK-024

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 1h

related_commits:
  - 3745d0e

related_files:
  - frontend/src/core/runtime/components/FormBreadcrumb.tsx

review_required: true

test_required: true

---

# Summary

Built the `FormBreadcrumb` component (43 lines) that displays the current record's hierarchy path using MUI `Breadcrumbs` with clickable segments. Each breadcrumb segment (except the last) is a clickable `Link` that navigates to the corresponding form/record. Uses the breadcrumb data from `useForm()` and React Router's `useNavigate` for navigation.

---

# Business Requirements Implemented

- FR-018: Breadcrumb Navigation (Frontend) — display clickable breadcrumb trail
- Standard breadcrumb trail with ">" separators (MUI `Breadcrumbs` with `ChevronRight` icon)
- Form link (no recordId): navigates to the form's view
- Record link (with recordId): navigates to that record's view
- Active/last segment: plain `Typography` (not clickable)
- Uses MUI `Breadcrumbs` component with `maxItems` support for truncation

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/core/runtime/components/FormBreadcrumb.tsx` | Breadcrumb navigation component (43 lines) |

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

None (consumes breadcrumb data from useForm hook)

---

# Routes

Navigates to `/app/runtime?form={formCode}&record={recordId}` via React Router `useNavigate`.

---

# Classes Added

None (React component)

---

# Classes Updated

None

---

# Methods Added

| Module | Export | Purpose |
|--------|--------|---------|
| FormBreadcrumb.tsx | FormBreadcrumb | Main component accepting `breadcrumb: BreadcrumbEntry[]` prop |

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

Uses existing TypeScript types: `BreadcrumbEntry` from `useForm.types.ts`

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

Uses existing: MUI (`Breadcrumbs`, `Link`, `Typography`, `ChevronRight`), `react-router-dom` (`useNavigate`)

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend)

## Lint

PASS — `eslint` on file

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] TypeScript compilation succeeds
- [x] Breadcrumb renders breadcrumb entries as clickable links
- [x] Last segment is plain text (not clickable)
- [x] Empty/undefined breadcrumb returns null (no render)
- [x] Navigation uses correct URL pattern

---

# Breaking Changes

None. New component with no existing consumers.

---

# Known Issues

1. **Unsaved changes dialog**: The PRD specifies showing a confirmation dialog when navigating away with unsaved changes. This is deferred — the `isDirty` prop is not wired in this implementation.
2. **No form-level breadcrumb entries**: Only record-level breadcrumb entries are supported. Form-level entries (navigating to list view, not a specific record) are handled by passing breadcrumb entries without `recordId`.
3. **Truncation**: Long breadcrumbs are handled by MUI's built-in `maxItems`/collapsed items feature, but no custom truncation logic is added.

---

# Future Improvements

- Wire `isDirty` prop to show `UnsavedChangesDialog` from TASK-022
- Add custom collapsed items rendering for long breadcrumbs
- Add keyboard navigation support

---

# Developer Notes

- **Conditional render**: Returns `null` if breadcrumb prop is undefined or empty — component is invisible when not needed.
- **URL encoding**: Form codes and record IDs are encoded with `encodeURIComponent` in navigation URLs.
- **MUI Breadcrumbs**: Uses the built-in MUI component which handles overflow automatically via `maxItems` and `itemsAfterCollapse`/`itemsBeforeCollapse` props (not explicitly set — uses defaults).
- The component is designed to be used inside `DynamicFormRenderer` or any parent layout, receiving breadcrumb data from `useForm().breadcrumb`.

---

# QA Handoff

Suggested test focus:
1. Breadcrumb displays the full path from root to current record
2. Each segment is clickable
3. Last segment is not clickable
4. Empty/undefined breadcrumb renders nothing
5. Navigating via breadcrumb works correctly
6. Responsive: long breadcrumbs truncate on small screens

Potential risk areas:
- Missing `recordId` on form-level entries — navigation should still work (goes to list view)
- Undefined breadcrumb entries from useForm during loading state

---

# Related Documents

Task: ai/tasks/TASK-024-breadcrumb-navigation.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md
