---
document: CHANGE_REPORT
task: TASK-017
status: COMPLETE
created: 2026-07-08
---

# Change Report — TASK-017

## Summary

Implemented Record Data APIs with proper runtime context integration, row filter resolution with dynamic variables, and the `GET /api/runtime/forms` endpoint for listing accessible forms. Fixed all record CRUD endpoints to use authenticated context instead of placeholder UUIDs.

## Files Added

None.

## Files Modified

| File | Changes |
|------|---------|
| `backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java` | Added `GET /api/runtime/forms` endpoint (`listAccessibleForms`) with tenant/role-based form filtering. Fixed all record endpoints (`/records`, `/records/{id}`, POST, PUT, DELETE) to use `RuntimeContextHolder.get()` for tenantId, roleCodes, and userId instead of `UUID.randomUUID()`. |
| `backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java` | Added `FormRoleFilterRepository` and `RoleRepository` dependencies. Updated method signatures to accept `List<String> roleCodes` instead of `List<UUID> roleIds`. Implemented `buildRowFilters()` to load real row filters from `sys_form_role_filters` via batch query (`findByFormIdAndRoleIdIn`). Added `resolveDynamicVariables()` for `{current_user_id}` and `{current_tenant_id}` resolution. |
| `backend/src/main/java/com/erp/core/metadata/repository/FormRoleFilterRepository.java` | Added `findByFormIdAndRoleIdIn(UUID formId, List<UUID> roleIds)` batch query method. |

## Validation Results

| Check | Result |
|-------|--------|
| `mvn compile` | PASS (544 source files) |
| `mvn test` | PASS (33/33 unit tests pass; 3 pre-existing integration test failures unchanged) |

## Acceptance Criteria

- [x] `GET /api/runtime/forms` returns only forms the user has role access to
- [x] `GET .../records` returns data filtered by where clause AND tenant_id
- [x] `GET .../records/{id}` returns record + sub-form child records
- [x] Creating a record auto-assigns tenant_id and where clause value
- [x] Read-only fields are stripped from update payloads
- [x] Backend re-validates required fields and data types
- [x] Delete is soft-delete
- [x] 403 returned for unauthorized form access
- [x] 404 returned for non-existent records (or records from other tenants)
- [x] All endpoints use the standard `ApiResponse<T>` envelope

## Key Implementation Decisions

1. **RuntimeContext integration**: All endpoints now extract `tenantId`, `roleCodes`, and `userId` from `RuntimeContextHolder.get()`.

2. **Row filter resolution**: `buildRowFilters()` converts role codes to UUIDs via `RoleRepository.findByCodeIn()`, loads configured filters from `FormRoleFilterRepository.findByFormIdAndRoleIdIn()`, and resolves `{current_user_id}` and `{current_tenant_id}` placeholders against the authenticated context.

3. **`GET /api/runtime/forms` endpoint**: Lists accessible forms by checking (a) SYSTEM_ADMIN bypass, (b) global forms with tenant-role assignments, (c) tenant-scoped forms belonging to the user's tenant with role access.

4. **Service signature change**: `RecordCrudService` methods now accept `List<String> roleCodes` (role names from JWT) instead of `List<UUID> roleIds`. Role-to-UUID conversion happens inside `buildRowFilters()`.

## Known Limitations

1. **Dynamic variables**: Only `{current_user_id}` and `{current_tenant_id}` are supported. Future JWT claims like `{current_user_region}` require additional JWT claim exposure.

2. **Row filter inheritance**: Sub-form row filters are loaded independently (not inherited from parent form). Full filter inheritance (parent + child combined) is deferred.

3. **Breadcrumb depth**: `buildBreadcrumb()` returns only the current form context. Multi-level breadcrumbs (Order > Order Line > Tax Entry) require `BreadcrumbService` integration (TASK-018).

## Breaking Changes

None. Method signatures in `RecordCrudService` changed parameter types (`List<UUID>` → `List<String>`), but this is an internal service not exposed as a public API.

## Follow-up Recommendations

- **TASK-018**: Implement multi-level breadcrumb with parent context from the sub-form chain.
- **Future**: Add additional JWT claim dynamic variable resolution for `{current_user_role}`, `{current_user_region}`, etc.
