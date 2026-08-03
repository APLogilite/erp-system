---
id: TASK-060
title: Create Document Sequence table and auto-numbering service
type: Feature
scope: both
status: READY_FOR_DEV
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 4
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-068
depends_on: []
blocks:
  - TASK-066
  - TASK-068
labels:
  - backend
  - database
  - prd-006
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Provide a configurable, tenant-scoped auto-numbering service so every business document (quotation, sales order, invoice, payment, shipment, return, credit memo) gets a professional sequential number (QT-0001, SO-0001, INV-0001, etc.) without manual entry.

---

# Description

Create the `sys_document_sequence` table via Flyway migration and implement a backend `DocumentSequenceService` with REST endpoints. The service must be thread-safe (use pessimistic locking on the sequence row) and tenant-scoped.

Table `sys_document_sequence`:
- `id` UUID PK, `sequence_name` string(100), `prefix` string(10), `next_value` integer, `padding` integer, `current_year` integer (nullable), plus system columns (`tenant_id`, `is_active`, `created_at`, `updated_at`, `created_by`, `updated_by`, `deleted_at`)

Seed default sequences: quotation (QT-), sales_order (SO-), sales_invoice (INV-), purchase_order (PO-), purchase_invoice (PIN-), payment (PAY-), shipment (SHP-), lead (LD-), opportunity (OPP-), return (RMA-), credit_memo (CM-). All with padding 4.

Backend service `DocumentSequenceService`:
- `String getNextNumber(String sequenceName)` — increments and returns formatted number (e.g., "QT-0001")
- `String peekNextNumber(String sequenceName)` — returns next number without incrementing
- Uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the repository query to prevent race conditions

REST endpoints (new controller `DocumentSequenceController` under a new `modules/common` or `platform` package):
- `POST /api/v1/sequences/{name}/next` → `{ "documentNumber": "QT-0001" }`
- `GET /api/v1/sequences/{name}/peek` → `{ "nextNumber": "QT-0002" }`

Also register table metadata in `sys_table` / `sys_column` so the form engine can manage sequences (the form itself is seeded in TASK-066).

---

# Acceptance Criteria

- [ ] `[SE]` Migration creates `sys_document_sequence` table and seeds 11 default sequences with correct prefixes
- [ ] `[SE]` `POST /api/v1/sequences/quotation/next` returns QT-0001 on first call, QT-0002 on second
- [ ] `[SE]` `GET /api/v1/sequences/quotation/peek` returns next number without incrementing
- [ ] `[SE]` Two concurrent requests receive different sequential numbers (no duplicates)
- [ ] `[SE]` Table and column metadata registered in sys_table/sys_column
- [ ] `[QA]` Calling next with an unknown sequence name returns 404 with a clear error message
- [ ] `[QA]` Sequences are tenant-isolated (tenant A's counter independent from tenant B)
- [ ] `[SE][QA]` Backend starts cleanly with migration applied (no Flyway errors in /tmp/erp-backend.log)

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Follow existing migration style from V5 (idempotent inserts with `WHERE NOT EXISTS`)
- Pessimistic locking via Spring Data JPA `@Lock` annotation on repository method
- Keep controller thin; logic in service
- Reference PRD-006 FR-001

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_sequence_table.sql`
- `backend/src/main/java/com/erp/modules/common/entity/DocumentSequence.java` (new)
- `backend/src/main/java/com/erp/modules/common/repository/DocumentSequenceRepository.java` (new)
- `backend/src/main/java/com/erp/modules/common/service/DocumentSequenceService.java` (new)
- `backend/src/main/java/com/erp/modules/common/controller/DocumentSequenceController.java` (new)
- `backend/src/main/java/com/erp/modules/common/dto/DocumentNumberResponse.java` (new)

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-006 approved by user

---

# Related Documents

- PRD-006 — Sales Quotation & Price Management (FR-001)
