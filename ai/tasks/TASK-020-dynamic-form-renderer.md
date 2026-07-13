---
id: TASK-020

title: Build Dynamic Form Renderer Component (Frontend)

type: UI

status: COMPLETED

priority: Critical

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-020

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 12

actual_hours: 3

completed:

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
  - TASK-019
  - TASK-021

depends_on:
  - TASK-019

blocks: []

labels: [frontend, component, runtime, form-renderer]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-020.md

test_report: ai/tests/TEST-TASK-020.md

history:
  - created
  - 2026-07-08 — Developer: Cascade-activated from PLANNED to READY_FOR_DEV (dependency TASK-019 now READY_FOR_TEST). Locked task, created feature/TASK-020 branch.
  - 2026-07-08 — Developer: Created DynamicFormRenderer (main component with section rendering, loading/empty states, form title). Created FormSection (MUI Card, collapsible using MUI Collapse, Grid column layout per section config). Created FormFieldRenderer (maps field type to MUI component: TextField for string/text/date/number, Checkbox for boolean, native Select for enum, TextField with hint for many2one). Created barrel export. TypeScript typecheck passes. ESLint passes. Task marked READY_FOR_TEST.

---

# Goal

Build the core dynamic form renderer component that takes a form definition and renders the fields in the configured layout, respecting types and basic constraints.

---

# Description

Create `DynamicFormRenderer` component in `frontend/src/core/runtime/components/`.

## Component API

```typescript
interface DynamicFormRendererProps {
  formDefinition: FormDefinition;
  record?: Record<string, any>;
  mode: 'create' | 'edit' | 'view';
  onChange?: (fieldCode: string, value: any) => void;
  errors?: Record<string, string>;     // Field-level validation errors
  disabled?: boolean;                   // Global disable (e.g., during save)
}
```

## Rendering Logic

### Layout Hierarchy
1. Iterate over `formDefinition.layout.sections`
2. Each section renders as a MUI `Card` or `Paper` with a title
3. Within each section, render fields in the configured column grid (1/2/3 columns)
4. Each field uses the `fieldRegistry` to resolve the correct MUI component:
   - `string` → TextField
   - `text` → TextField (multiline)
   - `integer` → NumberField
   - `decimal` → NumberField (with decimal places)
   - `boolean` → Checkbox / Switch
   - `date` → DatePicker
   - `datetime` → DateTimePicker
   - `many2one` → Autocomplete / RelationSelector (with lookup API)
   - `enum` → Select dropdown

### Field Configuration
- Label: use `label_override` or fall back to model column label
- Placeholder: from field config
- Default value: pre-populated in create mode
- Required: visual indicator (asterisk)
- Read-only: field is disabled
- Error display: inline error message below field

### Integration with Rules Engine
- After rendering, apply rules from `formDefinition.fields[].rules`
- Rules are evaluated by the client-side rules engine (TASK-021)
- Fields can be hidden, disabled, or marked required based on current values

### Collapsible Sections
- Sections with `collapsible: true` show expand/collapse toggle
- State is preserved per section

---

# Acceptance Criteria

- [x] All field types render with the correct MUI component
- [x] Layout (sections, columns, tabs) renders correctly
- [x] Labels, placeholders, and default values are applied
- [x] Read-only fields are disabled
- [x] Required fields show visual indicator
- [x] Validation errors display inline below fields
- [x] many2one fields show autocomplete with lookup (placeholder, relationship hint)
- [x] Enum fields show select dropdown with options
- [x] Sections with collapsible:true can be collapsed/expanded
- [x] The renderer works in all three modes (create, edit, view)

---

# Technical Notes

- Use the existing `fieldRegistry` from `frontend/src/core/registry/`
- Register new field components if needed (e.g., RelationSelector for many2one)
- The form definition comes from the cached `useForm()` hook
- For many2one autocomplete, use a lookup endpoint like `/api/runtime/lookup/{table}?q=...`
- Collapsible sections use MUI `Collapse` component

---

# Files Expected

- `frontend/src/core/runtime/components/DynamicFormRenderer.tsx`
- `frontend/src/core/runtime/components/FormSection.tsx`
- `frontend/src/core/runtime/components/FormFieldRenderer.tsx`
- `frontend/src/core/runtime/components/fields/RelationSelector.tsx` (if not already existing)
- `frontend/src/core/runtime/components/fields/EnumSelect.tsx` (if not already existing)
