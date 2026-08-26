---
id: PRD-018
title: Chart of Accounts, Tax & Fiscal Calendar
version: 1.0.0
status: DRAFT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-018-coa-tax-fiscal-calendar
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
  - PRD-004 (Window Hierarchy & Menu System v1.1.0)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-004 must be COMPLETED
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Chart of accounts, tax configuration, and fiscal calendar
---

# Executive Summary

This PRD establishes the accounting foundation: chart of accounts, account types, tax codes, and fiscal calendar. It is the prerequisite for general ledger posting (PRD-019) and AP/AR automation (PRD-020).

---

# Problem Statement

**What problem are we solving?**

- No chart of accounts
- No tax configuration
- No fiscal periods for reporting and period closing

**Who experiences this problem?**

- Accountants
- Controllers
- Tax managers

**Why is this feature required?**

Every financial transaction must post to accounts within a fiscal period.

---

# Business Goals

- Provide a multi-level chart of accounts
- Define account types (Asset, Liability, Equity, Revenue, Expense)
- Configure tax codes with rates and GL accounts
- Define fiscal years and periods
- Seed a standard chart of accounts

---

# Functional Requirements

## FR-001 — Account Type

Create account types.

- Types: Asset, Liability, Equity, Revenue, Expense, Cost of Goods Sold
- Fields: code, name, normal balance (Debit/Credit), is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_account_type` table created
- [ ] `[SE]` Account type window seeded
- [ ] `[QA]` Types can be maintained

---

## FR-002 — Chart of Accounts

Create account master.

- Fields: code, name, account type, parent account, is_summary, is_bank, is_active
- Tree structure supported

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_account` table created and registered
- [ ] `[SE]` Account window seeded
- [ ] `[SE]` Tree/list view supported
- [ ] `[QA]` Accounts can be created

---

## FR-003 — Tax Category

Group products and partners by tax treatment.

- Fields: code, name, is_active

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` `acc_tax_category` table created
- [ ] `[SE]` Tax category window seeded
- [ ] `[QA]` Categories can be maintained

---

## FR-004 — Tax Rate

Define tax rates.

- Fields: name, rate %, tax type (Sales/Purchase), sales account, purchase account, is_active
- Supports percentage tax

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_tax_rate` table created
- [ ] `[SE]` Tax rate window seeded
- [ ] `[QA]` Tax rates can be maintained

---

## FR-005 — Fiscal Year

Create fiscal years.

- Fields: name, start date, end date, status (Open/Closed), is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_fiscal_year` table created
- [ ] `[SE]` Fiscal year window seeded
- [ ] `[QA]` Fiscal years can be created

---

## FR-006 — Fiscal Period

Create periods within a fiscal year.

- Fields: fiscal year, period no, name, start date, end date, status (Open/Closed)
- Auto-generate 12 monthly periods

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_fiscal_period` table created
- [ ] `[SE]` Period window seeded
- [ ] `[SE]` Auto-generate periods action
- [ ] `[QA]` Periods can be generated and opened/closed

---

## FR-007 — Standard COA Seed

Seed a standard chart of accounts.

- Includes common accounts for cash, AR, AP, inventory, revenue, COGS, expenses

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Flyway seed migration created
- [ ] `[QA]` Standard accounts exist after migration

---

# Non-Functional Requirements

- Tenant-specific COA per tenant
- Period closing prevents posting to closed periods
- Soft-delete on accounts not allowed if posted

---

# User Stories

- As a controller, I want a chart of accounts so that transactions can be classified.
- As a tax manager, I want tax rates so that invoices calculate tax correctly.
- As an accountant, I want fiscal periods so that reporting is period-based.

---

# User Flow

```
Define Account Types
  ↓
Create Chart of Accounts
  ↓
Create Fiscal Year
  ↓
Generate Periods
  ↓
Configure Tax Rates
  ↓
Ready for GL Posting (PRD-019)
```

---

# Scope

## Included

- Account types
- Chart of accounts
- Tax categories and tax rates
- Fiscal years and periods
- Standard COA seed data

## Excluded

- General ledger postings (PRD-019)
- Journal entries (PRD-019)
- AP/AR automation (PRD-020)
- Bank reconciliation (PRD-021)
- Multi-currency accounting

---

# UI / UX Requirements

- Account tree view
- Fiscal period calendar view
- Tax rate list
- Menu group: **Accounting**

---

# API Requirements

- Runtime window CRUD for Account Type, Account, Tax Category, Tax Rate, Fiscal Year, Fiscal Period
- `POST /api/v1/accounting/fiscal-years/{id}/generate-periods`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `acc_account_type`
- `acc_account`
- `acc_tax_category`
- `acc_tax_rate`
- `acc_fiscal_year`
- `acc_fiscal_period`

## Relationships
- `acc_account.account_type_id` → `acc_account_type.id`
- `acc_account.parent_id` → `acc_account.id`
- `acc_tax_rate.sales_account_id` → `acc_account.id`
- `acc_tax_rate.purchase_account_id` → `acc_account.id`
- `acc_fiscal_period.fiscal_year_id` → `acc_fiscal_year.id`

---

# Security Requirements

- Roles:
  - `CHART_OF_ACCOUNTS_VIEW/CREATE`
  - `TAX_RATE_VIEW/CREATE`
  - `FISCAL_PERIOD_VIEW/CREATE`
- Tenant isolation

---

# Performance Requirements

- Account tree load < 500ms
- Period lookup < 100ms

---

# Logging Requirements

- Audit log for account changes
- Audit log for period open/close

---

# Edge Cases

- Account with existing journal entries cannot be deleted
- Overlapping fiscal periods
- Closing a period with unposted entries

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Wrong standard COA | Medium | User can customize after seed |

---

# Assumptions

- Single currency per tenant in v1.0
- Standard COA is a starting point

---

# Dependencies

- PRD-001, PRD-004
- PRD-019 depends on this PRD

---

# Acceptance Criteria

- [ ] Account type and account tables created
- [ ] Tax tables created
- [ ] Fiscal year/period tables created
- [ ] Standard COA seeded
- [ ] Period open/close works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed standard chart of accounts
- Seed menu and access

---

# Testing Requirements

- Unit tests for period generation
- UI scenario: create fiscal year → generate periods → add tax rate

---

# Future Enhancements

- Multi-currency accounts
- Account segments/dimensions
- Tax groups
- Period locking by module

---

# Open Questions

1. Which accounting standards should the standard COA follow (US GAAP, IFRS, local)?
2. Do we need multiple tax rates per tax category?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-019 — General Ledger & Journal Posting Engine
- PRD-020 — Accounts Payable & Receivable
