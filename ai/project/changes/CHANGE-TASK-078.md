---
document: CHANGE_REPORT
task_id: TASK-078
version: 1.0.0
status: DRAFT
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-078

## Task

Create Sales Territory table with metadata (PRD-008).

## Files Changed

- `ai/project/schema/crm/md_sales_territory.sql`
- `backend/src/main/resources/db/migration/V25__create_sales_territory_table.sql`
- `backend/src/main/resources/db/migration/V26__seed_sales_territory_form.sql`

## Summary

Created the `md_sales_territory` master-data table to support PRD-008 CRM pipeline & sales team management. The table stores territory code, name, and an optional manager reference to `identity_users`. Registered the table and columns in the metadata engine (`sys_table`, `sys_column`), seeded four default territories, and created a runtime window/menu entry under **Master Data → Sales Territories**.

## Schema

- `md_sales_territory`
  - `id` UUID PK
  - `code` VARCHAR(20) NOT NULL
  - `name` VARCHAR(100) NOT NULL
  - `manager_id` UUID FK → `identity_users(id)`
  - `tenant_id`, `is_active`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`
  - Unique constraint on `(code, tenant_id)`

## Migrations

- **V25** — Creates the table, seeds NORTH/SOUTH/EAST/WEST territories, and registers metadata.
- **V26** — Creates the `Sales Territories` window/tab/fields, grants `sys_admin` access, and adds the menu item.

## Testing Notes

- Requires `spring.flyway.enabled=true` to execute V25/V26.
- After startup, admin users can navigate to **Master Data → Sales Territories** to view/create/update territories.

## Known Issues

None.
