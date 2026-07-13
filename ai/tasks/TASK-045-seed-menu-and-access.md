---
id: TASK-045

title: Seed Data — Menu Entries + Window Access

type: Database

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-13

updated: 2026-07-13

started:

completed:

estimated_hours: 3

actual_hours:

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-043, TASK-044]

blocks: []

labels: [database, seed, flyway, menu]

review_required: true

test_required: true

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
