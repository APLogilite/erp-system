---
id: PRD-023
title: Timesheets & Project Billing
version: 1.0.0
status: DRAFT
priority: Medium
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-023-timesheets-project-billing
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
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0)
  - PRD-018 (Chart of Accounts, Tax & Fiscal Calendar)
  - PRD-019 (General Ledger & Journal Posting Engine)
  - PRD-022 (Projects, Tasks & Resource Planning)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-007 must be COMPLETED
  - PRD-018 must be COMPLETED
  - PRD-019 must be COMPLETED
  - PRD-022 must be COMPLETED (projects and tasks exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Timesheets and project billing
---

# Executive Summary

This PRD adds employee timesheets against project tasks and project billing to customer invoices. It links project work to AR and revenue.

---

# Problem Statement

**What problem are we solving?**

- No time tracking against projects
- No way to bill customers for project hours
- No labor cost visibility

**Who experiences this problem?**

- Consultants
- Project managers
- Billing clerks

**Why is this feature required?**

Time and materials projects need timesheets to invoice accurately.

---

# Business Goals

- Allow employees to log time per project/task
- Approve timesheets
- Bill approved time to customer invoices
- Track project labor cost

---

# Functional Requirements

## FR-001 — Timesheet Header

Create weekly/bi-weekly timesheets.

- Fields: employee, week start, week end, status (Draft → Submitted → Approved → Rejected → Billed)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `tm_timesheet` table created and registered
- [ ] `[SE]` Timesheet window seeded
- [ ] `[QA]` Timesheet can be created

---

## FR-002 — Timesheet Lines

Log hours per project/task.

- Fields: timesheet, date, project, task, description, hours, billable flag, billing rate
- Daily/weekly totals

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `tm_timesheet_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[SE]` Total hours auto-calculated
- [ ] `[QA]` Hours can be logged per task

---

## FR-003 — Timesheet Approval

Approve or reject submitted timesheets.

- Approver role required
- Approved timesheets can be billed

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Approval workflow service implemented
- [ ] `[SE]` Status transitions enforced
- [ ] `[QA]` Approval prevents edits

---

## FR-004 — Billing Rate

Define billing rates per employee/role/project.

- Fields: employee/role, project, rate, currency, effective date

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `tm_billing_rate` table created
- [ ] `[SE]` Billing rate window seeded
- [ ] `[QA]` Rate resolves correctly on timesheet line

---

## FR-005 — Project Billing

Generate customer invoices from approved timesheet lines.

- Select approved, unbilled lines by project
- Group into invoice lines
- Create AR invoice (PRD-007/020)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Billing service implemented
- [ ] `[SE]` AR invoice created from timesheet lines
- [ ] `[SE]` Timesheet lines marked as billed
- [ ] `[QA]` Invoice total matches billable hours × rate

---

## FR-006 — Project Cost & Profitability

Track labor cost and revenue per project.

- Cost = hours × employee cost rate
- Revenue = hours × billing rate
- Profit = revenue - cost

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Profitability query implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Profitability numbers are correct

---

# Non-Functional Requirements

- Tenant isolation
- Timesheet approval workflow
- Billing atomic with invoice creation

---

# User Stories

- As a consultant, I want to log my hours so that I can be billed correctly.
- As a project manager, I want to approve timesheets so that only valid hours are billed.
- As a billing clerk, I want to generate invoices from timesheets so that customers are billed on time.

---

# User Flow

```
Employee Logs Hours in Timesheet
  ↓
Submit for Approval
  ↓
Project Manager Approves
  ↓
Billing Clerk Generates Invoice
  ↓
AR Invoice Created and Posted
```

---

# Scope

## Included

- Timesheet header and lines
- Timesheet approval
- Billing rates
- Project billing to AR invoice
- Project cost/profitability report

## Excluded

- Expense reporting (PRD-025)
- Absence/leave tracking (PRD-025)
- Advanced billing rules (milestones, fixed fee)
- Payroll integration (PRD-026)

---

# UI / UX Requirements

- Weekly timesheet grid (days × projects/tasks)
- Approval button with comment
- Billing screen to select lines and generate invoice
- Profitability report

---

# API Requirements

- Runtime window CRUD for Timesheet, Timesheet Line, Billing Rate
- `POST /api/v1/timesheets/{id}/submit`
- `POST /api/v1/timesheets/{id}/approve`
- `POST /api/v1/timesheets/{id}/reject`
- `POST /api/v1/projects/{id}/bill-timesheets`
- `GET /api/v1/projects/{id}/profitability`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `tm_timesheet`
- `tm_timesheet_line`
- `tm_billing_rate`

## Modified Tables
- `ar_invoice` — add `project_id` and `billed_from_timesheet` flag (optional)

## Relationships
- `tm_timesheet_line.timesheet_id` → `tm_timesheet.id`
- `tm_timesheet_line.project_id` → `pm_project.id`
- `tm_timesheet_line.task_id` → `pm_task.id`

---

# Security Requirements

- Roles:
  - `TIMESHEET_VIEW/CREATE`
  - `TIMESHEET_APPROVE`
  - `PROJECT_BILLING_CREATE`
- Tenant isolation
- Users see only their own timesheets unless approver

---

# Performance Requirements

- Timesheet list < 1s
- Billing generation < 2s for 100 lines

---

# Logging Requirements

- Audit log for timesheet approval
- Audit log for billing

---

# Edge Cases

- Billing rate missing for employee/project
- Timesheet line billed partially
- Rejecting already billed timesheet

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Billing rate complexity | Medium | Rate resolution hierarchy documented |

---

# Assumptions

- PRD-022 projects and tasks exist
- PRD-020 AR posting available
- Employee data from PRD-024

---

# Dependencies

- PRD-001, PRD-007, PRD-018, PRD-019, PRD-020, PRD-022
- PRD-024 provides employee data
- PRD-026 may use timesheet hours for payroll

---

# Acceptance Criteria

- [ ] Timesheet tables and forms created
- [ ] Approval workflow works
- [ ] Billing rate resolution works
- [ ] Project billing creates AR invoice
- [ ] Profitability report works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed menu and access

---

# Testing Requirements

- Integration test: log time → approve → bill → invoice
- Unit tests for billing rate resolution
- UI scenario: timesheet entry and billing

---

# Future Enhancements

- Milestone/fixed-fee billing
- Expense billing
- Mobile timesheet entry
- Payroll integration with timesheets

---

# Open Questions

1. Should timesheets support non-project activities (internal/admin time)?
2. Do we need timesheet reminders/notifications?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-022 — Projects, Tasks & Resource Planning
- PRD-024 — Employees, Contracts & Organization
- PRD-026 — Payroll Rules, Payslips & Compliance
