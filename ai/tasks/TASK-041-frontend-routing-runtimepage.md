---
id: TASK-041

title: Frontend — Update Routing to /window/{name} + Fix RuntimePage

type: UI

status: TESTED

priority: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-041

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 8

actual_hours: 3

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-038, TASK-039, TASK-040, TASK-037]

blocks: []

labels: [frontend, ui, routing, runtime]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to Software Engineer. Started implementation.
  - 2026-07-13: WindowPage component created. /window/{windowName} route added. Legacy /runtime redirect added. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 11/11 tests passed, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-041.md

change_report: ai/changes/CHANGE-TASK-041.md

---

# Goal

Change frontend routes from `/runtime/{formCode}` to `/window/{windowName}` and fix the RuntimePage to render actual dynamic forms from the API instead of hardcoded sample bundles.

---

# Description

## Route changes

- Add new route: `/window/{windowName}` → renders `WindowPage`
- Remove or redirect old route: `/runtime/{formCode}`
- Update all navigation, breadcrumbs, and Ctrl+K search to use new route

## WindowPage component

Create `WindowPage` that:
1. Fetches window definition from `GET /api/runtime/windows/{windowName}/definition`
2. Shows a list view (records from `GET /api/runtime/windows/{windowName}/records`)
3. Clicking a record opens detail view with:
   - Main tab fields rendered using the form renderer
   - Child tabs showing inline grids of related records
   - Field display/readonly logic evaluated client-side
4. Create/Save/Delete operations call the data API

## Fix RuntimePage

- Replace the existing `RuntimePage` (which uses hardcoded sample bundles) with the new `WindowPage`
- Remove all hardcoded sample data from the component
- Ensure all existing form rendering from the form renderer (`DynamicFormRenderer`, `FormFieldRenderer`) is reused

## Field logic evaluation

- Implement client-side evaluation of `display_logic` and `readonly_logic` expressions
- Expressions use `@FieldName@=Value` format (e.g. `@PaymentRule@=Credit` shows credit card field)
- Fields with `is_displayed=false` are permanently hidden
- Fields with `display_logic` are conditionally shown
- Fields with `readonly_logic` are conditionally read-only

---

# Acceptance Criteria

- [ ] Route `/window/{windowName}` loads window definition from API
- [ ] List view shows records with correct columns (from field definitions)
- [ ] Clicking record opens detail with tabs
- [ ] Child tabs show linked records via inline grid
- [ ] Fields render with correct types (string→TextField, date→DatePicker, etc.)
- [ ] Field display/readonly logic works client-side
- [ ] Create/Save/Delete operations call the API
- [ ] Ctrl+K search uses `/window/{windowName}` paths
- [ ] No hardcoded sample bundles remain in RuntimePage
- [ ] Old `/runtime/*` routes redirect to `/window/*`

---

# Technical Notes

- Reuse existing `DynamicFormRenderer` and `FormFieldRenderer` from PRD-001
- Reuse existing `useForm` hook or create a new `useWindow` hook
- Field logic evaluation can be a simple expression parser
- Route config in `frontend/src/core/router/`
