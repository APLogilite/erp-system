---
id: TASK-044

title: Seed Data — ERP Windows with Tabs/Fields (replaces PRD-003)

type: Database

status: READY_FOR_TEST

priority: High

owner: software_engineer

assigned_to: software_engineer

assigned_branch: feature/TASK-044

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 10

actual_hours: 2

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-042, TASK-037]

blocks: [TASK-045]

change_report: ai/changes/CHANGE-TASK-044.md

labels: [database, seed, flyway, erp]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to software_engineer. Started implementation.
  - 2026-07-13: V27 Flyway migration created. 10 ERP windows + tabs + fields seeded. Validation passed. Status → READY_FOR_TEST.

review_required: true

test_required: true

change_report: ai/changes/CHANGE-TASK-044.md

---

# Goal

Seed all ERP business windows with Windows/Tabs/Fields configuration, replacing what PRD-003 seeded on the old schema.

---

# Description

Create Windows/Tabs/Fields for all standard ERP transactions and master data.

## Master Data windows

| Window Name | Table | Description |
|-------------|-------|-------------|
| `Business Partners` | `md_business_partner` | Single tab, field order: code, name, partner_type, email, phone, address, tax_id |
| `Products` | `md_product` | Single tab, field order: code, name, description, product_type, uom_id, unit_price |
| `UOM` | `md_uom` | Single tab, fields: code, name |
| `Warehouses` | `md_warehouse` | Single tab, fields: code, name, address |

## Transaction windows

| Window Name | Table | Where Clause | Tabs |
|-------------|-------|-------------|------|
| `Sales Orders` | `tx_orders` | `order_type = 'sales'` | Header (seq 10) + Lines (seq 20) + Shipments (seq 30) |
| `Purchase Orders` | `tx_orders` | `order_type = 'purchase'` | Header (seq 10) + Lines (seq 20) |
| `Sales Invoices` | `tx_invoice` | `invoice_type = 'sales'` | Header (seq 10) + Lines (seq 20) + Payments (seq 30) |
| `Purchase Invoices` | `tx_invoice` | `invoice_type = 'purchase'` | Header (seq 10) + Lines (seq 20) |
| `Payments` | `tx_payment` | — | Header (seq 10) |
| `Shipments` | `tx_shipment` | — | Header (seq 10) + Lines (seq 20) |

## Tab details

Each header tab should have fields with proper `seq_no`, `is_same_line`, `is_displayed` settings:
- Order fields: order_number, order_date, partner_id, warehouse_id, currency, subtotal, grand_total, status
- Invoice fields: invoice_number, invoice_date, due_date, partner_id, order_id, currency, grand_total, status
- Product fields: code, name, description, product_type, uom_id, unit_price
- Partner fields: code, name, partner_type, email, phone, address

Child tabs (Lines, Shipments, Payments) link via `parent_column`:
- Lines tab: `parent_column = order_id` (for orders), `parent_column = invoice_id` (for invoices)
- Shipments tab: `parent_column = order_id`
- Payment allocations: `parent_column = invoice_id` (for invoice payments)

---

# Acceptance Criteria

- [ ] All 4 master data windows created with correct fields
- [ ] All 6 transaction windows created with header + child tabs
- [ ] Where clause filtering works (sales vs purchase split)
- [ ] Child tabs use parent_column for FK filtering
- [ ] Fields ordered logically with is_same_line where appropriate
- [ ] Field types match column definitions (string→TextField, many2one→autocomplete, etc.)
- [ ] Display/readonly logic is set where applicable

---

# Technical Notes

- Use Flyway migration (after TASK-042)
- This replaces PRD-003's seeded forms, rebuilt on the new Window/Tab/Field schema
- All windows should be immediately usable after seeding
