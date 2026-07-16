---
id: CHANGE-TASK-012

task_id: TASK-012

parent_prd: PRD-001

branch: feature/TASK-012

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 3h

related_commits:
  - 3745d0e
  - aa378d8
  - 75ea6cf

related_files:
  - frontend/src/modules/admin/forms/components/RulesTab.tsx
  - frontend/src/modules/admin/forms/components/ValidationTab.tsx
  - frontend/src/modules/admin/forms/hooks/useFormRules.ts
  - frontend/src/modules/admin/forms/hooks/useFormValidations.ts

review_required: true

test_required: true

---

# Summary

Built the Rules and Validation tabs for the Form Designer Admin UI. The Rules tab allows per-field conditional rules with operator/action configuration and AND/OR logic grouping. The Validation tab allows per-field validation constraints with type, value, and custom error messages. Both tabs integrate into the existing FormDesignerPage tab structure delivered by TASK-011.

---

# Business Requirements Implemented

- FR-009b: Field Rules Configuration — conditional rules per field with condition builder
- FR-009c: Field Validation Configuration — validation constraints per field
- Rules: condition field selector, operator dropdown (equals, not_equals, greater_than, less_than, contains, is_empty, is_not_empty, in), value field, action dropdown (show, hide, read_only, editable, required, optional)
- Rules: AND/OR logic grouping via numeric `logicGroup` field
- Validations: type dropdown (required, min_length, max_length, min, max, pattern, custom_expression), value field, custom error message
- React Query hooks with proper cache invalidation

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/modules/admin/forms/components/RulesTab.tsx` | Rules tab: field selector, rules table, inline add rule with condition builder (field/operator/value/action/group), delete (121 lines) |
| `frontend/src/modules/admin/forms/components/ValidationTab.tsx` | Validation tab: field selector, validations table (type/value/message), inline add with type dropdown, delete (110 lines) |
| `frontend/src/modules/admin/forms/hooks/useFormRules.ts` | React Query hooks: useFormRules, useAddRule, useUpdateRule, useDeleteRule + FieldRule interface (63 lines) |
| `frontend/src/modules/admin/forms/hooks/useFormValidations.ts` | React Query hooks: useFormValidations, useAddValidation, useUpdateValidation, useDeleteValidation + FieldValidation interface (61 lines) |

---

# Files Modified

None. All files are new.

---

# Files Removed

None

---

# Database Changes

None (frontend only)

---

# API Changes

None (consumes existing backend APIs: `/api/v1/metadata/forms/{formId}/fields/{fieldId}/rules`, `/api/v1/metadata/forms/{formId}/fields/{fieldId}/validations`)

---

# Routes

None (tabs inside existing `/app/admin/forms/:formId` route)

---

# Classes Added

None (React components, hooks, types)

---

# Classes Updated

None

---

# Methods Added

| Module | Export | Purpose |
|--------|--------|---------|
| RulesTab.tsx | RulesTab | Rules tab with condition builder |
| ValidationTab.tsx | ValidationTab | Validations tab with constraint builder |
| useFormRules.ts | useFormRules | GET rules for a field query |
| useFormRules.ts | useAddRule | POST add rule mutation |
| useFormRules.ts | useUpdateRule | PUT update rule mutation |
| useFormRules.ts | useDeleteRule | DELETE rule mutation |
| useFormValidations.ts | useFormValidations | GET validations for a field query |
| useFormValidations.ts | useAddValidation | POST add validation mutation |
| useFormValidations.ts | useUpdateValidation | PUT update validation mutation |
| useFormValidations.ts | useDeleteValidation | DELETE validation mutation |

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

Added TypeScript interfaces: FieldRule, FieldValidation

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

Uses existing: MUI (Table, TextField, Select, Button, IconButton, Typography, Box, MenuItem), `@tanstack/react-query`, `@/core/api/client`

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
- [x] Rules tab shows field selector and configured rules
- [x] Rule condition builder has all 8 operator options
- [x] Rules support AND/OR logic grouping via numeric `logicGroup`
- [x] Validation tab shows field selector and configured validations
- [x] All 7 validation types available
- [x] Add/delete rules and validations via mutations

---

# Breaking Changes

None. New tabs integrated into existing FormDesignerPage tab structure.

---

# Known Issues

1. **No AND/OR toggle UI**: The `logicGroup` field supports grouping via numeric values, but there is no UI toggle for AND vs OR logic. Group semantics (AND between groups, OR within groups) are implicit.
2. **No dynamic operator filtering**: Operators are not filtered by field type (e.g., `contains` is shown for numeric fields).
3. **No field reference lookup**: The condition field dropdown shows all form fields without indicating available options from the selected field's column type.
4. **No preview panel integration**: Rules and validations aren't rendered in a live preview — only the configuration tables are shown.

---

# Future Improvements

- Add AND/OR radio toggle for logic group configuration
- Dynamically filter operators based on condition field type
- Integrate with live preview panel for real-time rule/validation testing
- Add bulk import/export for rules and validations

---

# Developer Notes

- **RulesTab**: Uses a field selector dropdown to filter rules by field. Inline add form with 5 fields (conditionField, conditionOperator, conditionValue, action, logicGroup). Rules display in a table with code-formatted conditionField, strong-formatted action.
- **ValidationTab**: Uses a field selector dropdown. Inline add form with 3 fields (type, value, message). Validation types are hardcoded (7 options). Validations display in a simple table.
- Both tabs use the same pattern: select field → show existing items → toggle inline add form → save/delete via mutations.
- Query key hierarchy: `['admin', 'forms', formId, 'fields', fieldId, 'rules']` and `['admin', 'forms', formId, 'fields', fieldId, 'validations']`.
- Empty states: "No rules configured for this field." / "No validations configured for this field."

---

# QA Handoff

Suggested test focus:
1. Rules tab shows all fields with their configured rules
2. Rule condition builder has correct operator options
3. Rules can be grouped with logicGroup values
4. Validation tab shows all validations per field
5. Validation types show appropriate value inputs
6. Rules and validations save correctly to backend APIs
7. Inline add form resets after successful save

Potential risk areas:
- Inline add form state leaks between fields if field selector changes during active add
- Duplicate rules/validations (no uniqueness constraint in UI)

---

# Related Documents

Task: ai/project/tasks/TASK-012-form-designer-rules-validation-ui.md

PRD: ai/project/prd/PRD-001-dynamic-form-configuration-system.md
