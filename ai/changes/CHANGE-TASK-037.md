---
task_id: TASK-037
type: Feature
parent_prd: PRD-004
prd_version: 1.0.0
git_branch: feature/TASK-037
base_branch: prd/PRD-004-window-hierarchy-menu
status: READY_FOR_TEST
created: 2026-07-13
author: Software Engineer
---

# Change Report — TASK-037

## Summary

Created JPA entities, repositories, and CRUD services for all 7 new metadata tables in the `com.erp.modules.metadata` package. This enables Java code to interact with the new schema created in TASK-036.

## Files Added

| File | Description |
|------|-------------|
| `backend/src/main/java/com/erp/modules/metadata/entity/SysTable.java` | JPA entity for `sys_table` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysColumn.java` | JPA entity for `sys_column` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysWindow.java` | JPA entity for `sys_window` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysTab.java` | JPA entity for `sys_tab` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysWindowField.java` | JPA entity for `sys_window_field` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysWindowAccess.java` | JPA entity for `sys_window_access` |
| `backend/src/main/java/com/erp/modules/metadata/entity/SysMenu.java` | JPA entity for `sys_menu` |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysTableRepository.java` | Repository for SysTable |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysColumnRepository.java` | Repository for SysColumn |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysWindowRepository.java` | Repository for SysWindow |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysTabRepository.java` | Repository for SysTab |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysWindowFieldRepository.java` | Repository for SysWindowField |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysWindowAccessRepository.java` | Repository for SysWindowAccess |
| `backend/src/main/java/com/erp/modules/metadata/repository/SysMenuRepository.java` | Repository for SysMenu |
| `backend/src/main/java/com/erp/modules/metadata/service/SysTableService.java` | CRUD service for SysTable |
| `backend/src/main/java/com/erp/modules/metadata/service/SysColumnService.java` | CRUD service for SysColumn + findByTableId |
| `backend/src/main/java/com/erp/modules/metadata/service/SysWindowService.java` | CRUD service for SysWindow + findByName |
| `backend/src/main/java/com/erp/modules/metadata/service/SysTabService.java` | CRUD service for SysTab + findByWindowId |
| `backend/src/main/java/com/erp/modules/metadata/service/SysWindowFieldService.java` | CRUD service for SysWindowField + findByTabId |
| `backend/src/main/java/com/erp/modules/metadata/service/SysWindowAccessService.java` | CRUD service for SysWindowAccess |
| `backend/src/main/java/com/erp/modules/metadata/service/SysMenuService.java` | CRUD service with tree-building logic (MenuTreeNode DTO) |

## Files Modified

None

## Files Removed

None

## Database Changes

None (schema was already created in TASK-036; JPA `ddl-auto=update` keeps it in sync)

## API Changes

None (API endpoints come in TASK-038 and TASK-039)

## Configuration Changes

None

## Dependencies Added/Updated

None

## Breaking Changes

None (new module, no existing code references old metadata entities)

## Validation Results

| Check | Result |
|-------|--------|
| `mvn clean compile` | PASS |
| `mvn test` (36 tests) | ALL PASS |

## Known Limitations

- Services use basic CRUD from BaseService; no complex query methods yet
- SysMenu tree building currently loads all menus into memory (acceptable for the expected size)
- No caching layer yet on services

## Follow-up Recommendations

- TASK-038 (Window Definition API) can start as it only depends on TASK-037 being READY_FOR_TEST
- TASK-042 (Seed business tables) can also start as it only depends on TASK-036
