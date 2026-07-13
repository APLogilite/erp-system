---
id: TASK-042

title: Seed Data — Register Business Tables (md_*, tx_*) as sys_table + sys_column

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

estimated_hours: 4

actual_hours:

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-036]

blocks: [TASK-043, TASK-044]

labels: [database, seed, flyway]

review_required: true

test_required: true

---

# Goal

Register all existing master data and transaction business tables in `sys_table` and `sys_column` so the runtime engine can discover them.

---

# Description

Insert rows into `sys_table` and `sys_column` for the physical PostgreSQL tables that already exist (created by PRD-003's Flyway migrations).

## Master data tables (md_)

| Table | Label | Plural Label | Type |
|-------|-------|-------------|------|
| `md_business_partner` | Business Partner | Business Partners | `static` |
| `md_product` | Product | Products | `static` |
| `md_uom` | Unit of Measure | Units of Measure | `static` |
| `md_uom_conversion` | UOM Conversion | UOM Conversions | `static` |
| `md_warehouse` | Warehouse | Warehouses | `static` |

## Transaction tables (tx_)

| Table | Label | Plural Label | Type |
|-------|-------|-------------|------|
| `tx_orders` | Order | Orders | `static` |
| `tx_order_lines` | Order Line | Order Lines | `static` |
| `tx_invoice` | Invoice | Invoices | `static` |
| `tx_invoice_lines` | Invoice Line | Invoice Lines | `static` |
| `tx_payment` | Payment | Payments | `static` |
| `tx_shipment` | Shipment | Shipments | `static` |
| `tx_shipment_line` | Shipment Line | Shipment Lines | `static` |

For each table, register all columns in `sys_column` with correct types matching the existing PostgreSQL DDL (string, integer, decimal, boolean, date, datetime, many2one, enum).

System columns (id, tenant_id, created_at, etc.) should NOT be registered — they are handled automatically by BaseEntity.

---

# Acceptance Criteria

- [ ] All 12 business tables registered in `sys_table`
- [ ] All business columns registered in `sys_column` with correct types
- [ ] No system columns registered (id, tenant_id, created_at, etc.)
- [ ] Many2one columns reference correct related table
- [ ] Enum columns include option list
- [ ] Data can be queried through runtime engine after registration

---

# Technical Notes

- Use Flyway migration (after TASK-036's schema migration)
- This replaces the PRD-003 FR-001 approach (old `sys_metadata_models` registration)
- Columns should match the existing PostgreSQL table definitions exactly
