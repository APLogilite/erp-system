---
change_id: CHANGE-TASK-070
task_id: TASK-070
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-070: Create Payment Term table with seed data and metadata

## Summary
Created the `md_payment_term` master table, seeded 5 standard payment terms, registered the table/columns in the metadata engine, and exposed it via a runtime window under **Master Data → Payment Terms**.

## Files Added
- `backend/src/main/resources/db/migration/V16__create_payment_term_table.sql`
- `backend/src/main/resources/db/migration/V17__seed_payment_term_form.sql`
- `ai/project/schema/master-data/md_payment_term.sql`
- `ai/project/scripts/verify-prd-007-data.sql`

## Files Modified
- `ai/project/tasks/TASK-070-create-payment-term-table.md`

## Database Changes
- New table `md_payment_term` with columns: `id`, `code`, `name`, `days`, `discount_percent`, `discount_days`, system columns.
- 5 seed rows: NET0, NET15, NET30, NET60, NET90.
- Metadata registration in `sys_table` / `sys_column`.
- Window "Payment Terms" with tab "Term" and fields registered in `sys_window` / `sys_tab` / `sys_window_field`.
- Menu entry added under **Master Data** in `sys_menu`.
- `sys_window_access` granted to `sys_admin`.

## API Changes
None — CRUD is provided by the existing runtime window API (`/api/v1/runtime/windows/Payment%20Terms/*`).

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Flyway must be temporarily enabled (`spring.flyway.enabled=true`) for V16/V17 to execute on an existing database.
- Idempotent seeding using `WHERE NOT EXISTS` guards.
