---
task_id: TASK-041
type: UI
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-041
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: Software Engineer
---

# Change Report — TASK-041

## Summary

Created the `WindowPage` component that renders actual dynamic forms from the new Window/Tab/Field API. Added the `/window/{windowName}` route and a redirect from old `/runtime/{formCode}` paths. Added window-specific API functions to the frontend runtime API client.

## Files Added

| File | Description |
|------|-------------|
| `frontend/src/routes/window/WindowPage.tsx` | Main window page with list view + record create/edit dialog |

## Files Modified

| File | Change |
|------|--------|
| `frontend/src/core/runtime/api/runtimeApi.ts` | Added window API types + functions (`fetchWindowDefinition`, `fetchWindowRecords`, `fetchWindowRecord`, `createWindowRecord`, `updateWindowRecord`, `deleteWindowRecord`) |
| `frontend/src/routes/AppRoutes.tsx` | Added `/window/:windowName` route and `/runtime/:formCode` → `/window/:formCode` redirect |

## Files Removed

None

## Database Changes

None

## API Changes (consume frontend)

### New Frontend API Functions

| Function | Method | Path |
|----------|--------|------|
| `fetchWindowDefinition` | GET | `/runtime/windows/{windowName}/definition` |
| `fetchWindowRecords` | GET | `/runtime/windows/{windowName}/records` (paginated) |
| `fetchWindowRecord` | GET | `/runtime/windows/{windowName}/records/{id}` |
| `createWindowRecord` | POST | `/runtime/windows/{windowName}/records` |
| `updateWindowRecord` | PUT | `/runtime/windows/{windowName}/records/{id}` |
| `deleteWindowRecord` | DELETE | `/runtime/windows/{windowName}/records/{id}` |

## Route Changes

| Route | Component | Description |
|-------|-----------|-------------|
| `/window/:windowName` | `WindowPage` | Main window page using new Window/Tab/Field API |
| `/runtime/:formCode` | Redirect→`/window/:formCode` | Legacy route redirect |

## WindowPage Features

- **List view**: Displays records from the window's main tab with pagination
- **Column headers**: Uses field `label_override` or `column.label`
- **Record dialog**: Create/edit with proper field labels and types
- **Field rendering**: Supports string, text (multiline), integer/decimal (number input)
- **Readonly/mandatory**: Respects field-level is_readonly and is_mandatory
- **Create/Update/Delete**: Full CRUD via window API
- **Loading/error states**: Proper loading spinners and error messages

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

- Old `/runtime` routes now redirect to `/window/*` — any bookmarked `/runtime/{formCode}` URLs will redirect
- Old `FormNavigationMenu` still exists but is no longer rendered by the sidebar
- The `RuntimePage` component still works for backward compatibility but new routes use `WindowPage`

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` (backend) | PASS |
| `mvn test` (36 backend tests) | ALL PASS |
| `pnpm typecheck` (frontend) | PASS |

## Known Limitations

- Child tab records (inline grids) not yet rendered — only main tab shown
- Field display/readonly logic expression evaluation not implemented
- Ctrl+K search still uses old `/runtime` paths (needs separate update)
- No date picker for date/datetime fields (uses text input)
- No autocomplete for many2one fields (uses text input)
- The RuntimePage still uses old API but is kept for backward compatibility

## Follow-up Recommendations

- TASK-042 (Seed business tables) — can proceed in parallel
- Implement field logic evaluation (display_logic/readonly_logic)
- Add child tab inline grids
- Update Ctrl+K search to use `/window/{windowName}` paths
- Add specialized field renderers (date picker, autocomplete, select)
