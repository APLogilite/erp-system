---
id: PRD-026
title: Payroll Rules, Payslips & Compliance
version: 1.0.0
status: DRAFT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-026-payroll-rules-payslips-compliance
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
  - PRD-001 (Dynamic Form Configuration System v1.0.0)
  - PRD-018 (Chart of Accounts, Tax & Fiscal Calendar)
  - PRD-019 (General Ledger & Journal Posting Engine)
  - PRD-021 (Bank, Cash & Payments Reconciliation)
  - PRD-023 (Timesheets & Project Billing)
  - PRD-024 (Employees, Contracts & Organization)
  - PRD-025 (Attendance, Leave & Expenses)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-018 must be COMPLETED
  - PRD-019 must be COMPLETED
  - PRD-021 must be COMPLETED (payments)
  - PRD-023 must be COMPLETED (optional: timesheet hours)
  - PRD-024 must be COMPLETED (employees, contracts)
  - PRD-025 must be COMPLETED (attendance, leave)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Payroll calculation, payslips, and compliance reporting
---

# Executive Summary

This PRD implements payroll: salary rules, pay runs, payslip generation, deductions, contributions, and compliance reporting. It uses employee contracts, attendance, and leave data to calculate pay.

---

# Problem Statement

**What problem are we solving?**

- No payroll calculation
- No payslip generation
- No statutory deductions or contributions

**Who experiences this problem?**

- Payroll officers
- HR managers
- Employees

**Why is this feature required?**

Payroll is the capstone HR function and is required for employee compensation.

---

# Business Goals

- Define configurable salary rules (earnings, deductions)
- Run payroll by period
- Generate payslips per employee
- Post payroll journal to GL
- Pay employees via bank/cash
- Provide basic payroll reports

---

# Functional Requirements

## FR-001 — Salary Rule Category

Group salary rules.

- Categories: Earnings, Deductions, Contributions, Net

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_salary_rule_category` table created
- [ ] `[SE]` Category window seeded
- [ ] `[QA]` Categories can be maintained

---

## FR-002 — Salary Rule

Define payroll calculation rules.

- Fields: code, name, category, sequence, condition, computation type (Fixed Amount / Percentage / Python/Expression), amount, percentage base, account (debit/credit), is_active
- Examples: Basic Salary, Tax, Insurance, Overtime

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_salary_rule` table created and registered
- [ ] `[SE]` Salary rule window seeded
- [ ] `[QA]` Rules can be maintained

---

## FR-003 — Salary Structure

Group rules into a structure.

- Fields: code, name, rules (many-to-many), is_active
- Assign structure to contract

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_salary_structure` and `hr_salary_structure_line` tables created
- [ ] `[SE]` Structure window seeded
- [ ] `[QA]` Structure can be assigned to contract

---

## FR-004 — Contract Salary Structure Assignment

Assign salary structure to employee contract.

- Fields on contract: salary structure, base salary

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Contract window updated
- [ ] `[QA]` Structure assigned correctly

---

## FR-005 — Payroll Run

Create a payroll run for a period.

- Fields: name, period start, period end, status (Draft → Confirmed → Paid → Closed)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_payroll_run` table created and registered
- [ ] `[SE]` Payroll run window seeded
- [ ] `[QA]` Payroll run can be created

---

## FR-006 — Payslip Generation

Generate payslips for employees in the run.

- Inputs: payroll run, employees, attendance/leave data
- Outputs: payslip header with lines per salary rule
- Payslip lines: rule, amount

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `hr_payslip` and `hr_payslip_line` tables created
- [ ] `[SE]` Payslip generation service implemented
- [ ] `[SE]` Rules evaluated per contract/attendance
- [ ] `[QA]` Payslip totals are correct

---

## FR-007 — Payslip Approval & Payment

Approve payslips and create payments.

- On approve: status → Approved
- On pay: create payment in PRD-021 and post payroll journal

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Approval workflow implemented
- [ ] `[SE]` Payment creation integrated
- [ ] `[SE]` Payroll journal posted to GL
- [ ] `[QA]` Employee payment reduces cash/bank and records salary expense

---

## FR-008 — Payroll Journal

Post payroll to GL.

- Debit: Salary expense accounts
- Credit: Tax payable, insurance payable, cash/bank

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Payroll journal generation implemented
- [ ] `[SE]` Journal entries posted
- [ ] `[QA]` Trial balance reflects payroll

---

## FR-009 — Payroll Reports

Provide payroll reports.

- Payslip report (printable)
- Payroll summary by department
- Deduction/contributions summary

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Report APIs implemented
- [ ] `[SE]` Report screens seeded
- [ ] `[QA]` Reports show correct data

