---
task_id: TASK-045
type: Database
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-045
status: READY_FOR_TEST
created: 2026-07-13
author: software_engineer
---

# Change Report — TASK-045

## Summary

Created V28 Flyway migration that seeds the hierarchical menu tree and default window access entries for the system admin role.

## Files Added

`backend/src/main/resources/db/migration/V28__seed_menu_and_access.sql`

## Key Data Seeded

### Menu Tree Structure
```
Administration (group)
  ├── Table Definitions → admin_table_definitions
  ├── Window Definitions → admin_window_definitions
  ├── Window Tabs → admin_window_tabs
  ├── Window Fields → admin_window_fields
  ├── Window Access → admin_window_access
  └── Menu Configuration → admin_menu_configuration

Master Data (group)
  ├── Business Partners → Business Partners
  ├── Products → Products
  ├── Units of Measure → UOM
  └── Warehouses → Warehouses

Transactions (group)
  ├── Sales (group)
  │   ├── Sales Orders → Sales Orders
  │   ├── Sales Invoices → Sales Invoices
  │   ├── Payments → Payments
  │   └── Shipments → Shipments
  └── Purchasing (group)
      ├── Purchase Orders → Purchase Orders
      └── Purchase Invoices → Purchase Invoices
```

### Access Control
- System admin role (`sys_admin`) granted access to all 17 windows
- Tenant_id set to null (global access)
- Granular role-based access to be configured through admin UI
