---
id: CHANGE-TASK-008

task_id: TASK-008

parent_prd: PRD-001

branch: feature/TASK-008

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 1.5h

related_commits: []

related_files:
  - backend/src/main/java/com/erp/core/metadata/service/ExpressionValidationService.java
  - backend/src/main/java/com/erp/core/metadata/controller/ExpressionController.java
  - backend/src/main/java/com/erp/core/metadata/dto/ExpressionValidateRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/ExpressionEvaluateRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/ExpressionResultResponse.java
  - backend/src/main/java/com/erp/core/metadata/service/FormRuleService.java
  - backend/src/main/java/com/erp/core/metadata/controller/FormRuleController.java
  - backend/src/main/java/com/erp/core/metadata/controller/FormValidationController.java

review_required: true

test_required: true

---

# Summary

Completed form rules and validation APIs with expression validation/evaluation service. Added ExpressionValidationService with syntax validation (field name, operators, actions, regex patterns) and runtime evaluation against sample data. Added ExpressionController with 4 endpoints. Added operator/action validation to FormRuleService. Added @PreAuthorize security to FormRuleController, FormValidationController, and ExpressionController.

The CRUD endpoints for rules and validations already existed from previous work; this task filled the gaps: expression validation, operator/action validation, and authorization.

---

# Business Requirements Implemented

- FR-008: Field Rules — CRUD + operator/action validation + expression validation
- FR-009: Field Validation — CRUD + pattern validation + expression evaluation

---

# Files Added

| File | Purpose |
|------|---------|
| `ExpressionValidationService.java` | Validates rule expressions, actions, patterns, and evaluates expressions against sample data |
| `ExpressionController.java` | REST endpoints for expression validation and evaluation |
| `ExpressionValidateRequest.java` | DTO for expression/action/pattern validation requests |
| `ExpressionEvaluateRequest.java` | DTO for expression evaluation with sample data |
| `ExpressionResultResponse.java` | DTO for validation/evaluation results |

---

# Files Modified

| File | Summary |
|------|---------|
| `FormRuleService.java` | Added operator/action validation with error messages for unsupported values |
| `FormRuleController.java` | Added @PreAuthorize("hasRole('SYSTEM_ADMIN')") |
| `FormValidationController.java` | Added @PreAuthorize("hasRole('SYSTEM_ADMIN')") |

---

# API Changes

## New Endpoints

- `POST /api/metadata/expressions/validate` — Validate rule expression syntax
- `POST /api/metadata/expressions/validate-action` — Validate action keyword
- `POST /api/metadata/expressions/validate-pattern` — Validate regex pattern
- `POST /api/metadata/expressions/evaluate` — Evaluate expression against sample data

---

# Validation

## Build

PASS — `mvn compile` completed successfully

## Existing Automated Tests

PASS — 33/36 (3 pre-existing DatabaseConnectionTest failures)

