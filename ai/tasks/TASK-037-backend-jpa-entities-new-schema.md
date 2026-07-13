---
id: TASK-037

title: Backend — JPA Entities + Services for New Metadata Tables

type: Feature

status: PLANNING

priority: Critical

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-13

updated: 2026-07-13

started:

completed:

estimated_hours: 6

actual_hours:

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: prd/PRD-004-window-hierarchy-menu

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: [TASK-036]

blocks: [TASK-038, TASK-039, TASK-046]

labels: [backend, jpa, entity, service]

review_required: true

test_required: true

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
