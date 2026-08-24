---
document: CHANGE_REPORT
task_id: TASK-080
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-080

## Task

Create Opportunity table with metadata (PRD-008).

## Files Changed

- `backend/src/main/resources/db/migration/V28__create_opportunity_table.sql`
- `ai/project/schema/crm/tx_opportunity.sql`

## Summary

Created the metadata-driven `tx_opportunity` table to replace the legacy hardcoded opportunities table. Captures opportunity number, name, customer, source lead, pipeline stage, probability, expected revenue/close date, salesperson/territory, notes, and conversion links to quotation/order. Registered in `sys_table`/`sys_column` with enum metadata. Seeded one sample opportunity.

## Schema

- `tx_opportunity` with FKs to `md_business_partner`, `tx_lead`, `identity_users`, `md_sales_territory`
- Unique constraint on `(opportunity_number, tenant_id)`
