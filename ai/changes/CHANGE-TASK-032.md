---
id: CHANGE-TASK-032

task_id: TASK-032

parent_prd: PRD-003

branch: feature/TASK-032

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: 1h

related_commits:
  - (pending commit)

related_files:
  - backend/src/main/resources/db/migration/V23__seed_line_forms_and_sub_forms.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V23__seed_line_forms_and_sub_forms.sql` that defines 4 line-item forms and 7 sub-form tab configurations. This completes the PRD-003 header-line drill-down experience — each header form (purchase/sales order, invoice, shipment, material receipt) now has a sub-form tab linking to its corresponding line items.

---

# Business Requirements Implemented

- [x] 4 line forms: order_line, invoice_line, shipment_line, mr_line
- [x] 35 field rows (9+10+7+9)
- [x] 4 layout sections (single "Line Details" per form)
- [x] All section-field mappings complete
- [x] 7 sub-form config rows linking headers to line forms
- [x] relation_code matches child table FK columns (order_id, invoice_id, etc.)
- [x] Shared order_line form used by both purchase_order and sales_order
- [x] Parent FK fields included in line forms (auto-populated from sub-form context)

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V23__seed_line_forms_and_sub_forms.sql` | 4 line forms + 7 sub-form configs |

---

# Validation

## Build

PASS — `mvn clean compile` succeeded.

---

# Related Documents

- [TASK-032](../tasks/TASK-032-seed-line-forms-and-sub-forms.md)
- [PRD-003](../prd/PRD-003-erp-order-flow-forms.md)