---

# Non-Functional Requirements

- Payroll calculation idempotent within a run
- Tenant isolation
- Payslip data PII protected

---

# User Stories

- As a payroll officer, I want to configure salary rules so that payslips calculate correctly.
- As an HR manager, I want to run payroll so that employees are paid.
- As an employee, I want to view my payslip so that I understand my pay.

---

# User Flow

```
Configure Salary Rules
  ↓
Create Salary Structure
  ↓
Assign Structure to Contract
  ↓
Create Payroll Run
  ↓
Generate Payslips
  ↓
Approve and Pay
  ↓
Post Payroll Journal
```

---

# Scope

## Included

- Salary rule categories and rules
- Salary structures
- Contract structure assignment
- Payroll runs
- Payslip generation
- Payslip approval and payment
- Payroll journal posting
- Payroll reports

## Excluded

- Country-specific statutory compliance (tax tables)
- Advanced formula engine (Python/expression sandbox)
- Multi-country payroll
- Employee self-service portal for payslips

---

# UI / UX Requirements

- Salary rule window with computation type
- Payroll run window with generate/approve/pay actions
- Payslip view with earnings/deductions/net
- Payroll summary report grid

---

# API Requirements

- Runtime window CRUD for Salary Rule, Structure, Payroll Run, Payslip
- `POST /api/v1/hr/payroll-runs/{id}/generate-payslips`
- `POST /api/v1/hr/payslips/{id}/approve`
- `POST /api/v1/hr/payslips/{id}/pay`
- `POST /api/v1/hr/payroll-runs/{id}/post-journal`
- `GET /api/v1/hr/payroll-runs/{id}/summary`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `hr_salary_rule_category`
- `hr_salary_rule`
- `hr_salary_structure`
- `hr_salary_structure_line`
- `hr_payroll_run`
- `hr_payslip`
- `hr_payslip_line`

## Modified Tables
- `hr_contract` — add `salary_structure_id`, `base_salary`

## Relationships
- `hr_salary_rule.category_id` → `hr_salary_rule_category.id`
- `hr_salary_structure_line.structure_id` → `hr_salary_structure.id`
- `hr_salary_structure_line.rule_id` → `hr_salary_rule.id`
- `hr_contract.salary_structure_id` → `hr_salary_structure.id`
- `hr_payslip.payroll_run_id` → `hr_payroll_run.id`
- `hr_payslip.employee_id` → `hr_employee.id`
- `hr_payslip_line.payslip_id` → `hr_payslip.id`

---

# Security Requirements

- Roles:
  - `SALARY_RULE_VIEW/CREATE`
  - `PAYROLL_RUN_VIEW/CREATE`
  - `PAYSLIP_VIEW`
- PII access restricted
- Tenant isolation

---

# Performance Requirements

- Payslip generation < 3s for 100 employees
- Payroll summary < 2s

---

# Logging Requirements

- Audit log for payroll run status changes
- Audit log for payslip payments

---

# Edge Cases

- Employee with no contract
- Employee with no attendance
- Negative net pay
- Mid-period hire/termination

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Payroll calculation errors | High | Configurable rules; test payslips before confirm |

---

# Assumptions

- PRD-024/025 provide employee, contract, attendance, leave data
- PRD-019/021 provide GL and payment capability
- Simple computation engine in v1.0 (fixed amount / percentage of base)

---

# Dependencies

- PRD-001, PRD-018, PRD-019, PRD-021, PRD-023, PRD-024, PRD-025

---

# Acceptance Criteria

- [ ] Salary rule and structure tables created
- [ ] Payroll run and payslip tables created
- [ ] Payslip generation uses rules and attendance
- [ ] Payment creates PRD-021 payment and posts GL
- [ ] Payroll reports work

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed default salary rule categories and sample rules
- Configure payroll GL accounts
- Seed menu and access

---

# Testing Requirements

- Integration test: configure rules → run payroll → generate payslips → pay → post journal
- Unit tests for rule calculation
- UI scenario: generate and view payslip

---

# Future Enhancements

- Country-specific tax tables
- Expression-based salary rules
- Employee self-service payslip portal
- Year-end tax forms

---

# Open Questions

1. Which statutory deductions must be supported in v1.0?
2. Should payslip formulas be simple (fixed/percentage) or expression-based?
3. Do we need payroll integration with timesheets (billable hours pay)?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-023 — Timesheets & Project Billing
- PRD-024 — Employees, Contracts & Organization
- PRD-025 — Attendance, Leave & Expenses
