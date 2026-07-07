---
id: TASK-020

title: Build Dynamic Form Renderer Component (Frontend)

type: UI

status: PLANNED

priority: Critical

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 12

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

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

change_summary:

test_report:

history:
  - created

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

- [ ] All field types render with the correct MUI component
- [ ] Layout (sections, columns, tabs) renders correctly
- [ ] Labels, placeholders, and default values are applied
- [ ] Read-only fields are disabled
- [ ] Required fields show visual indicator
- [ ] Validation errors display inline below fields
- [ ] many2one fields show autocomplete with lookup
- [ ] Enum fields show select dropdown with options
- [ ] Sections with collapsible:true can be collapsed/expanded
- [ ] The renderer works in all three modes (create, edit, view)

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
