---
id: PRD-025
title: Attendance, Leave & Expenses
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-025-attendance-leave-expenses
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
  - PRD-024 (Employees, Contracts & Organization)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-024 must be COMPLETED (employees and contracts exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Attendance tracking, leave management, and employee expenses
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD adds attendance tracking, leave requests, and employee expense claims. These feed into payroll (PRD-026) for pay calculation and deductions.

---

# Problem Statement

**What problem are we solving?**

- No attendance records
- No leave balance tracking
- No employee expense reimbursement

**Who experiences this problem?**

- Employees
- HR managers
- Payroll officers

**Why is this feature required?**

Attendance and leave affect payroll; expenses need reimbursement.

---

# Business Goals

- Record daily attendance (present, absent, late)
- Manage leave types and balances
- Process leave requests with approval
- Submit and approve employee expenses
- Integrate approved data with payroll

---

# Functional Requirements

## FR-001 — Leave Type

Define leave types.

- Fields: code, name, paid flag, default days per year, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_leave_type` table created
- [ ] `[SE]` Leave type window seeded
- [ ] `[QA]` Leave types can be maintained

---

## FR-002 — Employee Leave Balance

Track leave balances per employee and type.

- Fields: employee, leave type, allocated days, used days, pending days, available days

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_leave_balance` table created
- [ ] `[SE]` Balance window/field seeded
- [ ] `[QA]` Balance updates on leave approval

---

## FR-003 — Leave Request

Submit and approve leave.

- Fields: employee, leave type, start date, end date, days, reason, status (Draft → Submitted → Approved → Rejected → Cancelled)
- On approval: update leave balance

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_leave_request` table created and registered
- [ ] `[SE]` Leave request window seeded
- [ ] `[SE]` Approval workflow implemented
- [ ] `[QA]` Approved leave reduces available balance

---

## FR-004 — Attendance Entry

Record daily attendance.

- Fields: employee, date, check-in, check-out, status (Present/Absent/Late/On Leave), work hours
- Bulk import option (future)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_attendance` table created and registered
- [ ] `[SE]` Attendance window seeded
- [ ] `[QA]` Attendance can be recorded

---

## FR-005 — Attendance Summary

Compute monthly attendance summary.

- Fields: present days, absent days, late days, leave days, work hours

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Attendance summary query implemented
- [ ] `[SE]` Summary report seeded
- [ ] `[QA]` Summary is accurate

---

## FR-006 — Expense Category

Categorize expenses.

- Fields: code, name, reimbursable flag, default account, is_active

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `hr_expense_category` table created
- [ ] `[SE]` Expense category window seeded
- [ ] `[QA]` Categories can be maintained

---

## FR-007 — Expense Claim

Submit employee expenses.

- Header: employee, claim date, status (Draft → Submitted → Approved → Rejected → Paid), total amount
- Lines: date, category, description, amount, receipt attachment reference

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_expense_claim` and `hr_expense_claim_line` tables created
- [ ] `[SE]` Expense claim window seeded
- [ ] `[SE]` Approval workflow implemented
- [ ] `[QA]` Claim can be submitted and approved

---

## FR-008 — Expense Reimbursement

Reimburse approved expenses.

- On reimbursement: create payment in PRD-021 (or payroll deduction offset)
- Mark claim as Paid

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Reimbursement service implemented
- [ ] `[SE]` Claim status updated to Paid
- [ ] `[QA]` Reimbursement creates payment record

---

# Non-Functional Requirements

- Tenant isolation
- Leave balance calculations accurate
- Attendance summary performant

---

# User Stories

- As an employee, I want to request leave so that I can plan time off.
- As an HR manager, I want to track attendance so that payroll is accurate.
- As an employee, I want to claim expenses so that I can be reimbursed.

---

# User Flow

```
Employee
  ↓
Log Attendance / Request Leave / Submit Expense
  ↓
Manager/HR Approves
  ↓
Balance/Payroll Updated
```

---

# Scope

## Included

- Leave types and balances
- Leave requests and approval
- Attendance entry
- Attendance summary
- Expense categories and claims
- Expense reimbursement

## Excluded

- Shift/roster planning
- Biometric integration
- Travel requests
- Payroll calculation (PRD-026)

---

# UI / UX Requirements

- My leave balance widget
- Leave request calendar
- Attendance entry grid
- Expense claim with line totals

---

# API Requirements

- Runtime window CRUD for Leave Type, Leave Request, Attendance, Expense Category, Expense Claim
- `POST /api/v1/hr/leave-requests/{id}/approve`
- `POST /api/v1/hr/leave-requests/{id}/reject`
- `POST /api/v1/hr/expense-claims/{id}/approve`
- `POST /api/v1/hr/expense-claims/{id}/reimburse`
- `GET /api/v1/hr/attendance/summary?employeeId={id}&month={month}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `hr_leave_type`
- `hr_leave_balance`
- `hr_leave_request`
- `hr_attendance`
- `hr_expense_category`
- `hr_expense_claim`
- `hr_expense_claim_line`

## Relationships
- `hr_leave_balance.employee_id` → `hr_employee.id`
- `hr_leave_request.employee_id` → `hr_employee.id`
- `hr_attendance.employee_id` → `hr_employee.id`
- `hr_expense_claim.employee_id` → `hr_employee.id`

---

# Security Requirements

- Roles:
  - `LEAVE_VIEW/CREATE/APPROVE`
  - `ATTENDANCE_VIEW/CREATE`
  - `EXPENSE_VIEW/CREATE/APPROVE`
- Users see own data unless HR/approver
- Tenant isolation

---

# Performance Requirements

- Attendance summary < 1s
- Leave balance lookup < 100ms

---

# Logging Requirements

- Audit log for leave approvals
- Audit log for expense approvals

---

# Edge Cases

- Leave request exceeds balance
- Overlapping leave requests
- Expense claim with no receipt
- Attendance on holiday

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Negative leave balance | Medium | Validation before approval |

---

# Assumptions

- PRD-024 employees exist
- Work calendar (weekends/holidays) defined at tenant level or simple in v1.0

---

# Dependencies

- PRD-001, PRD-024
- PRD-021 for expense reimbursement payments
- PRD-026 depends on this PRD

---

# Acceptance Criteria

- [ ] Leave type/balance/request tables created
- [ ] Attendance table created
- [ ] Expense claim tables created
- [ ] Approval workflows work
- [ ] Summary and balance queries work

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed default leave types
- Seed menu and access

---

# Testing Requirements

- Integration test: leave request → approval → balance update
- UI scenario: submit expense claim and approve

---

# Future Enhancements

- Shift/roster planning
- Holiday calendar
- Biometric attendance import
- Approval workflows by department

---

# Open Questions

1. Do we need a holiday calendar in v1.0?
2. Should expense claims support multi-currency?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-024 — Employees, Contracts & Organization
- PRD-026 — Payroll Rules, Payslips & Compliance
