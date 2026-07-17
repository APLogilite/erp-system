---
id: CHANGE-TASK-055

task_id: TASK-055

parent_prd: PRD-005

branch: feature/TASK-055

type: Refactor

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 30 minutes (estimated)

related_commits:
  - fix(TASK-055): remove dead core/security/ package

related_files:
  - REMOVED backend/src/main/java/com/erp/core/security/ (entire directory, 12 files)

review_required: true

test_required: true

---

# Summary

Removed the stale `core/security/` package (12 files) that had been superseded by `platform/identity/authorization/`. Verified zero external references exist to `com.erp.core.security`. Backend compiles and all 36 tests pass after deletion.

---

# Scope Verification

- [ ] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- Remove Dead core/security/ Package
  - Entire `core/security/` directory deleted (12 files across 9 subdirectories)
  - `mvn clean compile` succeeds
  - All 36 existing tests pass
  - Confirmed zero external references to `core.security`

---

# Files Removed

| File | Reason |
|------|--------|
| `backend/.../core/security/controller/PermissionController.java` | Dead code |
| `backend/.../core/security/dto/PermissionCheckRequestDto.java` | Dead code |
| `backend/.../core/security/dto/PermissionCheckResponseDto.java` | Dead code |
| `backend/.../core/security/dto/PermissionMetadataDto.java` | Dead code |
| `backend/.../core/security/enums/PermissionLevel.java` | Dead code |
| `backend/.../core/security/evaluator/PermissionEvaluator.java` | Dead code |
| `backend/.../core/security/exception/PermissionDeniedException.java` | Dead code |
| `backend/.../core/security/mapper/PermissionMapper.java` | Dead code |
| `backend/.../core/security/registry/PermissionRegistry.java` | Dead code |
| `backend/.../core/security/service/PermissionService.java` | Dead code |
| `backend/.../core/security/service/PermissionServiceImpl.java` | Dead code |
| `backend/.../core/security/validator/PermissionValidator.java` | Dead code |

---

# Validation

## Build

PASS — `mvn clean compile` succeeds

---

## Existing Automated Tests

PASS — All 36 tests pass

---

# Breaking Changes

None. No external code references this package.
