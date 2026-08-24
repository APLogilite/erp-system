---
document: CHANGE_REPORT
task_id: TASK-079
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-079

## Task

Create Lead table with metadata (PRD-008).

## Files Changed

- `backend/src/main/resources/db/migration/V27__create_lead_table.sql`
- `ai/project/schema/crm/tx_lead.sql`

## Summary

Created the metadata-driven `tx_lead` table to replace the legacy hardcoded leads table. The table captures lead number, company/contact info, source, qualification status, auto-score, salesperson/territory assignment, expected revenue, notes, and conversion links. Registered in `sys_table`/`sys_column` with enum metadata. Seeded two sample leads.

## Schema

- `tx_lead` with FKs to `identity_users`, `md_sales_territory`, `md_business_partner`
- Unique constraint on `(lead_number, tenant_id)`

## Auto-numbering / Scoring

Handled by `CrmRecordLifecycleHook` (TASK-082): lead number assigned from `lead` sequence and score computed on create/update.
