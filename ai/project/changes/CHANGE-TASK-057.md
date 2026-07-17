---
id: CHANGE-TASK-057

task_id: TASK-057

parent_prd: PRD-005

branch: feature/TASK-057

type: Refactor

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 20 minutes (estimated)

related_commits:
  - refactor(TASK-057): remove stale authz endpoint section from endpoints.ts

related_files:
  - MODIFIED frontend/src/core/api/endpoints.ts

review_required: true

test_required: true

---

# Summary

Audited all sections in `endpoints.ts` against actual usage. Verified: `auth` (used in authService.ts), `context` (used in context pages), `identity` (used in admin pages), `metadata` (used in metadataService/useTables/useColumns). The `authz` section was confirmed unused (zero references) and was removed. The `customers` and `users` sections were already removed in TASK-056.

---

# Scope Verification

- [x] Frontend
- [ ] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- TASK-057: Audit and Remove Stale Frontend API Endpoints
  - Verified all sections against actual usage
  - `auth`, `context`, `identity`, `metadata` — all actively used
  - `authz` — removed (no references anywhere in frontend)

---

# Files Modified

| File | Summary |
|------|---------|
| `frontend/src/core/api/endpoints.ts` | Removed `authz` section (permissions/checkPermission/invalidate) |

---

# Validation

## Build

PASS — Frontend `tsc --noEmit` succeeds
