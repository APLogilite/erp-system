---
id: TASK-024

title: Build Breadcrumb Navigation Component (Frontend)

type: UI

status: PLANNING

priority: Medium

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 4

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

parent_task:

related_tasks:
  - TASK-019

depends_on:
  - TASK-019

blocks: []

labels: [frontend, component, breadcrumb, navigation]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the breadcrumb navigation component that displays the current record's hierarchy path with clickable segments.

---

# Description

Create `FormBreadcrumb` component in `frontend/src/core/runtime/components/`.

## Component API

```typescript
interface FormBreadcrumbProps {
  breadcrumb: BreadcrumbEntry[];
  isDirty: boolean;
  onNavigate: (entry: BreadcrumbEntry) => void;
}
```

## Rendering
- Standard breadcrumb trail with ">" separators
- Each segment is clickable (MUI `Link` or `Button` with text style)
- Segment types:
  - **Form link** (no recordId): clicking navigates to the form's list view
  - **Record link** (with recordId): clicking navigates to that record's view
- Active/last segment is not clickable (plain text)
- Styled with MUI `Breadcrumbs` component

## Unsaved Changes Handling
- If `isDirty === true`, clicking a breadcrumb segment shows a confirmation dialog
- Dialog: "You have unsaved changes. Discard changes and navigate away?"
- "Discard & Navigate" / "Cancel"

---

# Acceptance Criteria

- [ ] Breadcrumb displays the full path from root to current record
- [ ] Each segment is clickable
- [ ] Last segment is not clickable
- [ ] Navigating via breadcrumb shows unsaved changes warning when dirty
- [ ] Breadcrumb uses MUI Breadcrumbs component
- [ ] Responsive: long breadcrumbs truncate with ellipsis on small screens

---

# Technical Notes

- The breadcrumb array comes from the data response's `breadcrumb` field via `useForm()`
- MUI `Breadcrumbs` with `maxItems` prop for truncation
- Use the `UnsavedChangesDialog` component from TASK-022

---

# Files Expected

- `frontend/src/core/runtime/components/FormBreadcrumb.tsx`
