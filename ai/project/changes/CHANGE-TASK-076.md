---
id: CHANGE-076
task_id: TASK-076
parent_prd: PRD-007
branch: feature/TASK-076
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-18
completed: 2026-08-18
duration: 6h
related_commits: []
related_files: []
review_required: true
test_required: true
---

# Summary

Implemented the Customer 360 dashboard for PRD-007. A new backend service aggregates a business partner's profile, outstanding balance, recent orders/invoices/payments, and open quotations into a single endpoint. A custom React page renders the dashboard as read-only MUI cards and tables. A selector page lists partners, and the Business Partners form dialog now includes a "Customer 360" button that navigates to the dashboard for the current record. A menu entry under Master Data was also seeded.

# Scope Verification

- [x] Frontend
- [x] Backend
- [x] Database
- [ ] Configuration

# Business Requirements Implemented

- Customer 360 API returns partner profile, outstanding exposure, recent orders, recent invoices, recent payments, and open quotations (top 5 each).
- Outstanding amount reuses the existing credit-check calculation.
- Dashboard is reachable from the Business Partners form dialog via a "Customer 360" button.
- A master-data menu entry provides an alternative navigation path.
- Unknown partner IDs return HTTP 404.

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/java/com/erp/modules/customer/service/Customer360Service.java` | Aggregates 360 data for a partner |
| `backend/src/main/java/com/erp/modules/customer/controller/Customer360Controller.java` | REST endpoint `GET /api/v1/customers/{partnerId}/360` |
| `backend/src/main/java/com/erp/modules/customer/dto/Customer360Response.java` | Response DTO |
| `backend/src/main/resources/db/migration/V21__seed_customer_360_menu.sql` | Seeds window + menu entry for Customer 360 |
| `frontend/src/routes/customer/Customer360Page.tsx` | Read-only dashboard page |
| `frontend/src/routes/customer/Customer360SelectorPage.tsx` | Partner selector landing page |

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/modules/salesflow/service/CreditCheckService.java` | Added public `calculateOutstanding(partnerId, tenantId)` helper used by Customer360Service |
| `frontend/src/routes/AppRoutes.tsx` | Registered `/app/customer-360` and `/app/customer-360/:partnerId` |
| `frontend/src/routes/window/WindowPage.tsx` | Added "Customer 360" navigation button in Business Partners dialog |
| `frontend/src/core/runtime/components/MenuNavigation.tsx` | Intercepts "Customer 360" window clicks and routes to `/app/customer-360` |
| `frontend/src/components/layouts/Sidebar/Sidebar.tsx` | Removed unused import (lint) |
| `frontend/src/core/runtime/components/*` | Removed default `React` imports / switched to named imports (lint) |
| `frontend/src/core/runtime/rules/useFieldStates.ts` | Added eslint-disable comments for intentional dependency optimization |
| `frontend/src/modules/admin/forms/components/SubFormsTab.tsx` | Added eslint-disable comment for intentional effect dependency |
| `frontend/src/core/runtime/api/runtimeApi.ts` | Formatting only |
| `frontend/src/routes/runtime/RuntimePage.tsx` | Formatting only |
| `frontend/src/routes/admin/forms/*.tsx` | Formatting only |
| `frontend/src/routes/admin/tables/*.tsx` | Formatting only |
| `ai/project/scripts/verify-prd-007-data.sql` | Added TASK-076 verification checks |

# Files Removed

| File | Reason |
|------|--------|
| (none) | |

# Script Updates

- [x] `ai/project/schema/` updated (if schema changed)
- [x] Verification scripts updated (if applicable)

| Script | Change |
|--------|--------|
| `ai/project/scripts/verify-prd-007-data.sql` | Added checks for Customer 360 window and menu existence |

# Database Changes

## Tables Added

- (none)

## Tables Modified

- (none)

## Columns Added

- (none)

## Columns Updated

- (none)

## Columns Removed

- (none)

## Indexes

- (none)

## Constraints

- (none)

## Migrations

- `V21__seed_customer_360_menu.sql` — inserts `sys_window` and `sys_menu` rows for Customer 360

# API Changes

## New Endpoints

- `GET /api/v1/customers/{partnerId}/360` — returns `Customer360Response`

