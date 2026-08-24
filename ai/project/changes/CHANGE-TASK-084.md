---
document: CHANGE_REPORT
task_id: TASK-084
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-084

## Task

Implement Opportunity pipeline workflow, quotation conversion, and commission (PRD-008).

## Files Changed

- `backend/src/main/java/com/erp/modules/crmflow/service/OpportunityService.java`
- `backend/src/main/java/com/erp/modules/crmflow/controller/OpportunityController.java`
- `backend/src/main/java/com/erp/modules/crmflow/dto/OpportunityTransitionRequest.java`
- `backend/src/main/java/com/erp/modules/crmflow/dto/OpportunityTransitionResponse.java`
- `backend/src/main/java/com/erp/modules/crmflow/dto/CreateQuotationResponse.java`

## Summary

Implemented opportunity stage transitions (qualification → proposal → negotiation → closed_won/lost) with validation and 400 responses for invalid moves. Added `POST /api/v1/opportunities/{id}/create-quotation` to generate a draft `tx_quotation` pre-filled from the opportunity. On `closed_won`, a pending `tx_commission` is auto-created using the configurable default commission percent (`crm.default-commission-percent`, default 5.0%).

## Endpoints

- `POST /api/v1/opportunities/{id}/transition`
- `POST /api/v1/opportunities/{id}/create-quotation`
