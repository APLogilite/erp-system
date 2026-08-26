---
id: PRD-022
title: Projects, Tasks & Resource Planning
version: 1.0.0
status: APPROVED
priority: Medium
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-022-projects-tasks-resource-planning
target_branch: main
merge_strategy: merge

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL
  - Flyway (migrations only, disabled by default — enable for this PRD)
  - React 18
  - TypeScript (strict)
  - MUI 5

related_prds:
  - PRD-001 (Dynamic Form Configuration System v1.6.0)
  - PRD-003 (ERP Order Flow Forms v1.0.0)
  - PRD-004 (Window Hierarchy & Menu System v1.1.0)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED (business partners for customers)
  - PRD-004 must be COMPLETED
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Project and task management with resource planning
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD adds project management: projects, tasks, milestones, resource assignment, and project status tracking. It supports project-based work from initiation to closure.

---

# Problem Statement

**What problem are we solving?**

- No project structure
- No task assignment or progress tracking
- No project profitability visibility

**Who experiences this problem?**

- Project managers
- Team leads
- Delivery teams

**Why is this feature required?**

Project management is essential for service and project-based businesses.

---

# Business Goals

- Create and manage projects
- Break projects into tasks with assignments and deadlines
- Track task status and progress
- Assign resources (employees) to tasks
- Track project budget vs actual cost

---

# Functional Requirements

## FR-001 — Project Type

Categorize projects.

- Fields: code, name, is_active

**Priority:** Low

**Acceptance Criteria**
- [ ] `[SE]` `pm_project_type` table created
- [ ] `[SE]` Project type window seeded
- [ ] `[QA]` Types can be maintained

---

## FR-002 — Project Header

Create projects.

- Fields: code, name, project type, customer, project manager, start date, end date, status (Draft → In Progress → On Hold → Completed → Cancelled), budget, currency, description

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pm_project` table created and registered
- [ ] `[SE]` Project window seeded
- [ ] `[SE]` Document sequence `PRJ-XXXX` generated
- [ ] `[QA]` Project can be created

---

## FR-003 — Project Tasks

Break projects into tasks.

- Fields: project, task name, assigned to, start date, end date, estimated hours, actual hours, status (Not Started → In Progress → Completed → Cancelled), parent task
- Supports hierarchical tasks

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pm_task` table created and registered
- [ ] `[SE]` Tasks tab seeded
- [ ] `[QA]` Tasks can be created and assigned

---

## FR-004 — Project Milestones

Track key milestones.

- Fields: project, name, target date, actual date, status

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `pm_milestone` table created
- [ ] `[SE]` Milestones tab seeded
- [ ] `[QA]` Milestones can be tracked

---

## FR-005 — Resource Assignment

Assign employees to tasks.

- Fields: task, employee, role, allocated hours

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `pm_task_assignment` table created
- [ ] `[SE]` Assignments tab seeded
- [ ] `[QA]` Employees can be assigned

---

## FR-006 — Project Status Dashboard

Summarize project health.

- KPIs: total tasks, completed tasks, overdue tasks, budget vs actual

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Dashboard API implemented
- [ ] `[SE]` Dashboard window/screen seeded
- [ ] `[QA]` KPIs display correctly

---

# Non-Functional Requirements

- Tenant isolation
- Task hierarchy query performance < 500ms
- Soft-delete

---

# User Stories

- As a project manager, I want to create a project so that I can track deliverables.
- As a team lead, I want to assign tasks so that team members know what to do.
- As a manager, I want to see project status so that I can manage deadlines.

---

# User Flow

```
Create Project
  ↓
Define Tasks and Milestones
  ↓
Assign Resources
  ↓
Update Task Status
  ↓
Project Dashboard
```

---

# Scope

## Included

- Project types
- Project header
- Tasks and sub-tasks
- Milestones
- Resource assignments
- Project dashboard

## Excluded

- Timesheets (PRD-023)
- Project billing (PRD-023)
- Gantt chart
- Project templates
- Resource capacity planning

---

# UI / UX Requirements

- Project window with tasks, milestones, and assignments tabs
- Task list with status filters
- Project dashboard cards/charts
- Menu group: **Projects**

---

# API Requirements

- Runtime window CRUD for Project, Task, Milestone, Assignment
- `GET /api/v1/projects/{id}/dashboard`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `pm_project_type`
- `pm_project`
- `pm_task`
- `pm_milestone`
- `pm_task_assignment`

## Relationships
- `pm_project.customer_id` → `c_business_partner.id`
- `pm_task.project_id` → `pm_project.id`
- `pm_task.parent_id` → `pm_task.id`
- `pm_milestone.project_id` → `pm_project.id`
- `pm_task_assignment.task_id` → `pm_task.id`

---

# Security Requirements

- Roles:
  - `PROJECT_VIEW/CREATE`
  - `TASK_VIEW/CREATE`
  - `PROJECT_DASHBOARD_VIEW`
- Tenant isolation

---

# Performance Requirements

- Project list < 1s
- Dashboard < 1s

---

# Logging Requirements

- Audit log for project status changes

---

# Edge Cases

- Circular task parent references
- Project with no tasks
- Assignment hours exceed task estimated hours

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Scope creep into full PM suite | Medium | Keep v1.0 focused on tasks and assignments |

---

# Assumptions

- Business partners exist from PRD-003
- Employees exist after PRD-024

---

# Dependencies

- PRD-001, PRD-003, PRD-004
- PRD-023 depends on this PRD
- PRD-024 provides employee data

---

# Acceptance Criteria

- [ ] Project tables and forms created
- [ ] Tasks and milestones supported
- [ ] Resource assignments work
- [ ] Dashboard displays KPIs

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequence `PRJ`
- Seed menu and access

---

# Testing Requirements

- UI scenario: create project → tasks → assignments
- Unit tests for dashboard aggregation

---

# Future Enhancements

- Gantt chart
- Project templates
- Resource capacity planning
- Risk/issue tracking

---

# Open Questions

1. Do we need project templates in v1.0?
2. Should tasks support time logging directly, or only via timesheets?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-023 — Timesheets & Project Billing
- PRD-024 — Employees, Contracts & Organization
