---
id: PRD-024
title: Employees, Contracts & Organization
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-024-employees-contracts-organization
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
  - PRD-003 must be COMPLETED (business partner for employee contact)
  - PRD-004 must be COMPLETED
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Employee master data, contracts, and HR organization
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD establishes the HR foundation: employees, contracts, job positions, departments, and employment status. It links employees to the identity/organization hierarchy and to business partners for contact information.

---

# Problem Statement

**What problem are we solving?**

- No employee records
- No employment contracts
- No HR organizational structure

**Who experiences this problem?**

- HR managers
- Payroll officers
- Project managers assigning resources

**Why is this feature required?**

Employees are required for project assignments, timesheets, attendance, leave, and payroll.

---

# Business Goals

- Maintain employee master records
- Track employment contracts and compensation
- Define job positions and departments
- Link employees to users and business partners
- Provide employee directory

---

# Functional Requirements

## FR-001 — Department

Create HR departments.

- Fields: code, name, parent department, manager, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_department` table created
- [ ] `[SE]` Department window seeded
- [ ] `[QA]` Departments can be maintained

---

## FR-002 — Job Position

Define job positions.

- Fields: code, name, department, description, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_job_position` table created
- [ ] `[SE]` Job position window seeded
- [ ] `[QA]` Positions can be maintained

---

## FR-003 — Employee

Create employee records.

- Fields: employee number, first name, last name, business partner, user account, department, job position, manager, hire date, termination date, employment status (Active/On Leave/Terminated), gender, birth date, marital status

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_employee` table created and registered
- [ ] `[SE]` Employee window seeded
- [ ] `[SE]` Document sequence `EMP-XXXX` generated
- [ ] `[QA]` Employee can be created

---

## FR-004 — Employee Contact & Address

Link employee to business partner contact/address.

- Reuse existing address/contact tabs via business partner link

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Employee links to business partner
- [ ] `[QA]` Address/contact visible on employee

---

## FR-005 — Employment Contract

Track contract details.

- Fields: employee, contract type (Permanent/Contract/Intern), start date, end date, salary, salary frequency, currency, working hours, notice period, status

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_contract` table created and registered
- [ ] `[SE]` Contracts tab seeded on employee window
- [ ] `[QA]` Contract can be added to employee

---

## FR-006 — Employee Directory

Searchable directory of employees.

- Filters: department, job position, status

**Priority:** Low

**Acceptance Criteria**
- [ ] `[SE]` Directory API implemented
- [ ] `[SE]` Directory screen seeded
- [ ] `[QA]` Directory searchable

---

# Non-Functional Requirements

- Tenant isolation
- PII protection (employee personal data)
- Soft-delete

---

# User Stories

- As an HR manager, I want to add employees so that I can manage the workforce.
- As a payroll officer, I want contract details so that I can calculate pay.
- As a project manager, I want an employee directory so that I can assign resources.

---

# User Flow

```
Create Departments
  ↓
Create Job Positions
  ↓
Create Business Partner (if new employee)
  ↓
Create Employee Record
  ↓
Add Employment Contract
  ↓
Employee Directory
```

---

# Scope

## Included

- Departments
- Job positions
- Employee master
- Employment contracts
- Employee directory

## Excluded

- Attendance (PRD-025)
- Leave (PRD-025)
- Expenses (PRD-025)
- Payroll calculation (PRD-026)
- Recruitment
- Performance reviews

---

# UI / UX Requirements

- Employee window with contracts tab
- Department tree view
- Employee directory grid with filters
- Menu group: **Human Resources**

---

# API Requirements

- Runtime window CRUD for Department, Job Position, Employee, Contract
- `GET /api/v1/hr/employees/directory`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `hr_department`
- `hr_job_position`
- `hr_employee`
- `hr_contract`

## Relationships
- `hr_department.parent_id` → `hr_department.id`
- `hr_job_position.department_id` → `hr_department.id`
- `hr_employee.business_partner_id` → `c_business_partner.id`
- `hr_employee.user_id` → identity user
- `hr_employee.department_id` → `hr_department.id`
- `hr_employee.job_position_id` → `hr_job_position.id`
- `hr_contract.employee_id` → `hr_employee.id`

---

# Security Requirements

- Roles:
  - `EMPLOYEE_VIEW/CREATE`
  - `CONTRACT_VIEW/CREATE`
- PII access restricted to HR roles
- Tenant isolation

---

# Performance Requirements

- Employee directory < 1s for 10,000 employees

---

# Logging Requirements

- Audit log for employee changes
- Audit log for contract changes

---

# Edge Cases

- Employee with no user account
- Multiple active contracts
- Terminated employee reassignment

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| PII exposure | High | Role-based access; mask sensitive fields |

---

# Assumptions

- Business partner exists from PRD-003
- Identity user module provides user accounts

---

# Dependencies

- PRD-001, PRD-003, PRD-004
- PRD-022 uses employee data for project assignments
- PRD-023 uses employee data for timesheets
- PRD-025 depends on this PRD
- PRD-026 depends on this PRD

---

# Acceptance Criteria

- [ ] Department and job position tables created
- [ ] Employee and contract tables created
- [ ] Employee links to business partner and user
- [ ] Directory works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequence `EMP`
- Seed menu and access

---

# Testing Requirements

- UI scenario: create department → job → employee → contract
- Unit tests for directory filters

---

# Future Enhancements

- Recruitment pipeline
- Performance appraisals
- Skills matrix
- Org chart

---

# Open Questions

1. Should every employee have a business partner automatically created?
2. Do we need contract document attachments?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-022 — Projects, Tasks & Resource Planning
- PRD-023 — Timesheets & Project Billing
- PRD-025 — Attendance, Leave & Expenses
- PRD-026 — Payroll Rules, Payslips & Compliance
