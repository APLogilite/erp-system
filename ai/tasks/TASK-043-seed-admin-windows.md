---
id: TASK-043

title: Seed Data — Admin Windows for Metadata Management (replaces PRD-002)

type: Database

status: TESTED

priority: High

owner: software_engineer

assigned_to: software_engineer

assigned_branch: feature/TASK-043

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 6

actual_hours: 2

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-037, TASK-042]

blocks: [TASK-045]

labels: [database, seed, flyway, admin]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to software_engineer. Started implementation.
  - 2026-07-13: V26 Flyway migration created. 7 admin windows + tabs + fields seeded. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 10/10 tests passed, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-043.md

change_report: ai/changes/CHANGE-TASK-043.md

---

# Goal

Create admin Windows/Tabs/Fields for all metadata tables so administrators can manage configuration through the same runtime interface.

---

# Description

Create the following admin windows via Flyway seed data. Each window gets a `sys_window` entry, one or more `sys_tab` entries, and `sys_window_field` entries for each field.

## Admin windows

| Window Name | Table | Tabs |
|-------------|-------|------|
| `Table Definitions` | `sys_table` | Main tab (table fields) + Columns sub-tab |
| `Table Columns` | `sys_column` | Main tab (column fields) — child of Table |
| `Window Definitions` | `sys_window` | Main tab (window fields) + Tabs sub-tab + Access sub-tab |
| `Window Tabs` | `sys_tab` | Main tab (tab fields) — child of Window |
| `Window Fields` | `sys_window_field` | Main tab (field fields) — child of Tab |
| `Window Access` | `sys_window_access` | Main tab (access entries) — child of Window |
| `Menu Configuration` | `sys_menu` | Main tab (menu entries in tree view) |

## Tab structure

For each window, the first tab (`seq_no=10`) is the main record list. Where applicable, a second tab (`seq_no=20`) shows child records:

- `Table Definitions` → Tab "Columns" shows related `sys_column` records
- `Window Definitions` → Tab "Tabs" shows related `sys_tab` records, Tab "Access" shows `sys_window_access` records
- `Window Tabs` → Tab "Fields" shows related `sys_window_field` records

## Field configuration

Each field should set:
- `seq_no` — display order
- `is_same_line` — where appropriate for compact layouts
- `is_displayed` — hide system/internal fields
- `is_readonly` — set for auto-generated fields
- `is_mandatory` — set for required columns

---

# Acceptance Criteria

- [ ] All 7 admin windows created with correct tabs and fields
- [ ] Parent-child relationships work (Table → Columns, Window → Tabs → Fields)
- [ ] All sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu columns covered
- [ ] Fields are ordered logically with proper labels
- [ ] Admin users can manage all metadata through the UI

---

# Technical Notes

- Use Flyway migration (after TASK-042)
- This replaces PRD-002's admin forms, rebuilt on the new schema
- Window names follow the naming: `admin_{entity}` pattern
