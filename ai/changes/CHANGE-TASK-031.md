---
id: CHANGE-TASK-031

task_id: TASK-031

parent_prd: PRD-003

branch: feature/TASK-031

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: 1.5h

related_commits:
  - (pending commit)

related_files:
  - backend/src/main/resources/db/migration/V22__seed_transaction_header_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V22__seed_transaction_header_forms.sql` that defines 9 transaction header forms with purchase/sales variants using where_clause filtering. Each form includes field configuration, layout sections, and section-field mappings. Covers Orders, Invoices, Payments, Shipments, and Material Receipts.

---

# Business Requirements Implemented

- [x] 9 rows inserted into `sys_metadata_views`
- [x] ~96 field rows inserted into `sys_form_fields` (12+12+14+14+9+9+9+9+9)
- [x] 17 layout sections inserted (2 per form × 8 + 1 for payment forms × 2)
- [x] All section-field mappings completed
- [x] Type discriminator fields (order_type, invoice_type, etc.) excluded from form fields
- [x] where_clause configured for purchase/sales variants
- [x] Partner_id labels differ per variant ("Supplier" vs "Customer")
- [x] material_receipt has no where_clause (no purchase/sales variant)
- [x] Idempotent

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V22__seed_transaction_header_forms.sql` | 9 transaction header forms |

---

# Validation

## Build

PASS — `mvn clean compile` succeeded.

---

# Related Documents

- [TASK-031](../tasks/TASK-031-seed-transaction-header-forms.md)
- [PRD-003](../prd/PRD-003-erp-order-flow-forms.md)
