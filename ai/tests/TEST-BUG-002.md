---
id: TEST-BUG-002

task: BUG-002

title: ApiVersionConfig.API_BASE uses /api instead of /api/v1

status: COMPLETED

qa_engineer: QA Engineer

test_date: 2026-07-13

test_scope:
  - API endpoint routing for all metadata/runtime endpoints
  - Regression: 36 backend unit tests

---

# Test Results

| Test | Status | Notes |
|------|--------|-------|
| `GET /api/v1/metadata/tables` | ✅ PASS | HTTP 200, returns table list |
| `GET /api/v1/metadata/forms` | ✅ PASS | HTTP 200, returns form list |
| `GET /api/v1/runtime/forms` | ✅ PASS | HTTP 200, returns 11 forms |
| `GET /api/v1/runtime/forms/{code}/definition` | ✅ PASS | HTTP 200 |
| No more 500 on any endpoint | ✅ PASS | All endpoints return proper auth errors or 200 |
| `mvn test` — 36/36 | ✅ PASS | BUILD SUCCESS |
