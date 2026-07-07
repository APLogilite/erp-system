---
id: TASK-002

title: Create JPA Entities for All Metadata Tables

type: Feature

status: READY_FOR_DEV

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 6

actual_hours:

parent_prd: PRD-001

prd_version: 1.5.0

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

change_summary:

test_report:

history:
  - created

---

# Goal

Create JPA entity classes, repositories, and DTOs for all normalized metadata tables so the backend can interact with the database.

---

# Description

Create the following Java classes in `backend/src/main/java/com/erp/core/metadata/`:

## Entities (extending BaseEntity)

Create entity classes for these tables:
1. **TableColumnEntity** → `sys_table_columns`
2. **FormFieldEntity** → `sys_form_fields`
3. **FormFieldRuleEntity** → `sys_form_field_rules`
4. **FormFieldValidationEntity** → `sys_form_field_validations`
5. **FormLayoutSectionEntity** → `sys_form_layout_sections`
6. **FormSectionFieldEntity** → `sys_form_section_fields`
7. **FormRoleFilterEntity** → `sys_form_role_filters`
8. **FormSubFormEntity** → `sys_form_sub_forms`
9. **FormTenantRoleEntity** → `sys_form_tenant_role`

## Update Existing Entities

1. **MetadataModelEntity** (existing) — Add fields: `tableType`, `tableName`, `description`
2. **MetadataViewEntity** (existing) — Add fields: `scope`, `tenantId`, `description`, `whereClauseField`, `whereClauseOperator`, `whereClauseValue`

## Repositories

Create JPA Repository interfaces for each new entity:
- `TableColumnRepository` — with `findByTableIdAndIsActiveTrueOrderByPosition(@Param("tableId") UUID tableId)`
- `FormFieldRepository` — with `findByFormIdAndIsActiveTrueOrderByPosition(UUID formId)`
- `FormFieldRuleRepository` — with `findByFieldIdIn(List<UUID> fieldIds)`
- `FormFieldValidationRepository` — with `findByFieldIdIn(List<UUID> fieldIds)`
- `FormLayoutSectionRepository` — with `findByFormIdOrderByPosition(UUID formId)`
- `FormSectionFieldRepository` — with `findBySectionIdIn(List<UUID> sectionIds)`
- `FormRoleFilterRepository` — with `findByFormIdAndRoleId(UUID formId, UUID roleId)`, `findByFormId(UUID formId)`
- `FormSubFormRepository` — with `findByParentFormIdOrderByPosition(UUID parentFormId)`
- `FormTenantRoleRepository` — with `findByFormIdAndTenantId(UUID formId, UUID tenantId)`, `findByTenantIdAndRoleId(UUID tenantId, UUID roleId)`, `deleteByFormIdAndTenantId(UUID formId, UUID tenantId)`

## DTOs

Create simple DTO records/classes for each entity for API request/response mapping.

---

# Acceptance Criteria

- [ ] All 8 new entity classes exist with correct JPA annotations (`@Entity`, `@Table`, `@Column`)
- [ ] All entity fields match the Flyway migration column definitions exactly
- [ ] Existing `MetadataModel` and `MetadataView` entities have the new fields added
- [ ] All 8 repository interfaces exist with the required query methods
- [ ] DTOs exist for create/update/list operations
- [ ] Code compiles with `mvn clean compile`
- [ ] All existing unit tests still pass

---

# Technical Notes

- Follow the existing code patterns in `com.erp.core.metadata.entity.*`
- Use `@JdbcTypeCode(SqlTypes.JSON)` for the `enumOptions` field in TableColumnEntity (stored as JSONB)
- All entities extend `BaseEntity` which provides id, createdAt, updatedAt, createdBy, updatedBy, isActive, deletedAt
- Use Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` patterns if already used in the project
- Repository naming: `EntityName + Repository` (e.g., `TableColumnRepository`)

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/entity/TableColumnEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormFieldEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormFieldRuleEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormFieldValidationEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormLayoutSectionEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormSectionFieldEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormSubFormEntity.java`
- `backend/src/main/java/com/erp/core/metadata/entity/FormTenantRoleEntity.java`
- Modified: `MetadataModel.java`, `MetadataView.java`
- `backend/src/main/java/com/erp/core/metadata/repository/TableColumnRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormFieldRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormFieldRuleRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormFieldValidationRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormLayoutSectionRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormSectionFieldRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormSubFormRepository.java`
- `backend/src/main/java/com/erp/core/metadata/repository/FormTenantRoleRepository.java`
- DTO classes in `backend/src/main/java/com/erp/core/metadata/dto/`
