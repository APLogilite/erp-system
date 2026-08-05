---
id: CHANGE-TASK-060
task_id: TASK-060
parent_prd: PRD-006
branch: feature/TASK-060
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-05
completed: 2026-08-05
duration: 2 hours
related_commits:
  - feat(TASK-060): Document Sequence table + auto-numbering service
related_files:
  - backend/src/main/resources/db/migration/V10__create_sequence_table.sql
  - backend/src/main/java/com/erp/modules/common/entity/DocumentSequence.java
  - backend/src/main/java/com/erp/modules/common/repository/DocumentSequenceRepository.java
  - backend/src/main/java/com/erp/modules/common/service/DocumentSequenceService.java
  - backend/src/main/java/com/erp/modules/common/controller/DocumentSequenceController.java
  - backend/src/main/java/com/erp/modules/common/dto/DocumentNumberResponse.java
review_required: true
test_required: true
---

# Summary

Created the `sys_document_sequence` table and a tenant-scoped, thread-safe auto-numbering service. 11 default sequences seeded (quotation, sales_order, sales_invoice, purchase_order, purchase_invoice, payment, shipment, lead, opportunity, return, credit_memo).

# Validation

- Flyway V10 applied, 10 migrations validated
- `POST /sequences/quotation/next` → QT-0001, QT-0002 (increments correctly)
- `GET /sequences/quotation/peek` → QT-0003 (no increment)
- Unknown sequence → 404 NOT_FOUND
- `mvn test` 36/36 BUILD SUCCESS
- DB: 11 sequences seeded, sys_table/sys_column metadata registered
