---
id: CHANGE-ENH-004
task_id: ENH-004
parent_prd: PRD-005
branch: enhancement/ENH-004
type: Enhancement
status: IMPLEMENTED
developer: Software Engineer
started: 2026-07-29
completed: 2026-07-30
duration: 2 hours
related_commits:
  - feat(ENH-004): backend generation endpoint + frontend cache auto-invalidation
related_files:
  - backend/src/main/java/com/erp/core/runtime/controller/RuntimeMetaController.java
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/core/runtime/hooks/useDefinitionGeneration.ts
  - frontend/src/components/layouts/AppLayout/AppLayout.tsx
review_required: true
test_required: true
---

# Summary

The frontend caches window definitions in React Query (staleTime 5 min, in-memory). Every `start-all.sh --setup` reseeds the database with `gen_random_uuid()`, changing ALL metadata UUIDs. Without a generation-change signal, open browser tabs serve stale definitions with ghost UUIDs, producing "Tab not found" errors and missing UI elements — observed during BUG-013 manual QA (7 of 8 scenarios initially failed; only a hard refresh fixed it).

**Fix:** Generation-based cache auto-invalidation:
1. **Backend** — new `GET /api/v1/runtime/meta/generation` returns a data-generation marker derived from `flyway_schema_history` (max installed_rank + max installed_on). This changes on every reseed or new migration, but is stable across plain restarts. Falls back to application startup timestamp if the Flyway history table doesn't exist.
2. **Frontend** — `useDefinitionGeneration()` hook polls the generation every 30 s and on window focus. On generation change, all UUID-bound query caches (`window-definition`, `window-records`, `window-record`, `runtime-menu`, `dynamic-lookup`) are invalidated automatically. The last-seen generation is persisted in localStorage so a freshly opened tab also detects changes. Hook wired in `AppLayout` (mounted on all authenticated routes).

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [x] Configuration

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../controller/RuntimeMetaController.java` | **New** — `/runtime/meta/generation` endpoint using `JdbcTemplate` to query `flyway_schema_history` |
| `frontend/.../api/runtimeApi.ts` | Added `fetchDefinitionGeneration()` |
| `frontend/.../hooks/useDefinitionGeneration.ts` | **New** — polling hook with localStorage persistence and cache invalidation |
| `frontend/.../layouts/AppLayout/AppLayout.tsx` | Wired `useDefinitionGeneration()` in the authenticated layout shell |

---

# Database Changes

None. The endpoint reads `flyway_schema_history` (read-only) and `information_schema.tables` (via exception handling).

---

# API Changes

## Added

`GET /api/v1/runtime/meta/generation`

Response:
```json
{
  "success": true,
  "data": { "generation": "flyway-8-1785257719" },
  "message": "Generation retrieved."
}
```

The marker format is `flyway-{rank}-{installedOnEpoch}` or `start-{startupEpoch}` if Flyway history is absent.

---

# Validation

## Build
PASS — Backend `mvn clean compile` succeeds. Frontend `tsc --noEmit` succeeds.

## Backend Tests
PASS — 36/36, BUILD SUCCESS.

## Runtime Verification (2026-07-30)
- Generation endpoint responds with `success: true` and a stable generation string: `flyway-8-1785257719`.
- Same value on repeated calls. Will change after `start-all.sh --setup`.
- Frontend hook compiles and wires into AppLayout without errors.

---

# Breaking Changes

None. Additive endpoint; frontend hook is wired without user-visible changes.

---

# Known Issues

None.

---

# QA Handoff

**Suggested test focus:**
1. **Generation endpoint** — verify it returns `success: true` and a stable generation value; value changes after `start-all.sh --setup`
2. **Cache invalidation** — open the app in a browser, then run `start-all.sh --setup`; within ~30 s the open tab should refresh definitions/records automatically; child tabs render with fresh UUIDs (no ghost errors)
3. **Stable period** — within a stable generation, verify normal cache behavior (no frequent network requests for definitions)
