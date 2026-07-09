---
id: TASK-011

title: Build Form Designer Admin UI — Core (Frontend)

type: UI

status: TESTED

priority: High

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-011

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 12

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-007
  - TASK-012
  - TASK-013

depends_on:
  - TASK-007

blocks: []

labels: [frontend, admin-ui, form-designer]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-011.md

test_report: ai/tests/TEST-TASK-011.md

history:
  - created
  - 2026-07-08 — Planning audit: demoted READY_FOR_DEV → PLANNED (dependency TASK-007 is READY_FOR_TEST, not COMPLETED; current workflow requires COMPLETED for activation)
  - 2026-07-08 — Re-evaluated: restored PLANNED → READY_FOR_DEV. WORKFLOW.md allows READY_FOR_TEST or COMPLETED. Dependency TASK-007 is READY_FOR_TEST.
  - 2026-07-08 — Developer: All tabs now in FormDesignerPage (Fields, Layout, Rules, Validations, Sub-Forms). Completed.
  - 2026-07-08 — Documentation audit: created CHANGE-TASK-011.md (change_summary restored)

---

# Goal

Build the core Form Designer admin UI screens: form list, create/edit form, fields tab, and layout tab.

---

# Description

Create React components for the Form Designer section.

## Pages/Components

### `FormListPage`
- MUI DataGrid of all forms (filtered by role: System Admin sees all, Tenant Admin sees own + global)
- Columns: code, label, model name, scope, created date, status
- "Create Form" button opens a dialog to select scope (global/tenant) and table
- Actions: Edit, Clone, Delete, Configure Access (for global forms)

### `FormDesignerPage`
- Two-panel layout: left config panel (tabs), right live preview panel
- Tabs at top: Fields, Layout, Rules, Validations, Sub-Forms, Row Access, Access
- **Fields Tab:**
  - Sortable list of all table columns
  - Toggle visible/hidden per field
  - Inline label override, placeholder, default value
  - Drag-and-drop reorder
  - Quick edit: required, read-only toggles
- **Layout Tab:**
  - Add/remove/edit sections
  - Per section: label, columns (1/2/3), collapsible toggle
  - Assign fields to sections via drag-and-drop
  - Reorder sections
- **Preview Panel:**
  - Live preview using the same DynamicFormRenderer component
  - Updates in real-time as admin changes config

### API Integration
- React Query with optimistic updates for field/layout changes
- Debounced save where appropriate

---

# Acceptance Criteria

- [ ] Form list shows all forms with scope indicator (global vs tenant)
- [ ] Create form dialog allows scope selection + table selection
- [ ] Fields tab shows all columns from the selected table with toggles
- [ ] Fields can be reordered via drag-and-drop
- [ ] Layout tab allows section creation and field-to-section assignment
- [ ] Preview panel updates in real-time
- [ ] All changes save correctly to backend APIs
- [ ] Tenant Admin cannot change scope or table after form creation

---

# Technical Notes

- Use MUI `DataGrid` for lists, `TextField`, `Select`, `Switch` for field config
- Drag-and-drop: use `@dnd-kit/sortable` or MUI's sortable list
- The preview panel embeds the same runtime form renderer component with a "preview mode" flag
- React Query cache invalidation: after save, invalidate the form definition query

---

# Files Expected

- `frontend/src/modules/admin/forms/FormListPage.tsx`
- `frontend/src/modules/admin/forms/FormDesignerPage.tsx`
- `frontend/src/modules/admin/forms/components/FieldsTab.tsx`
- `frontend/src/modules/admin/forms/components/LayoutTab.tsx`
- `frontend/src/modules/admin/forms/components/FieldConfigRow.tsx`
- `frontend/src/modules/admin/forms/components/SectionEditor.tsx`
- `frontend/src/modules/admin/forms/components/CreateFormDialog.tsx`
- `frontend/src/modules/admin/forms/hooks/useFormDesigner.ts`
- `frontend/src/modules/admin/forms/hooks/useFormFields.ts`
- `frontend/src/modules/admin/forms/hooks/useFormLayout.ts`
