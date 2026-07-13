---
id: TASK-012

title: Build Form Designer — Rules & Validation UI (Frontend)

type: UI

status: COMPLETED

priority: High

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-012

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 8

actual_hours: 3

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-008
  - TASK-011

depends_on:
  - TASK-008
  - TASK-011

blocks: []

labels: [frontend, admin-ui, rules, validation]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-012.md

test_report: ai/tests/TEST-TASK-012.md

history:
  - created
  - 2026-07-08 — Developer: Rules and Validations tabs integrated into FormDesignerPage. All tabs complete. Completed.
  - 2026-07-08 — Documentation audit: created CHANGE-TASK-012.md (change_summary restored)

---

# Goal

Build the Rules and Validation tabs in the Form Designer admin UI.

---

# Description

## Rules Tab

For each field in the form, admins can configure conditional rules.

**UI:**
- Select a field from the form (dropdown or click field in preview)
- Rule list for that field showing: `WHEN {field} {operator} {value} → {action}`
- Add rule button opens a condition builder:
  - **Condition field:** dropdown of all form fields
  - **Operator:** dropdown (equals, not_equals, greater_than, less_than, contains, is_empty, is_not_empty, in)
  - **Value:** text input (or multi-select for `in` operator)
  - **Action:** dropdown (show, hide, read_only, editable, required, optional)
- Multiple rules can be added with AND/OR logic group selector
- Rules can be deleted or reordered
- Visual indicator in the preview panel when a rule is active

## Validation Tab

For each field, admins can configure validation constraints.

**UI:**
- Select a field from the form
- Add validation button opens:
  - **Type:** dropdown (required, min_length, max_length, min, max, pattern, custom_expression)
  - **Value:** conditional input based on type (number for min/max, text for pattern/expression)
  - **Message:** custom error message text
- Validation list shows all validations with edit/delete actions
- Preview panel shows validation in action when test data is entered

---

# Acceptance Criteria

- [ ] Rules tab shows all fields with their configured rules
- [ ] Rule condition builder has correct operator options
- [ ] Rules can be grouped with AND/OR logic
- [ ] Validation tab shows all validations per field
- [ ] Validation types show appropriate value inputs
- [ ] Rules and validations save correctly to backend APIs
- [ ] Preview panel reflects rules (field hides/shows based on sample data)

---

# Technical Notes

- The condition builder should dynamically show relevant operators based on field type (e.g., `contains` only for string fields)
- Preview test: admin enters sample values in preview, rules evaluate in real-time
- Use the expression validation API to validate custom expressions/patterns on save

---

# Files Expected

- `frontend/src/modules/admin/forms/components/RulesTab.tsx`
- `frontend/src/modules/admin/forms/components/RuleBuilder.tsx`
- `frontend/src/modules/admin/forms/components/ValidationTab.tsx`
- `frontend/src/modules/admin/forms/components/ValidationBuilder.tsx`
- `frontend/src/modules/admin/forms/components/ConditionBuilder.tsx`
- `frontend/src/modules/admin/forms/hooks/useFormRules.ts`
- `frontend/src/modules/admin/forms/hooks/useFormValidations.ts`
