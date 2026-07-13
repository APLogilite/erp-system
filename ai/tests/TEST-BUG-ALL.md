---
id: TEST-BUG-ALL

title: QA verification — all 6 bugs + 1 enhancement

status: COMPLETED

qa_engineer: QA Engineer

test_date: 2026-07-13

environment:
  os: Linux
  java: OpenJDK 17
  backend: Spring Boot 3.3.4 (port 8081)
  frontend: React 18 + Vite (port 5173)
  database: PostgreSQL (dev) + H2 (test)

---

# Test Results

## Full Regression Suite

| Test Suite | Tests | Result |
|-----------|-------|--------|
| DatabaseConnectionTest | 3/3 | ✅ PASS |
| PermissionCacheTest | 6/6 | ✅ PASS |
| PermissionEvaluatorTest | 9/9 | ✅ PASS |
| PasswordServiceTest | 13/13 | ✅ PASS |
| JwtProviderTest | 5/5 | ✅ PASS |
| **Total** | **36/36** | **✅ BUILD SUCCESS** |

## Bug Verification

### BUG-001 — DatabaseConnectionTest failures

| Test | Result |
|------|--------|
| `mvn test` → BUILD SUCCESS | ✅ PASS |
| DatabaseConnectionTest — 3/3 pass | ✅ PASS |
| Test uses JDBC DatabaseMetaData (portable) | ✅ PASS |
| `spring.flyway.enabled=false` in test props | ✅ PASS |

### BUG-002 — API_BASE path mismatch

| Test | Result |
|------|--------|
| `GET /api/v1/metadata/tables` → 200 | ✅ PASS |
| `GET /api/v1/metadata/forms` → 200 | ✅ PASS |
| `GET /api/v1/runtime/forms` → 200 | ✅ PASS |
| No more 500 errors on any endpoint | ✅ PASS |
| `GET /api/...` (old path) → no longer used | ✅ PASS |

### BUG-003 — Sidebar overlap + responsiveness

| Test | Result |
|------|--------|
| Sidebar visible left (280px), content clears it | ✅ PASS |
| No overlap on desktop (>1200px) | ✅ PASS |
| Mobile sidebar hidden, hamburger works | ✅ PASS |
| All pages use responsive MUI Grid patterns | ✅ PASS |
| ContentArea minHeight handles variable header | ✅ PASS |
| Custom scrollbar styling present | ✅ PASS |

### BUG-004 — Search bar

| Test | Result |
|------|--------|
| Search icon visible in header | ✅ PASS |
| Clicking icon opens search dialog | ✅ PASS |
| Ctrl+K opens search dialog | ✅ PASS |
| Loading spinner shown while fetching | ✅ PASS |
| Lists accessible forms on open | ✅ PASS |
| Typing filters form list | ✅ PASS |
| Selecting form navigates to runtime page | ✅ PASS |

### BUG-005 — FormNavigationMenu

| Test | Result |
|------|--------|
| Only form labels shown (no model/table names) | ✅ PASS |
| Sub-forms filtered out (line items not in sidebar) | ✅ PASS |
| Clicking form navigates to correct URL | ✅ PASS |

### BUG-006 — Role authority

| Test | Result |
|------|--------|
| admin user → `GET /metadata/tables` → 200 | ✅ PASS |
| admin user → `GET /metadata/forms` → 200 | ✅ PASS |
| admin user → 11 forms from runtime API | ✅ PASS |
| Regular user → still blocked from admin endpoints | ✅ PASS |
| `jane.smith` (tnt_admin) → has appropriate access | ✅ PASS |

### ENH-003 — RuntimePage API integration

| Test | Result |
|------|--------|
| RuntimePage reads `?form=xxx` from URL | ✅ PASS |
| Fetches form definition from API | ✅ PASS |
| Maps to RuntimeMetadataBundle (typed) | ✅ PASS |
| No `as any` casts | ✅ PASS |
| Loading/error/empty states work | ✅ PASS |

---

# Summary

| Category | Count |
|----------|-------|
| Tests executed | 36 backend + API verification |
| Tests passed | 36/36 ✅ |
| Bugs found | 0 |
| APIs verified | 6 endpoints all 200 ✅ |
| Frontend pages verified | Dashboard, Table Designer, Form Designer, Runtime, Admin pages ✅ |

---

# Related Documents

- [BUG-001](../tasks/BUG-001-database-connection-test-failures.md)
- [BUG-002](../tasks/BUG-002-api-base-path-mismatch.md)
- [BUG-003](../tasks/BUG-003-sidebar-content-overlap.md)
- [BUG-004](../tasks/BUG-004-search-bar-not-loading.md)
- [BUG-005](../tasks/BUG-005-sidebar-form-navigation-issues.md)
- [BUG-006](../tasks/BUG-006-role-authority-mismatch.md)
- [ENH-003](../tasks/ENH-003-runtime-page-api-integration.md)
