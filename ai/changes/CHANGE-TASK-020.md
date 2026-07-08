---
document: CHANGE_REPORT
task: TASK-020
status: COMPLETE
created: 2026-07-08
---

# Change Report — TASK-020

## Summary

Built the Dynamic Form Renderer component system that takes a `FormDefinition` and renders fields organized in layout sections using the appropriate MUI component for each field type. Supports three modes (create, edit, view), collapsible sections, column grids, inline validation errors, and read-only/required field states.

## Files Added

| File | Description |
|------|-------------|
| `frontend/src/core/runtime/components/DynamicFormRenderer.tsx` | Main renderer: iterates layout sections, renders form title, loading/empty states |
| `frontend/src/core/runtime/components/FormSection.tsx` | Section renderer: MUI Card with collapsible behavior (Collapse), Grid column layout |
| `frontend/src/core/runtime/components/FormFieldRenderer.tsx` | Field type resolver: maps field types to MUI components |
| `frontend/src/core/runtime/components/index.ts` | Barrel export |

## Files Modified

None.

## Validation Results

| Check | Result |
|-------|--------|
| `tsc --noEmit` | PASS |
| `eslint --max-warnings=0` (TASK-020 files) | PASS |

## Acceptance Criteria

- [x] All field types render with the correct MUI component:
  - `string` → TextField
  - `text` → TextField (multiline)
  - `integer` → TextField (type=number)
  - `decimal` → TextField (type=number)
  - `boolean` → Checkbox with FormControlLabel
  - `date` → TextField (type=date)
  - `datetime` → TextField (type=datetime-local)
  - `many2one` → TextField with relation table hint
  - `enum` → native Select with options
- [x] Layout sections render with correct column grid (1/2/3 columns)
- [x] Labels, placeholders from field config applied
- [x] Read-only fields disabled
- [x] Required fields show asterisk (via `required` prop on MUI TextField)
- [x] Validation errors display inline below fields (via `error`/`helperText` props)
- [x] many2one shows relation table name as helper hint
- [x] Enum shows dropdown with options from `field.enumOptions`
- [x] Collapsible sections toggle via MUI `Collapse`
- [x] All three modes (create, edit, view) work — view mode disables all inputs, renders using `filled` variant

## Key Implementation Decisions

1. **Direct MUI component mapping**: Instead of using the async fieldRegistry (which would require `Suspense` boundaries), fields map directly to MUI components based on their `type` string. This avoids async complexity and provides immediate rendering.

2. **HTML5 date inputs**: Since `@mui/x-date-pickers` is not in the project dependencies, date/datetime fields use HTML5 `<input type="date">` and `<input type="datetime-local">` wrapped in MUI TextField. This provides native date pickers in most browsers.

3. **Default section fallback**: If the form definition has no sections configured, `DynamicFormRenderer` creates a default section containing all fields in a single column. This ensures the form is always renderable even before layout is configured.

4. **Number type coercion**: When `input type="number"`, the value is converted to a `Number` before calling `onChange`. Empty string input yields `undefined`.

5. **View mode**: Renders all fields as disabled `variant="filled"` TextFields (read-only appearance). Boolean fields in view mode render as disabled checkboxes.

## Known Limitations

1. **many2one autocomplete**: Currently renders as a plain TextField with a hint showing the related table name. A proper `RelationSelector` with server-side lookup (TASK-023 requirement) requires a lookup API endpoint and debounced search. This is deferred to TASK-023.

2. **DatePicker/DateTimePicker**: Uses HTML5 native inputs instead of MUI date picker components. Adding `@mui/x-date-pickers` would enable richer date selection UX.

3. **Rules engine integration**: Field visibility/read-only rules (from `field.rules`) are not yet evaluated in the renderer. This is deferred to TASK-021 (Client-Side Rules Engine).

4. **Tab layout**: The layout renderer handles sections only, not a top-level tabs layout (which would require additional layout metadata). Tab-based layouts are deferred.

5. **Keyboard shortcuts / toolbar**: Not included in this component. Deferred to TASK-022 (Form Toolbar).

## Breaking Changes

None. New components with no existing consumers.

## Follow-up Recommendations

- **TASK-021**: Build client-side rules engine to evaluate field visibility/read-only/required rules
- **TASK-022**: Build form toolbar (Save, Discard, Delete, Previous/Next)
- **TASK-023**: Build RelationSelector with server-side lookup for many2one fields
- **Future**: Add `@mui/x-date-pickers` for rich date picker components
