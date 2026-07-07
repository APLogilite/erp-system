---
id: TASK-021

title: Build Client-Side Rules Engine (Frontend)

type: Feature

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 6

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

parent_task:

related_tasks:
  - TASK-020

depends_on:
  - TASK-020

blocks: []

labels: [frontend, rules, engine, runtime]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the client-side rules engine that evaluates field conditions (visibility, read-only, required) in real-time as the user interacts with the form.

---

# Description

Create `FormRulesEngine` in `frontend/src/core/runtime/`.

## Core Function

```typescript
interface FieldState {
  visible: boolean;
  readOnly: boolean;
  required: boolean;
}

function evaluateFieldState(
  fieldConfig: FormField,
  allFieldValues: Record<string, any>,
  allFieldConfigs: FormField[]
): FieldState
```

## Rule Evaluation

For a given field, evaluate all its rules:

1. Start with base state from field config (visible, read_only, required from form designer)
2. For each rule, check if the condition is met:
   - Look up `conditionField` value in `allFieldValues`
   - Apply `conditionOperator`:
     - `equals`: `value === conditionValue`
     - `not_equals`: `value !== conditionValue`
     - `greater_than`: `Number(value) > Number(conditionValue)`
     - `less_than`: `Number(value) < Number(conditionValue)`
     - `contains`: `String(value).includes(conditionValue)`
     - `is_empty`: `value === null || value === undefined || value === ''`
     - `is_not_empty`: `value !== null && value !== undefined && value !== ''`
     - `in`: `Array.isArray(conditionValue) && conditionValue.includes(value)`
3. If condition is met, apply the action:
   - `show` / `hide` → override visibility
   - `read_only` / `editable` → override readOnly
   - `required` / `optional` → override required
4. If multiple rules exist, handle AND/OR logic groups

## Integration with Form Renderer

- The renderer calls `evaluateFieldState()` for every field whenever any field value changes
- Results are used to show/hide fields, enable/disable, and toggle required
- Debounce evaluation to avoid performance issues (50ms)
- Only re-evaluate fields whose dependencies changed (optimization)

## React Hook

```typescript
function useFieldStates(
  fields: FormField[],
  values: Record<string, any>
): Record<string, FieldState>
```

Returns a map of field_code → FieldState, recomputed whenever values change.

---

# Acceptance Criteria

- [ ] All operators (equals, not_equals, gt, lt, contains, is_empty, is_not_empty, in) work correctly
- [ ] Actions (show, hide, read_only, editable, required, optional) update field state properly
- [ ] AND/OR logic groups are supported
- [ ] Field state updates in real-time as dependent field values change
- [ ] Performance: 50+ rules evaluated in < 100ms
- [ ] Rules engine is a pure function (testable without React)
- [ ] When a field is hidden by a rule, its value is not submitted

---

# Technical Notes

- The engine is a pure function — easy to unit test
- Use `useMemo` in the hook to avoid unnecessary re-computation
- When a field is hidden by rules, clear its value from the submission payload (unless it has a default)
- Rules reference field codes from the form definition

---

# Files Expected

- `frontend/src/core/runtime/rules/FormRulesEngine.ts`
- `frontend/src/core/runtime/rules/useFieldStates.ts`
- `frontend/src/core/runtime/rules/ruleOperators.ts`
- `frontend/src/core/runtime/rules/__tests__/FormRulesEngine.test.ts`
