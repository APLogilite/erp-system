---
id: CHANGE-TASK-067
task_id: TASK-067
parent_prd: PRD-006
type: Feature
status: IMPLEMENTED
related_files:
  - backend/.../common/service/QuotationService.java
  - backend/.../common/controller/QuotationController.java
  - backend/.../common/dto/QuotationTotalsResponse.java
  - backend/.../common/dto/LineCalcRequest.java
  - backend/.../common/dto/LineCalcResponse.java
---

# Summary
POST /api/v1/quotations/{id}/calculate — resolves price+discount per line, persists totals.
POST /api/v1/quotations/calculate-line — single-line preview.

# Validation
- mvn compile OK
