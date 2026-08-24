---
id: CHANGE-077
task_id: TASK-077
parent_prd: PRD-007
branch: feature/TASK-077
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-18
completed: 2026-08-18
duration: 5h
related_commits: []
related_files: []
review_required: true
test_required: true
---

# Summary

Updated the Sales Order form to support the PRD-007 order workflow. A new Flyway migration marks computed header totals and status as read-only and ensures the payment-term field is visible. The frontend window renderer now renders workflow transition buttons based on the backend's `allowed-transitions` response, surfaces credit-check failures in an alert, shows a credit-status badge next to the customer field, and persists child-tab line changes before triggering header total recalculation.

# Scope Verification

- [x] Frontend
- [ ] Backend
- [x] Database
- [ ] Configuration

# Business Requirements Implemented

- Sales Order header totals (subtotal, tax, discount, grand total) and status are read-only.
- Payment term dropdown is present on the Sales Order header.
- Workflow action buttons (Confirm, Mark Delivered, Create Invoice, Cancel, etc.) render only when allowed by the backend.
- Clicking a transition calls the workflow API; 400/credit errors are shown to the user.
- Order line changes in the child tab are persisted and trigger header total recalculation.
- A credit-status badge appears next to the customer field.

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V22__update_sales_order_form.sql` | Updates Sales Orders window metadata for workflow |
| `ai/project/changes/CHANGE-TASK-077.md` | This change report |

# Files Modified

| File | Summary |
|------|---------|
| `frontend/src/core/runtime/api/runtimeApi.ts` | Added workflow/transition/calculate/credit-check API functions and types |
| `frontend/src/routes/window/WindowPage.tsx` | Added workflow action bar, credit badge, child-row persistence + recalculation |
| `ai/project/scripts/verify-prd-007-data.sql` | Added TASK-077 metadata verification checks |
| `ai/project/tasks/TASK-077-update-sales-order-form-workflow.md` | Marked acceptance criteria and set actual hours |

# Files Removed

| File | Reason |
|------|--------|
| (none) | |

# Script Updates

- [x] `ai/project/schema/` updated (if schema changed)
- [x] Verification scripts updated (if applicable)

| Script | Change |
|--------|--------|
| `ai/project/scripts/verify-prd-007-data.sql` | Added checks that Sales Order totals are read-only/displayed and payment_term_id field exists |

# Database Changes

## Tables Added

- (none)

## Tables Modified

- (none)

## Columns Added

- `tx_order.discount_amount` registered in `sys_column` (column already existed in DB)

## Columns Updated

- (none)

## Columns Removed

- (none)

## Indexes

- (none)

## Constraints

- (none)

## Migrations

- `V22__update_sales_order_form.sql` — updates `sys_window_field` for Sales Orders totals/status; adds `discount_amount` and `payment_term_id` fields

# API Changes

## New Endpoints

- (none — reused TASK-073/074/075 endpoints)

## Updated Endpoints

- (none)

## Removed Endpoints

- (none)

## Request Changes

- (none)

## Response Changes

- (none)

# Routes

## Added

- (none)

## Updated

- `/app/window/Sales%20Orders` record dialog — workflow action bar, credit badge, child-tab recalculation

## Removed

- (none)

# Classes Added

| Class | Purpose |
|--------|---------|
| (none) |

# Classes Updated

| Class | Summary |
|--------|---------|
| (none) |

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| runtimeApi | `fetchAllowedTransitions` | Fetch valid order transitions |
| runtimeApi | `transitionOrder` | Execute a workflow transition |
| runtimeApi | `calculateOrder` | Recalculate order header totals |
| runtimeApi | `checkOrderCredit` | Evaluate customer credit for order |

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| WindowPage/RecordDialog | — | Added workflow and credit UI logic |
| WindowPage/ChildTabGrid | — | Propagates edited rows to parent for persistence |

# Models

Added

- (none)

Updated

- (none)

Removed

- (none)

# Services

Added

- (none)

Updated

- (none)

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

- (none — types added in frontend only)

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

- Verified `start-all.sh --setup` starts backend cleanly; V22 migration applies without errors.
- Verified frontend dev server starts and reaches `VITE ready`.
- Verified Sales Orders fields are read-only/displayed via DB query.

# Breaking Changes

None

# Known Issues

- Child-tab inline editing persists all rows when "Done Editing" is clicked; individual cell changes are batched.
- The generic window renderer now contains Sales Order-specific logic; a future refactor should extract a plugin/extension system.

# Future Improvements

- Extract Sales Order workflow UI into a registered form-level plugin/extension.
- Add a dedicated "Add Line" / "Save Lines" flow with per-row validation.

# Developer Notes

- Workflow transition buttons use the `allowed-transitions` endpoint so the UI only offers valid next states.
- Credit failures return 400 BAD_REQUEST from the transition endpoint; the UI shows the server message.
- Child row persistence reuses `createWindowRecord`/`updateWindowRecord`/`deleteWindowRecord` with `tabId`.
- After persisting lines, `calculateOrder` is called and the header record is refreshed.

# QA Handoff

Suggested test focus:

- Open a draft Sales Order; confirm totals/status fields are read-only.
- Confirm workflow buttons match allowed transitions.
- Click Confirm on an order that exceeds credit limit; verify error alert appears.
- Edit order lines via "Quick Update" → "Done Editing"; verify header totals refresh.
- Verify credit-status badge updates next to the customer field.

Potential risk areas:

- Child-tab persistence may conflict with drill-down navigation if a child record is also opened as a dialog.
- Window field metadata update affects only Sales Orders; Purchase Orders unchanged.

Important edge cases:

- New Sales Order (no recordId) should not show workflow buttons or credit badge.
- Order with no lines should calculate to zero totals.

# Related Documents

- Task: `ai/project/tasks/TASK-077-update-sales-order-form-workflow.md`
- PRD: `ai/project/prd/PRD-007-sales-order-workflow-customer-management.md`
