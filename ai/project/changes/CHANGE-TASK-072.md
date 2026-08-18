---
change_id: CHANGE-TASK-072
task_id: TASK-072
parent_prd: PRD-007
author: Software Engineer
date: 2026-08-14
---

# Change Report — TASK-072: Add payment term to Order and Invoice tables

## Summary
Added `payment_term_id` to `tx_order` and `tx_invoice`, exposed it in the runtime forms, and implemented backend enrichment so orders inherit the partner's default term and invoices auto-calculate `due_date` from the term's days.

## Files Added
- `backend/src/main/resources/db/migration/V19__add_payment_term_to_order_invoice.sql`
- `backend/src/main/java/com/erp/modules/common/service/PaymentTermService.java`

## Files Modified
- `backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java`
- `ai/project/schema/transactions/tx_order.sql`
- `ai/project/schema/transactions/tx_invoice.sql`
- `ai/project/scripts/verify-prd-007-data.sql`
- `ai/project/tasks/TASK-072-add-payment-term-to-order-invoice.md`

## Database Changes
- Added `payment_term_id` UUID FK to `tx_order` and `tx_invoice`.
- Registered both columns in `sys_column`.
- Added fields to Sales Orders, Purchase Orders, Sales Invoices, and Purchase Invoices windows.
- Added indexes `idx_tx_order_payment_term` and `idx_tx_inv_payment_term`.

## API / Business Logic Changes
- New `PaymentTermService` in `modules/common/service`:
  - `enrichOrderPaymentTerm(data)` — pre-fills `payment_term_id` from `md_business_partner.payment_term_id` when creating an order.
  - `enrichInvoiceDueDate(data)` — sets `due_date = invoice_date + term.days` when `due_date` is empty.
- `RecordCrudService.createRecord` and `updateRecord` now call `applyPaymentTermEnrichment` for `tx_order` and `tx_invoice`.

## Validation Results
- `mvn clean compile` — PASS
- `mvn test` — 36 tests, 0 failures, BUILD SUCCESS

## Notes
- Migration is idempotent via `ADD COLUMN IF NOT EXISTS` and `ensure_column`/`ensure_field` helpers.
- The enrichment runs only when the relevant fields are present in the payload; explicit `due_date` values are preserved.
