---
id: TASK-002

title: Create JPA Entities for All Metadata Tables

type: Feature

status: READY_FOR_TEST

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started: 2026-07-07

completed: 2026-07-07

estimated_hours: 6

actual_hours: 0.5

parent_prd: PRD-001

prd_version: 1.5.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-001

depends_on:
  - TASK-001

blocks:
  - TASK-003
  - TASK-004
  - TASK-007

labels: [backend, jpa, entity]

review_required: true

test_required: false

automation_required: false

change_summary: ai/changes/CHANGE-TASK-002.md

test_report:

history:
  - created
  - implemented 2026-07-07 — Developer completed JPA entities, repositories, and DTOs for all metadata tables

---

# Goal

Create JPA entity classes, repositories, and DTOs for all normalized metadata tables so the backend can interact with the database.

---

# Description

Created the following Java classes in `backend/src/main/java/com/erp/core/metadata/`:

## Entities (extending BaseEntity)

1. **TableColumnEntity** → `sys_table_columns`
2. **FormFieldEntity** → `sys_form_fields`
3. **FormFieldRuleEntity** → `sys_form_field_rules`
4. **FormFieldValidationEntity** → `sys_form_field_validations`
5. **FormLayoutSectionEntity** → `sys_form_layout_sections`
6. **FormSectionFieldEntity** → `sys_form_section_fields`
7. **FormRoleFilterEntity** → `sys_form_role_filters`
8. **FormSubFormEntity** → `sys_form_sub_forms`
9. **FormTenantRoleEntity** → `sys_form_tenant_role`

## Updated Existing Entities

1. **MetadataModel** — Added fields: `tableType`, `tableName`, `description`
2. **MetadataView** — Added fields: `scope`, `tenantId`, `description`, `whereClauseField`, `whereClauseOperator`, `whereClauseValue`

## Repositories

9 JPA Repository interfaces with query methods as specified.

## DTOs

18 DTO classes (response DTO + create request per entity).

---

# Acceptance Criteria

- [x] All 9 new entity classes exist with correct JPA annotations (`@Entity`, `@Table`, `@Column`)
- [x] All entity fields match the Flyway migration column definitions exactly
- [x] Existing `MetadataModel` and `MetadataView` entities have the new fields added
- [x] All 9 repository interfaces exist with the required query methods
- [x] DTOs exist for create/update/list operations
- [x] Code compiles with `mvn clean compile`
- [x] All existing unit tests still pass

---

# Technical Notes

- Followed the existing code patterns in `com.erp.core.metadata.entity.*`
- Used `@JdbcTypeCode(SqlTypes.JSON)` for the `enumOptions` field in TableColumnEntity (stored as JSONB)
- All entities extend `BaseEntity` which provides id, createdAt, updatedAt, createdBy, updatedBy, isActive, deletedAt
- No Lombok — explicit getters/setters consistent with existing codebase
- Repository naming: `EntityName + Repository` (e.g., `TableColumnRepository`)
