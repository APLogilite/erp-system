---
id: TASK-032

title: Seed Line Forms and Sub-Form Configurations (Flyway Migration)

type: Database

status: READY_FOR_TEST

priority: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-032

locked: true

created: 2026-07-10

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 3

actual_hours: 1

parent_prd: PRD-003

prd_version: 1.0.0

prd_branch: prd/PRD-003-erp-order-flow-forms

base_branch: main

merge_target: prd/PRD-003-erp-order-flow-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on:
  - TASK-031

blocks: []

labels:
  - database
  - flyway
  - seed
  - forms
  - sub-forms

review_required: true

test_required: true

automation_required: false

change_summary: ai/changes/CHANGE-TASK-032.md

test_report:

history:
  - 2026-07-10 — Planner — Created task from PRD-002 v1.0.0
  - 2026-07-13 — Software Engineer — Activated to IN_DEVELOPMENT, started implementation
  - 2026-07-13 — Software Engineer — Created V23 Flyway migration with 4 line forms + 7 sub-form configs

---

# Goal

Create the final Flyway migration(s) that:
1. Define 4 line-item forms (Order Line, Invoice Line, Shipment Line, MR Line)
2. Configure sub-form links connecting each header form to its line form

This is the last piece — once complete, the header-line drill-down experience works end-to-end.

---

# Description

Create two Flyway migration files (can be combined into one if the line count is manageable):

- `V{next}__seed_transaction_line_forms.sql`
- `V{next+1}__seed_sub_form_configs.sql`

Or a single combined file:
- `V{next}__seed_line_forms_and_sub_forms.sql`

### Part 1 — Clean Existing (idempotency)

```sql
-- Clean sub-form configs first (FK to forms)
DELETE FROM sys_form_sub_forms WHERE parent_form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice','purchase_shipment','sales_shipment','material_receipt'));

-- Clean line form data
DELETE FROM sys_form_section_fields WHERE section_id IN (SELECT id FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('order_line','invoice_line','shipment_line','mr_line')));
DELETE FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('order_line','invoice_line','shipment_line','mr_line'));
DELETE FROM sys_form_fields WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('order_line','invoice_line','shipment_line','mr_line'));
DELETE FROM sys_metadata_views WHERE name IN ('order_line','invoice_line','shipment_line','mr_line');
```

### Part 2 — Insert Line Form Definitions

| name | model_name | description |
|------|-----------|-------------|
| order_line | tx_order_line | Line items for orders (used by both purchase and sales) |
| invoice_line | tx_invoice_line | Line items for invoices |
| shipment_line | tx_shipment_line | Line items for shipments |
| mr_line | tx_mr_line | Line items for material receipts |

All forms: `type='form'`, `scope='global'`, `tenant_id=NULL`.

### Part 3 — Insert Line Form Fields

The parent foreign key (order_id, invoice_id, etc.) is included in the form but will be auto-populated from the sub-form context when opened as a tab. It is hidden when accessed through the sub-form tab (PRD-001 handles this).

#### Form: order_line

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | order_id | Order | ✓ | Auto-set from parent |
| 2 | line_number | Line # | ✓ | |
| 3 | product_id | Product | ✓ | |
| 4 | description | Description | | |
| 5 | quantity | Quantity | | |
| 6 | uom_id | UOM | | |
| 7 | unit_price | Unit Price | | |
| 8 | line_total | Line Total | | |
| 9 | tax_rate | Tax Rate (%) | | |

#### Form: invoice_line

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | invoice_id | Invoice | ✓ | Auto-set from parent |
| 2 | line_number | Line # | ✓ | |
| 3 | product_id | Product | ✓ | |
| 4 | description | Description | | |
| 5 | quantity | Quantity | | |
| 6 | uom_id | UOM | | |
| 7 | unit_price | Unit Price | | |
| 8 | line_total | Line Total | | |
| 9 | tax_rate | Tax Rate (%) | | |
| 10 | order_line_id | Source Order Line | | |

#### Form: shipment_line

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | shipment_id | Shipment | ✓ | Auto-set from parent |
| 2 | line_number | Line # | ✓ | |
| 3 | product_id | Product | ✓ | |
| 4 | description | Description | | |
| 5 | quantity | Quantity | | |
| 6 | uom_id | UOM | | |
| 7 | order_line_id | Source Order Line | | |

#### Form: mr_line

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | receipt_id | Receipt | ✓ | Auto-set from parent |
| 2 | line_number | Line # | ✓ | |
| 3 | product_id | Product | ✓ | |
| 4 | description | Description | | |
| 5 | ordered_qty | Ordered Qty | | |
| 6 | received_qty | Received Qty | | |
| 7 | uom_id | UOM | | |
| 8 | order_line_id | Source Order Line | | |
| 9 | shipment_line_id | Source Shipment Line | | |

