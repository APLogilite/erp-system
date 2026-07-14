---
task_id: TASK-039
type: API
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-039
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: Software Engineer
---

# Change Report — TASK-039

## Summary

Created the runtime window data CRUD API endpoints for listing, creating, updating, and deleting records through a window. Uses the new Window/Tab/Field metadata schema to resolve table names, apply where_clause filters, and load child tab records.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` | Orchestrates CRUD operations using WindowDefinition + DynamicCrudService |
| `backend/src/main/java/com/erp/core/runtime/controller/WindowDataController.java` | REST controller for data CRUD endpoints |

## Files Modified

None

## Files Removed

None

## Database Changes

None

## API Changes

### New Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/runtime/windows/{windowName}/records` | Paginated record list from main tab |
| GET | `/api/v1/runtime/windows/{windowName}/records/{id}` | Single record + child tab records |
| POST | `/api/v1/runtime/windows/{windowName}/records` | Create record (auto-sets where_clause value) |
| PUT | `/api/v1/runtime/windows/{windowName}/records/{id}` | Update record |
| DELETE | `/api/v1/runtime/windows/{windowName}/records/{id}` | Soft-delete record |

### Key Behaviors

- **List**: Uses the window's main tab (first tab where `parent_column IS NULL`); applies tab `where_clause` (e.g. `order_type = 'sales'`)
- **Get**: Returns the main record plus child tab records (linked via `parent_column` FK)
- **Create**: Injects system fields (id, tenant_id, timestamps) + auto-sets where_clause values
- **Update**: Modifies only provided fields; preserves tenant isolation
- **Delete**: Soft-delete (`is_active=false`, `deleted_at=NOW()`)
- All endpoints require authentication (401 if no RuntimeContext)
- All endpoints return `ApiResponse<T>` envelope

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

- These endpoints replace the old `GET /api/v1/runtime/forms/{formCode}/records` (old endpoint still exists)
- Response structure differs from the old `RecordCrudService` response

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |

## Known Limitations

- `where_clause` parsing is basic (supports `field = value` and `field = @id@` patterns only)
- No role-based access filtering (to be added in TASK-045 with sys_window_access)
- No field-level validation (read-only/mandatory enforcement is frontend-side)
- Child tab records are loaded individually per parent (N+1; acceptable for typical form sizes)

## Follow-up Recommendations

- TASK-040 (Frontend Menu Navigation) can start as TASK-037 is READY_FOR_TEST
- TASK-042 (Seed business tables) can also proceed
- Consider enhancing where_clause parsing for complex expressions
