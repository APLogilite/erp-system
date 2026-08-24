---
document: CHANGE_REPORT
task_id: TASK-086
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-086

## Task

Implement CRM Dashboard API and deprecate hardcoded crm/ module (PRD-008).

## Files Changed

- `backend/src/main/java/com/erp/modules/crmflow/service/CrmDashboardService.java`
- `backend/src/main/java/com/erp/modules/crmflow/controller/CrmDashboardController.java`
- `backend/src/main/java/com/erp/modules/crmflow/dto/CrmDashboardResponse.java`
- `backend/src/main/resources/db/migration/V31__drop_legacy_crm_tables.sql`
- `backend/src/main/resources/db/migration/V32__add_territory_to_business_partner.sql`
- `frontend/src/routes/CrmDashboard.tsx`
- `frontend/src/routes/AppRoutes.tsx`
- Deleted: `backend/src/main/java/com/erp/modules/crm/**`

## Summary

Implemented `GET /api/v1/crm/dashboard` returning pipeline stage counts/revenue, lead-to-opportunity and opportunity-to-order conversion rates, and per-salesperson team performance. Built a custom React `CrmDashboard` page with pipeline cards, conversion rates, and a team performance table. Added the `/app/crm-dashboard` route. Removed the legacy `com.erp.modules.crm` Java module and added a migration to drop the old `leads`/`opportunities` tables. Also added `territory_id` to `md_business_partner` (V32).

## Validation

- `mvn clean compile`: BUILD SUCCESS
- `mvn test`: 36 tests pass
- `pnpm lint`, `pnpm typecheck`, `pnpm build`: all pass
