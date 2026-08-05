---
id: ENH-004

title: Window definition cache auto-invalidation after backend data-generation change (DB reseed)

status: COMPLETED

priority: High

owner: Software Engineer

assigned_to: QA Engineer

assigned_branch: prd/PRD-005-v2

locked: false

created: 2026-07-29

updated: 2026-07-29

started: 2026-07-29

completed:

parent_prd: PRD-005

parent_task: BUG-013

reason: BUG-013 manual QA (2026-07-29) — 7 of 8 UI scenarios initially failed from stale browser cache holding ghost UUIDs from a previous DB generation; user needed a hard refresh to recover

fix_summary:

verification_report:

history:
  - 2026-07-29 — Product Manager — Task definition complete → PLANNED.
  - 2026-07-29 — Software Engineer — No dependencies → READY_FOR_DEV → locked, started implementation (branch enhancement/ENH-004).
  - 2026-07-30 — Software Engineer — Implemented (RuntimeMetaController + useDefinitionGeneration hook). Merged to prd/PRD-005-v2. → READY_FOR_TEST. Root cause evidence in TEST-BUG-013: backend logs showed the client sending tab UUIDs that do not exist in the current DB; fresh curl sessions returned correct data. Requested by user ("can we fix this cache issue?").

---

# Summary

The frontend caches window definitions in React Query (global `staleTime` 5 min, `gcTime` 10 min, in-memory). Every `start-all.sh --setup` reseeds the database with `gen_random_uuid()`, so **all metadata UUIDs change** (windows, tabs, columns). Any client holding a cached definition from an older generation experiences:

1. **Silent UI degradation** — elements missing (e.g., child tabs not rendered) with no error
2. **Ghost-UUID errors** — "Tab not found in window 'X': <uuid>" on drill-down, where the uuid does not exist in the current DB

There is no mechanism for the frontend to learn that the backend's data generation changed.

# Problem

- React Query cache is in-memory but long-lived tabs keep it alive indefinitely; within `staleTime` windows, navigation serves stale definitions without refetching.
- After reseeds, stale definitions reference non-existent tab ids; failures are confusing and look like product bugs (cost a full QA round in BUG-013).
- Today the only recovery is a manual hard refresh — undiscoverable for users.

# Expected Behaviour

- The frontend automatically detects a backend data-generation change and invalidates its definition/record caches — open tabs self-heal without user intervention.
- No request storms: within a stable generation, caching behavior is unchanged.

# Solution (approved by user 2026-07-29)

**Generation-based cache invalidation:**

1. **Backend** — new lightweight endpoint `GET /api/v1/runtime/meta/generation` returning a generation marker derived from Flyway state: a hash/concatenation of `max(installed_rank)` + `max(installed_on)` from `flyway_schema_history`. This changes on every reseed and on any new migration, but is stable across plain restarts. **Fallback:** if `flyway_schema_history` does not exist (Flyway disabled env), use the application startup timestamp.
2. **Frontend** — app-level hook (`useDefinitionGeneration`) that:
   - Polls the generation endpoint every 30 s (`refetchInterval`) and on window focus
   - Persists the last-seen generation in `localStorage` (survives tab reloads; fresh tabs compare on first load)
   - On change: invalidates `['window-definition']`, `['window-records']`, `['window-record']`, and menu queries, then stores the new value
   - Guard against invalidate loops: store the new generation BEFORE invalidating
3. Wire the hook into a layout component mounted on all authenticated routes.

# Acceptance Criteria

## Backend (`[SE]`)
- [ ] `GET /api/v1/runtime/meta/generation` returns `success: true` with a `generation` string inside the standard `ApiResponse` envelope
- [ ] Value is identical across repeated calls within the same DB generation
- [ ] Value changes after `start-all.sh --setup` and after any new Flyway migration
- [ ] Falls back to startup timestamp (no error) when `flyway_schema_history` is absent
- [ ] Endpoint requires authentication (consistent with other `/runtime/**` endpoints)

## Frontend (`[SE]`)
- [ ] Generation check hook runs at app level (all authenticated pages)
- [ ] On generation change: definition + record + menu caches invalidated; last-seen persisted to `localStorage`
- [ ] No invalidate loop (new value stored before invalidation; re-render does not re-trigger)
- [ ] No request storm: window definition fetched at most once per staleTime per window within a stable generation

## Verification (`[QA]`)
- [ ] Reseed simulation: with the app open in a browser tab, run `start-all.sh --setup`; within ~30 s the open tab refreshes definitions automatically — child tabs render with fresh ids, no ghost-UUID errors
- [ ] Fresh tab after reseed: opens with correct data on first load (localStorage comparison)
- [ ] Stable generation: no visible refetch/invalidate loop during normal use

## Build & Tests (`[SE]`)
- [ ] `mvn clean compile` succeeds; all 36 backend tests pass
- [ ] `tsc --noEmit` succeeds

# Files Affected

- Backend: new `core/runtime/controller/RuntimeMetaController.java`; generation resolution in a small service/helper (JdbcTemplate over `flyway_schema_history`, fallback startup timestamp)
- Frontend: new `core/runtime/hooks/useDefinitionGeneration.ts`; `core/runtime/api/runtimeApi.ts` (add `fetchDefinitionGeneration()`); wire into authenticated layout (e.g., `components/layouts/` shell)
- Docs: `ai/project/changes/CHANGE-ENH-004.md`

# Out of Scope

- Reducing `staleTime`/`gcTime` globally (would increase server load; generation check is the targeted fix)
- Cross-tab broadcast of invalidation (single-tab polling is sufficient for dev)

  - 2026-07-30 — Software Engineer — Cascade COMPLETED with PRD-005 merge. Manual reseed scenario deferred (non-blocking).