### Part 4 — Insert Layout Sections

All line forms use a single flat section:

| Section Code | Label | Columns | Position |
|-------------|-------|:---:|:---:|
| items | Line Details | 2 | 1 |

### Part 5 — Insert Section-Field Mappings

All fields go into section 'items' in position order matching the field position above.

### Part 6 — Insert Sub-Form Configurations (sys_form_sub_forms)

This is the critical piece that links header forms to their line forms. Each row defines one sub-form tab.

```sql
INSERT INTO sys_form_sub_forms (id, parent_form_id, relation_code, child_form_code, label, display_as, position, created_at, updated_at)
SELECT gen_random_uuid(), parent.id, 'order_id', 'order_line', 'Order Lines', 'tab', 1, now(), now()
FROM sys_metadata_views parent WHERE parent.name = 'purchase_order';
```

| Parent Form | relation_code | Child Form | Tab Label | Pos |
|-------------|--------------|-----------|-----------|:---:|
| purchase_order | order_id | order_line | Order Lines | 1 |
| sales_order | order_id | order_line | Order Lines | 1 |
| purchase_invoice | invoice_id | invoice_line | Invoice Lines | 1 |
| sales_invoice | invoice_id | invoice_line | Invoice Lines | 1 |
| purchase_shipment | shipment_id | shipment_line | Shipment Lines | 1 |
| sales_shipment | shipment_id | shipment_line | Shipment Lines | 1 |
| material_receipt | receipt_id | mr_line | MR Lines | 1 |

> Note: `relation_code` is the column on the **child** table that stores the parent's ID (the FK column). For example, `tx_order_line.order_id` references `tx_order.id`, so `relation_code = 'order_id'`.

All entries: `display_as = 'tab'`.

---

# Acceptance Criteria

- [ ] Flyway migration file(s) exist
- [ ] Migration cleans existing data before inserting
- [ ] 4 rows inserted into `sys_metadata_views` for line forms
- [ ] ~33 field rows inserted into `sys_form_fields` (9+10+7+9 — parent FK excluded where auto-set)
- [ ] 4 layout section rows inserted
- [ ] All section-field mappings are complete
- [ ] 7 sub-form config rows inserted into `sys_form_sub_forms`
- [ ] Each sub-form config has correct `relation_code` (matches child table FK column)
- [ ] Each sub-form config has correct `child_form_code` (matches `sys_metadata_views.name`)
- [ ] Migration runs successfully after TASK-031
- [ ] After migration + restart, line forms appear in `GET /api/runtime/forms`
- [ ] Opening a purchase_order record shows an "Order Lines" tab
- [ ] The Order Lines tab shows an editable inline grid
- [ ] Adding a line via the tab creates a child record with parent FK auto-assigned
- [ ] Clicking a line item drills down with breadcrumb (Order > #123 > Order Lines > Line #1)

---

# Technical Notes

### Sub-Form relation_code
Must match the exact column code in `sys_table_columns` for the FK column on the child table. Double-check:
- `tx_order_line` has column `order_id` → `relation_code = 'order_id'`
- `tx_invoice_line` has column `invoice_id` → `relation_code = 'invoice_id'`
- `tx_shipment_line` has column `shipment_id` → `relation_code = 'shipment_id'`
- `tx_mr_line` has column `receipt_id` → `relation_code = 'receipt_id'`

### Shared Line Forms
The same `order_line` form is used as a sub-form for both `purchase_order` and `sales_order`. This is intentional — the form definition is shared, only the parent context differs. Two sub-form config rows reference the same child form code.

### Parent FK in Line Forms
The parent FK field (order_id, invoice_id, etc.) is included in the form field list so it can be viewed in standalone mode (when drilling down). PRD-001's runtime handles hiding/auto-populating it when the form is opened as a sub-form tab.

### Flyway Version
Use `{next}` = TASK-031's version + 1. If splitting into two files, use +1 and +2.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_transaction_line_forms.sql` (new)
- `backend/src/main/resources/db/migration/V{next+1}__seed_sub_form_configs.sql` (new)
- *(Or one combined file)*

---

# Developer Notes

*(maintained by Software Engineer)*

---

# Tester Notes

*(maintained by QA Engineer)*

---

# Review Notes

---

# Task History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-10 | Planner | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — ERP Order Flow Forms](../prd/PRD-002-erp-order-flow-forms.md)
- [TASK-031 — Seed Transaction Header Forms](../tasks/TASK-031-seed-transaction-header-forms.md)
