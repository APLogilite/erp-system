---
document: CHANGE_REPORT
task_id: TASK-083
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-083

## Task

Implement Lead to Opportunity conversion (PRD-008).

## Files Changed

- `backend/src/main/java/com/erp/modules/crmflow/service/LeadService.java`
- `backend/src/main/java/com/erp/modules/crmflow/controller/LeadController.java`
- `backend/src/main/java/com/erp/modules/crmflow/dto/LeadConvertResponse.java`

## Summary

Added `LeadService.convertToOpportunity` and `POST /api/v1/leads/{id}/convert`. The service validates that the lead is qualified and not already converted. If the lead has no linked customer, it creates a new `md_business_partner` from the lead data. It then generates an opportunity number, creates a `tx_opportunity` linked to the lead and partner, and updates the lead status to `converted`. All operations run in a single Spring transaction.

## Endpoint

- `POST /api/v1/leads/{id}/convert`
- Response: `{ partnerId, opportunityId, opportunityNumber }`
