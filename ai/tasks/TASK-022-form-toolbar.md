---
id: TASK-022

title: Build Form Toolbar with Record Actions & Keyboard Shortcuts (Frontend)

type: UI

status: IN_DEVELOPMENT

priority: High

owner: developer

assigned_to: developer

assigned_branch: feature/TASK-022

locked: true

created: 2026-07-07

updated: 2026-07-08

started: 2026-07-08

completed:

estimated_hours: 8

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

depends_on:
  - TASK-019

blocks: []

labels: [frontend, component, toolbar, keyboard-shortcuts]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Build the form toolbar component with Create, Save, Discard, Refresh, Delete, Previous/Next actions and keyboard shortcuts (FR-022).

---

# Description

Create `FormToolbar` component in `frontend/src/core/runtime/components/`.

## Component API

```typescript
interface FormToolbarProps {
  mode: 'list' | 'create' | 'edit' | 'view';
  isDirty: boolean;               // Unsaved changes exist?
  isSaving: boolean;              // Currently saving?
  recordIndex?: number;           // Current position in list (1-indexed)
  totalRecords?: number;          // Total records in current context
  hasPrevious: boolean;
  hasNext: boolean;
  canDelete: boolean;             // Role permission check
  onCreateNew: () => void;
  onSave: () => void;
  onSaveAndNew: () => void;
  onDiscard: () => void;
  onRefresh: () => void;
  onDelete: () => void;
  onPrevious: () => void;
  onNext: () => void;
}
```

## Toolbar Layout

```
[Create +]  [Save] [Save & New]  [Discard] [Refresh]  [Delete]  |  < Prev | 3 of 15 | Next >  (sticky bar)
```

## Behavior by Mode

### List View
- Shows: Create New, Refresh, pagination controls
- No record context

### Record View (Edit)
- Shows: Create New, Save, Discard, Refresh, Delete, Previous/Next
- Save enabled only when `isDirty === true`
- Discard enabled only when `isDirty === true`
- Previous/Next disabled at boundaries
- Shows "Record 3 of 15" context

### Record View (Create)
- Shows: Save, Save & New, Discard
- Previous/Next hidden

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+S` | Save |
| `Ctrl+Shift+S` | Save & New (create mode only) |
| `Escape` | Discard (with confirmation if dirty) |
| `F5` | Refresh (with confirmation if dirty) |
| `Alt+Left` | Previous record |
| `Alt+Right` | Next record |

## Components
- `FormToolbar.tsx` — Main toolbar with all buttons
- `RecordNavigator.tsx` — Previous/Next + "X of Y" display
- `UnsavedChangesDialog.tsx` — Confirmation dialog shown when discarding with unsaved changes
- Keyboard shortcut hook: `useKeyboardShortcuts()`

## Styling
- Sticky/fixed position at top (below breadcrumb)
- MUI `AppBar` or `Toolbar` component
- Responsive: on small screens, collapse into a "more" menu

---

# Acceptance Criteria

- [ ] Toolbar renders correctly in all modes (list, create, edit)
- [ ] Save button is disabled when not dirty
- [ ] Discard button shows confirmation dialog when dirty
- [ ] Previous/Next navigates through records
- [ ] "Record X of Y" updates correctly
- [ ] All keyboard shortcuts work
- [ ] Ctrl+S does not trigger browser's save-as
- [ ] Toolbar is sticky on scroll
- [ ] Delete shows confirmation dialog
- [ ] Toolbar respects permissions (canDelete prop)

---

# Technical Notes

- Use `useEffect` with `keydown` event listener for keyboard shortcuts
- Prevent default browser behavior for Ctrl+S and F5
- The toolbar's Previous/Next calls the data navigation which triggers `useForm()` with the next record ID
- The `isDirty` state comes from a form-level dirty tracking hook

---

# Files Expected

- `frontend/src/core/runtime/components/FormToolbar.tsx`
- `frontend/src/core/runtime/components/RecordNavigator.tsx`
- `frontend/src/core/runtime/components/UnsavedChangesDialog.tsx`
- `frontend/src/core/runtime/hooks/useKeyboardShortcuts.ts`
- `frontend/src/core/runtime/hooks/useDirtyTracking.ts`
