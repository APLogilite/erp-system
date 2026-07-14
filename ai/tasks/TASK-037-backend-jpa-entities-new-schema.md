---
id: TASK-037

title: Backend — JPA Entities + Services for New Metadata Tables

type: Feature

status: TESTED

priority: Critical

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: feature/TASK-037

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 6

actual_hours: 2

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-036]

blocks: [TASK-038, TASK-039, TASK-046]

labels: [backend, jpa, entity, service]

history:
  - 2026-07-13: Status READY_FOR_DEV → IN_DEVELOPMENT. Assigned to Software Engineer. Started implementation.
  - 2026-07-13: 7 JPA entities + 7 repositories + 7 services created. Validation passed. Status → READY_FOR_TEST.
  - 2026-07-14: QA verification completed. 10/12 tests passed, 2 skipped, 0 bugs. Status → TESTED.

review_required: true

test_required: true

test_report: ai/tests/TEST-TASK-037.md

change_report: ai/changes/CHANGE-TASK-037.md

---

# Goal

Create JPA entities, repositories, and CRUD services for all new metadata tables.

---

# Description

Create JPA entities for:
- `SysTable` → `sys_table`
- `SysColumn` → `sys_column` (with FK to SysTable)
- `SysWindow` → `sys_window` (with FK to SysTable)
- `SysTab` → `sys_tab` (with FK to SysWindow, SysTable)
- `SysWindowField` → `sys_window_field` (with FK to SysTab, SysColumn)
- `SysWindowAccess` → `sys_window_access` (with FK to SysWindow)
- `SysMenu` → `sys_menu` (with self-referencing FK for parent)

Each entity should extend the existing `BaseEntity` pattern.
Each entity should have a corresponding `Repository` interface extending `JpaRepository`.
Each entity should have a `Service` class with basic CRUD extending `BaseService` pattern.

The `SysMenu` entity should support the tree structure (parent-child relationship).

---

# Acceptance Criteria

- [ ] All 7 JPA entities created with correct relationships
- [ ] All FK mappings use `@ManyToOne` / `@OneToMany` as appropriate
- [ ] Repositories created for each entity
- [ ] Services created for each entity with CRUD operations
- [ ] SysMenu service includes methods for building menu tree
- [ ] Entities follow existing BaseEntity pattern (UUID id, soft-delete, timestamps)
- [ ] Seed data from TASK-036 migration is readable through JPA

---

# Technical Notes

- Follow the existing entity patterns in `platform/identity/` and `modules/`
- Use `@Entity`, `@Table(name = "sys_table")` annotations
- Use Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- SysMenu tree: use `@ManyToOne` for parent, `@OneToMany(mappedBy="parent")` for children
