---
id: BUG-014

title: Status columns render as text input instead of dropdown (missing enum_options)

status: IN_DEVELOPMENT

priority: Medium

severity: Medium

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: prd/PRD-005-v2

locked: true

created: 2026-07-30

updated: 2026-07-30

parent_prd: PRD-005

parent_task: (seed data)

reported_by: QA Engineer

detected_in: Manual QA — status fields in transaction forms (Sales/Purchase Orders, Invoices, Payments, Shipments) show as free-text input instead of a dropdown selector

fix_summary: Update sys_column.type from 'string' to 'enum' for the 4 status columns, and set enum_options with valid status values. Add Flyway V9 migration.

history:
  - 2026-07-30 — QA Engineer — Created (user report). Root cause: sys_column rows for status on tx_order/tx_invoice/tx_payment/tx_shipment have type='string' and enum_options=NULL; FormFieldRenderer requires type='enum' + enumOptions for dropdown rendering.

---

# Summary

The status field in transaction records (Sales Orders, Purchase Orders, Sales Invoices, Purchase Invoices, Payments, Shipments) renders as a generic text input. Users expect a dropdown with valid status values.

# Root Cause

`sys_column` rows for the `status` column on `tx_order`, `tx_invoice`, `tx_payment`, `tx_shipment` have `type = 'string'` and `enum_options IS NULL`. The `FormFieldRenderer` (line 588) renders a dropdown only when `col.type === 'enum' && col.enumOptions`.

# Fix

New idempotent Flyway migration `V9__status_enum_options.sql`:

| Table | Enum Options |
|-------|-------------|
| `tx_order` | `["draft","confirmed","shipped","delivered","cancelled"]` |
| `tx_invoice` | `["draft","posted","paid","cancelled"]` |
| `tx_payment` | `["pending","completed","failed","refunded"]` |
| `tx_shipment` | `["draft","in_transit","delivered","returned"]` |

# Acceptance Criteria

- [ ] V9 migration updates the 4 status columns: type='enum', enum_options set
- [ ] Dropdown renders in forms for all status fields
- [ ] Existing data (tx_shipment.status='completed') still valid against new enum
- [ ] `mvn clean compile` / 36 tests pass
