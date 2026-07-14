---
id: TASK-023

title: Build Sub-Form Tabs & Inline Grids (Frontend)

type: UI

status: COMPLETED

priority: High

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-023

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 10

actual_hours: 6

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-019
  - TASK-020

depends_on:
  - TASK-019
  - TASK-020

blocks: []

labels: [frontend, component, sub-forms, grid]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-023.md

test_report: ai/tests/TEST-TASK-023.md

history:
  - created
  - 2026-07-08 — Implemented: SubFormTabPanel, InlineEditableGrid, SubFormTabBadge, useSubFormGrid. Merged into prd/PRD-001-dynamic-form-configuration. Set READY_FOR_TEST.

---

# Goal

Build the sub-form tab rendering and inline editable grid components that appear below parent record fields.

---

# Description

## SubFormTabPanel Component

Renders horizontal MUI `Tabs` below the parent form fields. Each tab corresponds to a configured sub-form.

```typescript
interface SubFormTabPanelProps {
  subForms: SubFormDefinition[];      // From form definition
  subFormRecords: Record<string, Record[]>;  // From data response  
  parentRecordId: string;
  onDrillDown: (formCode: string, recordId: string) => void;
}
```

## InlineEditableGrid Component

Each tab renders an inline editable data grid showing child records.

Features:
- MUI DataGrid or AG Grid showing child records
- Columns determined by the child form's fields (from sub-form definition)
- Inline editing: double-click a cell to edit
- Add row: bottom row or "+" button
- Delete row: checkbox selection + delete button
- Changes are collected and saved in batch with the parent record
- Empty state: "No records yet. Click + to add."

## SubFormRecordCount Badge
- Each tab shows a badge with the count of child records (e.g., "Order Lines (3)")

---

# Acceptance Criteria

- [ ] Sub-form tabs render below the parent record fields
- [ ] Each tab shows an inline editable grid of child records
- [ ] New rows can be added inline
- [ ] Cells can be edited inline
- [ ] Rows can be deleted
- [ ] Grid columns match the child form's visible fields
- [ ] Empty state is shown when no child records exist
- [ ] Tab badges show record counts
- [ ] Clicking a row triggers drill-down navigation
- [ ] Unsaved grid changes are tracked and warned on navigation

---

# Technical Notes

- AG Grid Enterprise is the standard grid component (per architecture blueprint)
- Fall back to MUI DataGrid if AG Grid is not available
- Inline edits are collected and sent as a batch update with the parent
- The child form definition comes from the sub-form definition in the parent's form definition bundle

---

# Files Expected

- `frontend/src/core/runtime/components/SubFormTabPanel.tsx`
- `frontend/src/core/runtime/components/InlineEditableGrid.tsx`
- `frontend/src/core/runtime/components/SubFormTabBadge.tsx`
- `frontend/src/core/runtime/hooks/useSubFormGrid.ts`