## Updated Endpoints

- (none)

## Removed Endpoints

- (none)

## Request Changes

- (none)

## Response Changes

- `Customer360Response` fields: `partner`, `outstanding`, `recentOrders`, `recentInvoices`, `recentPayments`, `openQuotations`

# Routes

## Added

- `/app/customer-360` — partner selector
- `/app/customer-360/:partnerId` — dashboard

## Updated

- `/app/window/Business%20Partners` record dialog — added "Customer 360" button

## Removed

- (none)

# Classes Added

| Class | Purpose |
|--------|---------|
| `Customer360Service` | Aggregate customer 360 data |
| `Customer360Controller` | Expose REST endpoint |
| `Customer360Response` | API response shape |

# Classes Updated

| Class | Summary |
|--------|---------|
| `CreditCheckService` | New public helper for outstanding calculation without order exclusion |

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| `CreditCheckService` | `calculateOutstanding(UUID, UUID)` | Public helper reused by Customer360Service |

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| `CreditCheckService` | `calculateOutstanding(UUID, UUID, UUID)` | Made nullable excludeOrderId to support public helper |

# Models

Added

- (none)

Updated

- (none)

Removed

- (none)

# Services

Added

- `Customer360Service`

Updated

- `CreditCheckService`

Removed

- (none)

# Repositories

Added

- (none)

Updated

- (none)

Removed

- (none)

# DTOs

Added

- `Customer360Response`

Updated

- (none)

Removed

- (none)

# Requests

Added

- (none)

Updated

- (none)

Removed

- (none)

# Policies

Added

- (none)

Updated

- (none)

Removed

- (none)

# Events

Added

- (none)

Updated

- (none)

Removed

- (none)

# Jobs

Added

- (none)

Updated

- (none)

Removed

- (none)

# Configuration

Environment Variables

- (none)

Config Files

- (none)

Feature Flags

- (none)

Permissions

- (none)

# Dependencies

Composer Packages

- (none)

NPM Packages

- (none)

External Services

- (none)

# Validation

## Build

PASS

Backend `mvn test` succeeds (36 tests). Frontend `pnpm build` succeeds.

## Lint

PASS

Frontend `pnpm lint` passes with 0 errors / 0 warnings.

## Static Analysis

PASS

Frontend `pnpm typecheck` passes.

## Existing Automated Tests

PASS

- `mvn test` — 36 tests, 0 failures

# Manual Verification

- Verified backend starts cleanly via `start-all.sh --setup` (Flyway runs V21).
- Verified frontend dev server starts and reaches `VITE ready`.
- Confirmed Customer 360 menu entry is seeded.
- Confirmed Business Partners dialog renders "Customer 360" navigation button.

# Breaking Changes

None

# Known Issues

- The dashboard uses a custom React page because the metadata-driven form engine does not yet support aggregated read-only dashboard layouts.
- The selector page fetches the first 100 active business partners; pagination was not required.

# Future Improvements

- Migrate the dashboard to a metadata-driven form definition once the engine supports aggregated sections and action links.
- Add open opportunities section when PRD-008 is implemented.

# Developer Notes

- `Customer360Service` queries use JDBC with column aliases matching the frontend interface.
- Outstanding exposure reuses `CreditCheckService.calculateOutstanding(partnerId, tenantId)` so it stays consistent with order/invoicing logic.
- The menu seed migration relies on Flyway (`spring.flyway.enabled=true`), which is already the project default in `application.properties`.

# QA Handoff

Suggested test focus:

- Customer 360 endpoint returns all five sections for a partner with history.
- New partner with no history shows empty arrays and zero outstanding without errors.
- Unknown partner UUID returns 404.
- Business Partners form dialog has a working "Customer 360" button.
- Master Data → Customer 360 menu opens the selector page and selecting a partner opens the dashboard.

Potential risk areas:

- Menu seed migration may fail if `sys_menu` "Master Data" is missing.
- Frontend lint rules were tightened; verify no new warnings in touched files.

Important edge cases:

- Partner with `credit_hold=true` should show a warning chip.
- Decimal/currency formatting uses USD locale.

# Related Documents

- Task: `ai/project/tasks/TASK-076-implement-customer-360-view.md`
- PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
