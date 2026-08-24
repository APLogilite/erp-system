---
change_id: CHANGE-TASK-071
task_id: TASK-071
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-071: Add credit limit and payment term to Business Partner

## Summary
Extended `md_business_partner` with credit management fields (`credit_limit`, `credit_hold`) and a default `payment_term_id` foreign key. Registered the new columns in metadata and added them to the existing **Business Partners** runtime window.

## Files Added
- `backend/src/main/resources/db/migration/V18__add_credit_to_partner.sql`

## Files Modified
- `ai/project/schema/master-data/md_business_partner.sql`
- `ai/project/scripts/verify-prd-007-data.sql`
- `ai/project/tasks/TASK-071-add-credit-payment-term-to-partner.md`

## Database Changes
- Added columns to `md_business_partner`:
  - `credit_limit` DECIMAL(15,2)
  - `credit_hold` BOOLEAN NOT NULL DEFAULT false
  - `payment_term_id` UUID → FK to `md_payment_term(id)`
- Added index `idx_md_bp_payment_term`.
- Registered 3 new columns in `sys_column`.
- Added 3 fields to the **Business Partners** window (`sys_window_field`).

## API Changes
None — CRUD continues through the existing runtime window API.

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Migration is idempotent via `ADD COLUMN IF NOT EXISTS` and `ensure_column` / `ensure_field` helpers.
