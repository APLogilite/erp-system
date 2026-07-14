---
task_id: TASK-042
type: Database
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-042
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-042

## Summary

Created V25 Flyway migration that registers all 12 business tables (5 master data + 7 transaction) in the new `sys_table` and `sys_column` metadata tables. This enables the runtime engine to discover and interact with these tables through the Window/Tab/Field API.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/resources/db/migration/V25__register_business_tables.sql` | Registers md_* and tx_* tables in sys_table + sys_column |

## Files Modified

None

## Files Removed

None

## Database Changes

### Tables Registered in `sys_table`

| Name | Label | Physical Table | Columns Registered |
|------|-------|---------------|-------------------|
| `md_business_partner` | Business Partner | `md_business_partner` | 7 (code, name, partner_type, email, phone, address, tax_id) |
| `md_product` | Product | `md_product` | 6 (code, name, description, product_type, uom_id, unit_price) |
| `md_uom` | Unit of Measure | `md_uom` | 2 (code, name) |
| `md_uom_conversion` | UOM Conversion | `md_uom_conversion` | 4 (from_uom_id, to_uom_id, product_id, factor) |
| `md_warehouse` | Warehouse | `md_warehouse` | 3 (code, name, address) |
| `tx_order` | Order | `tx_order` | 13 (order_number through notes) |
| `tx_order_line` | Order Line | `tx_order_line` | 9 (order_id through tax_rate) |
| `tx_invoice` | Invoice | `tx_invoice` | 15 (invoice_number through notes) |
| `tx_invoice_line` | Invoice Line | `tx_invoice_line` | 10 (invoice_id through order_line_id) |
| `tx_payment` | Payment | `tx_payment` | 10 (payment_number through status) |
| `tx_shipment` | Shipment | `tx_shipment` | 10 (shipment_number through notes) |
| `tx_shipment_line` | Shipment Line | `tx_shipment_line` | 7 (shipment_id through order_line_id) |

### Key Details

- All tables registered with `table_type = 'static'` (physical tables managed outside the dynamic engine)
- Columns include: string, text, integer, decimal, boolean, date, enum, many2one types
- `many2one` columns include `relation_table` references (e.g., `partner_id → md_business_partner`)
- `enum` columns include `enum_options` JSONB arrays
- System columns (id, tenant_id, created_at, etc.) are NOT registered
- Existing tables `tx_material_receipt` and `tx_mr_line` are NOT registered (consolidated into `tx_shipment` per PRD-004 design)

## API Changes

None

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

None (new seed data in new tables only)

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |

## Known Limitations

- `tx_material_receipt` and `tx_mr_line` are not registered (deprecated in PRD-004 in favor of unified `tx_shipment` with `movement_type`)

## Follow-up Recommendations

- TASK-043 (Seed admin windows) can start now — depends on TASK-037 (READY_FOR_TEST ✓) and TASK-042 (now READY_FOR_TEST ✓)
- TASK-044 (Seed ERP windows) can start — depends on TASK-042 (READY_FOR_TEST ✓) and TASK-037 (READY_FOR_TEST ✓)
