---
id: TEST-BUG-006

task: BUG-006

title: Backend @PreAuthorize uses SYSTEM_ADMIN but seeded role code is sys_admin

status: COMPLETED

qa_engineer: QA Engineer

test_date: 2026-07-13

test_scope:
  - Role-based access to all admin endpoints
  - Runtime form access for admin user
  - Regular user access restrictions

---

# Test Results

| Test | Status | Notes |
|------|--------|-------|
| admin → `GET /api/v1/metadata/tables` → 200 | ✅ PASS | Was 403 before fix |
| admin → `GET /api/v1/metadata/forms` → 200 | ✅ PASS | Was 403 before fix |
| admin → `GET /api/v1/runtime/tables` → 200 | ✅ PASS | 11 forms returned |
| admin → Form rules/validations/subforms endpoints | ✅ PASS | 200 on all |
| `@PreAuthorize` uses `hasAuthority('sys_admin')` | ✅ PASS | 8 controllers fixed |
| `RuntimeFormController` checks `"sys_admin"` | ✅ PASS | Not "SYSTEM_ADMIN" |
| `mvn test` — 36/36 | ✅ PASS | BUILD SUCCESS |
