---
task_id: TASK-044
type: Database
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-044
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-044

## Summary

Created V27 Flyway migration that seeds 10 ERP business windows with proper tabs, child tabs, and field configurations.

## Files Added

`backend/src/main/resources/db/migration/V27__seed_erp_windows.sql`

## Key Data Seeded

### Master Data Windows (4)
- Business Partners (7 fields), Products (6 fields), UOM (2 fields), Warehouses (3 fields)

### Transaction Windows (6)
- Sales Orders (Header tab + Lines child tab + Shipments child tab)
- Purchase Orders (Header tab + Lines child tab)
- Sales Invoices (Header tab + Lines child tab)
- Purchase Invoices (Header tab + Lines child tab)
- Payments (single tab, 10 fields)
- Shipments (single tab, 10 fields + Lines child tab)

### Window Configuration
- Where clause filters: `order_type = 'sales'` / `order_type = 'purchase'` for Sales/Purchase split
- `invoice_type = 'sales'` / `invoice_type = 'purchase'` for invoice split
- Parent column FK linking: Lines use `order_id` or `invoice_id`
- Field display settings: seq_no ordering, is_same_line for compact layouts
