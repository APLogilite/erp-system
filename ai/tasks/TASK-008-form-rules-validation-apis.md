---
id: TASK-008

title: Implement Form Rules & Validation APIs (Backend)

type: API

status: TESTED

priority: High

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-008

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 5

actual_hours: 1.5

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-007

depends_on:
  - TASK-007

blocks:
  - TASK-012

labels: [backend, api, rules, validation]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-008.md

test_report: ai/tests/TEST-TASK-008.md

history:
  - created
  - 2026-07-08 — Planning audit: demoted READY_FOR_DEV → PLANNED
  - 2026-07-08 — Re-evaluated: restored to READY_FOR_DEV
  - 2026-07-08 — Implementation: added ExpressionValidationService + ExpressionController with 4 endpoints (validate expression, validate action, validate pattern, evaluate). Added operator/action validation to FormRuleService. Added @PreAuthorize to FormRuleController, FormValidationController, ExpressionController.
  - 2026-07-09 — QA verified; all files present; compilation PASS; status: TESTED

---

# Goal

Implement APIs for managing field rules (visibility, read-only, required conditions) and field validations on form fields.

---

# Description

Add endpoints and services for rules and validations.

## APIs

### Rules
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/forms/{formId}/fields/{fieldId}/rules` | List rules for a field |
| POST | `/api/metadata/forms/{formId}/fields/{fieldId}/rules` | Add rule |
| PUT | `/api/metadata/forms/{formId}/fields/{fieldId}/rules/{ruleId}` | Update rule |
| DELETE | `/api/metadata/forms/{formId}/fields/{fieldId}/rules/{ruleId}` | Delete rule |

### Validations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/forms/{formId}/fields/{fieldId}/validations` | List validations |
| POST | `/api/metadata/forms/{formId}/fields/{fieldId}/validations` | Add validation |
| PUT | `/api/metadata/forms/{formId}/fields/{fieldId}/validations/{valId}` | Update validation |
| DELETE | `/api/metadata/forms/{formId}/fields/{fieldId}/validations/{valId}` | Delete validation |

### Expression Validation
| POST | `/api/metadata/expressions/validate` | Validate a rule expression syntax |
| POST | `/api/metadata/expressions/evaluate` | Test-evaluate with sample data |

## Service Logic

### Rule Model
Each rule has: `conditionField`, `conditionOperator`, `conditionValue`, `action`, `logicGroup`, `position`

Supported operators: `equals`, `not_equals`, `greater_than`, `less_than`, `greater_than_or_equal`, `less_than_or_equal`, `contains`, `is_empty`, `is_not_empty`, `in`

Supported actions: `show`, `hide`, `read_only`, `editable`, `required`, `optional`

### Validation Model
Each validation has: `type`, `value`, `message`, `position`

Supported types: `required`, `min_length`, `max_length`, `min`, `max`, `pattern`, `custom_expression`

### Expression Validation
- For `custom_expression`, validate the syntax is parseable
- For `pattern`, validate it's a valid regex
- `evaluate` endpoint takes sample data and tests the expression

---

# Acceptance Criteria

- [x] Rules can be created, updated, deleted per field
- [x] Validations can be created, updated, deleted per field
- [x] Expression validation endpoint returns parse errors
- [x] Test evaluation endpoint runs the expression against sample data and returns pass/fail
- [x] All CRUD scoped to the form's tenant (if tenant form)
- [x] Proper error messages for invalid operators, actions, or values

---

# Technical Notes

- The rule/validation endpoints are nested under form and field IDs
- Authorization checks: ensure the form belongs to the current tenant before any operation
- Expression validation is syntax-only (not data-aware) in backend

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/controller/FormRuleController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormValidationController.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormRuleService.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormValidationService.java`
- `backend/src/main/java/com/erp/core/metadata/service/ExpressionValidationService.java`
- DTOs for rule/validation create/update/response
