---
id: CHANGE-BUG-002

task_id: BUG-002

parent_prd: PRD-001

branch: bugfix/BUG-002

type: Bug

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: ~1 hour

related_commits:
  - (pending merge)

related_files:
  - backend/src/main/java/com/erp/config/ApiVersionConfig.java
  - ai/project/tasks/BUG-002-api-base-path-mismatch.md
  - ai/project/changes/CHANGE-BUG-002.md

review_required: true

test_required: true

---

# Summary

Fixed `ApiVersionConfig.API_BASE` from `"/api"` to `"/api/v1"`. This resolves 500 errors on all metadata designer pages (Table Designer, Form Designer) and runtime form pages. 14 backend controllers were mapped to `/api/...` but the frontend consistently sends requests to `/api/v1/...` — the request never matched any controller and fell through to the static resource handler.

---

# Business Requirements Implemented

- Metadata designer pages (Table Designer, Form Designer) load without 500 errors
- Runtime form endpoints respond correctly
- All 14 affected controllers now match their expected request paths

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/config/ApiVersionConfig.java` | Changed `API_BASE = "/api"` to `"/api/v1"` (line 9) |

---

# Validation

## Build
**PASS** — `mvn compile` completed without errors

## Endpoint Tests (with auth token)

| Endpoint | Before | After | Status |
|----------|--------|-------|--------|
| `GET /api/v1/metadata/tables` | 500 NoResourceFoundException | 403 (requires sys_admin role) | ✅ Fixed |
| `GET /api/v1/metadata/forms` | 500 NoResourceFoundException | 403 (requires sys_admin role) | ✅ Fixed |
| `GET /api/v1/runtime/forms` | 500 NoResourceFoundException | 200 with `data: []` | ✅ Fixed |

---

# Breaking Changes

None. The fix only changes the path prefix used in `@RequestMapping` annotations. No API contract changed — endpoints are at the same `/api/v1/...` URLs they were always supposed to be at.

---

# Known Issues

The `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` on `TableDesignerController` and `FormDesignerController` returns 403 for the `admin` user (role: `sys_admin`). This is a pre-existing authorization configuration concern, not related to this fix. The endpoints now at least match the correct controller — the role check is separate.

# Related Documents

- [BUG-002](../tasks/BUG-002-api-base-path-mismatch.md)
