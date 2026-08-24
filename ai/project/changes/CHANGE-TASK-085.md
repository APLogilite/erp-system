---
document: CHANGE_REPORT
task_id: TASK-085
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-085

## Task

Seed CRM forms (lead, opportunity, territory, commission) (PRD-008).

## Files Changed

- `backend/src/main/resources/db/migration/V30__seed_crm_forms.sql`

## Summary

Seeded metadata-driven windows/tabs/fields for Leads, Opportunities, Commissions, and Sales Territories. Configured read-only flags for auto-generated number and score fields. Granted `sys_admin` window access and added menu entries: Leads and Opportunities under a new CRM group, Commissions under Sales, and Sales Territories under Master Data. Also added a CRM Dashboard menu placeholder.
