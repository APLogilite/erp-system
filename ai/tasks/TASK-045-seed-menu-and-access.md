---
id: TASK-045

title: Seed Data — Menu Entries + Window Access

type: Database

status: TESTED

priority: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-045

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 3

actual_hours: 1

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-043, TASK-044]

blocks: []

labels: [database, seed, flyway, menu]

history:
  - 2026-07-13: Status PLANNED → IN_DEVELOPMENT. Assigned to Software Engineer. Started implementation.
  - 2026-07-13: V28 Flyway migration created. Menu tree + window access seeded. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 12/12 tests passed, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-045.md

change_report: ai/changes/CHANGE-TASK-045.md

---

# Goal

Seed the menu tree and default window access entries so users can navigate and use windows immediately after deployment.

---

# Description

## Menu tree

Create `sys_menu` entries for the following hierarchy:

```
Administration                          (group)
├── Table Definitions                   (window → admin_table_definitions)
├── Window Definitions                  (window → admin_window_definitions)
├── Window Tabs                         (window → admin_window_tabs)
├── Window Fields                       (window → admin_window_fields)
├── Window Access                       (window → admin_window_access)
└── Menu Configuration                  (window → admin_menu_configuration)

Master Data                             (group)
├── Business Partners                   (window → Business Partners)
├── Products                            (window → Products)
├── Units of Measure                    (window → UOM)
└── Warehouses                          (window → Warehouses)

Transactions                            (group)
├── Sales                               (group)
│   ├── Sales Orders                    (window → Sales Orders)
│   └── Sales Invoices                  (window → Sales Invoices)
├── Purchasing                          (group)
│   ├── Purchase Orders                 (window → Purchase Orders)
│   └── Purchase Invoices               (window → Purchase Invoices)
├── Payments                            (window → Payments)
└── Shipments                           (window → Shipments)
```

Each entry should have proper `seq_no` for ordering within its parent group.

## Window access

Create `sys_window_access` entries granting default access to the system admin role for all windows. Tenant-specific access can be configured later through the admin UI.

---

# Acceptance Criteria

- [ ] All menu groups and items created with correct parent-child relationships
- [ ] Menu items correctly reference their windows via `window_id`
- [ ] Group entries have `type='group'`, window entries have `type='window'`
- [ ] Seq_no values provide logical ordering
- [ ] System admin role has access to all windows
- [ ] Menu renders correctly in the frontend menu component

---

# Technical Notes

- Use Flyway migration (after TASK-044)
- Menu tree uses self-referencing parent_id FK
- Root-level entries have parent_id = null
