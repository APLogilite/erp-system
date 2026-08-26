---
id: PRD-020
title: Accounts Payable & Receivable
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-020-accounts-payable-receivable
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
  - PRD-009 (Sales Return & Analytics v1.0.0)
  - PRD-012 (Goods Receipt & Supplier Invoice Matching)
  - PRD-018 (Chart of Accounts, Tax & Fiscal Calendar)
  - PRD-019 (General Ledger & Journal Posting Engine)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-007 must be COMPLETED (customer invoices)
  - PRD-009 must be COMPLETED (sales returns/credit memos)
  - PRD-012 must be COMPLETED (supplier invoices)
  - PRD-018 must be COMPLETED (COA, tax, periods)
  - PRD-019 must be COMPLETED (GL posting engine)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Accounts payable and receivable automation
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD automates accounts payable and accounts receivable. It generates GL postings from customer and supplier invoices, tracks open balances, and provides aging reports.

---

# Problem Statement

**What problem are we solving?**

- Customer and supplier invoices do not create GL entries
- No AP/AR balance tracking
- No aging visibility

**Who experiences this problem?**

- AR clerks
- AP clerks
- Controllers

**Why is this feature required?**

Invoices must drive the general ledger and cash management.

---

# Business Goals

- Post customer invoices to AR and revenue accounts
- Post supplier invoices to AP and expense/COGS accounts
- Track open invoice balances
- Generate AP/AR aging reports
- Support credit memos against invoices

---

# Functional Requirements

## FR-001 — Customer Invoice Posting

On customer invoice validation, create GL journal entry.

- Debit AR account
- Credit Revenue account
- Credit Tax payable account

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Customer invoice posting service implemented
- [ ] `[SE]` Journal entry created and posted
- [ ] `[QA]` GL balances update correctly

---

## FR-002 — Supplier Invoice Posting

On supplier invoice validation, create GL journal entry.

- Debit Expense/COGS/Inventory account
- Debit Tax receivable account
- Credit AP account

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Supplier invoice posting service implemented
- [ ] `[SE]` Journal entry created and posted
- [ ] `[QA]` GL balances update correctly

---

## FR-003 — Credit Memo Posting

Post customer and supplier credit memos.

- Reverse the corresponding invoice accounts
- Link to original invoice

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Credit memo posting service implemented
- [ ] `[SE]` Reversing entries created
- [ ] `[QA]` Credit memo reduces invoice balance

---

## FR-004 — AR/AP Open Balance Tracking

Track open amount per invoice.

- Fields: invoice amount, paid amount, credit applied, open amount
- Updated on payment or credit memo

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Open balance fields added to invoices
- [ ] `[SE]` Balance update service implemented
- [ ] `[QA]` Open amount is accurate

---

## FR-005 — AR Aging Report

Report customer open balances by aging bucket.

- Buckets: Current, 1-30, 31-60, 61-90, 90+
- Group by customer

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` AR aging query implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Aging buckets are correct

---

## FR-006 — AP Aging Report

Report vendor open balances by aging bucket.

- Buckets: Current, 1-30, 31-60, 61-90, 90+
- Group by vendor

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` AP aging query implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Aging buckets are correct

---

## FR-007 — Invoice Allocation

Apply credit memos and payments to invoices.

- Allocate partial or full amounts
- Update open balance

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Allocation service implemented
- [ ] `[SE]` Open balances updated
- [ ] `[QA]` Allocation reduces open amount

---

# Non-Functional Requirements

- Posting transactional with invoice validation
- Tenant isolation
- Aging report < 2s

---

# User Stories

- As an AR clerk, I want customer invoices to post to GL so that revenue is recognized.
- As an AP clerk, I want supplier invoices to post to GL so that expenses are recorded.
- As a controller, I want aging reports so that I can manage collections and payments.

---

# User Flow

```
Customer/Supplier Invoice Validated
  ↓
Generate Journal Entry
  ↓
Post to GL
  ↓
Track Open Balance
  ↓
Payment or Credit Memo Applied
  ↓
Aging Report
```

---

# Scope

## Included

- Customer invoice posting
- Supplier invoice posting
- Credit memo posting
- Open balance tracking
- AR/AP aging reports
- Invoice allocation framework

## Excluded

- Outgoing/incoming payment recording (PRD-021)
- Bank reconciliation (PRD-021)
- Dunning letters/collections automation
- Vendor payment proposals

---

# UI / UX Requirements

- Aging report grids for AR and AP
- Invoice balance fields visible on invoice windows
- GL entry link on posted invoices

---

# API Requirements

- `POST /api/v1/accounting/ar/invoices/{id}/post`
- `POST /api/v1/accounting/ap/invoices/{id}/post`
- `GET /api/v1/accounting/ar/aging?asOfDate={date}`
- `GET /api/v1/accounting/ap/aging?asOfDate={date}`
- `POST /api/v1/accounting/invoices/{id}/allocate`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## Modified Tables
- `ar_invoice` — add `posted`, `journal_entry_id`, `open_amount`, `paid_amount`
- `ap_invoice` — add `posted`, `journal_entry_id`, `open_amount`, `paid_amount`

## New Tables
- `acc_invoice_allocation` (links payments/credits to invoices)

## Relationships
- `ar_invoice.journal_entry_id` → `acc_journal_entry.id`
- `ap_invoice.journal_entry_id` → `acc_journal_entry.id`

---

# Security Requirements

- Roles:
  - `AR_INVOICE_POST`
  - `AP_INVOICE_POST`
  - `AR_AGING_VIEW`
  - `AP_AGING_VIEW`
- Tenant isolation

---

# Performance Requirements

- Invoice posting < 500ms
- Aging report < 2s

---

# Logging Requirements

- Audit log for invoice posting
- Audit log for allocations

---

# Edge Cases

- Invoice posted to closed period
- Credit memo exceeds invoice amount
- Payment allocation over open amount

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Incorrect GL account mapping | High | Configurable account mapping per document type |

---

# Assumptions

- PRD-018/019 provide COA and GL engine
- Customer/supplier invoices exist from PRD-007/012
- Single currency

---

# Dependencies

- PRD-001, PRD-007, PRD-009, PRD-012, PRD-018, PRD-019
- PRD-021 depends on this PRD

---

# Acceptance Criteria

- [ ] Customer invoice posting creates GL entries
- [ ] Supplier invoice posting creates GL entries
- [ ] Credit memos post correctly
- [ ] Open balances tracked
- [ ] AR/AP aging reports work

---

# Deployment Requirements

- Enable Flyway temporarily
- Configure GL account mappings
- Seed menu and access

---

# Testing Requirements

- Integration test: validate invoice → post → trial balance
- Unit tests for aging calculation
- UI scenario: view AR aging

---

# Future Enhancements

- Payment proposals
- Dunning management
- Vendor 1099 reporting
- Multi-currency AR/AP

---

# Open Questions

1. Should invoice posting happen automatically on validation or via a separate post action?
2. Do we need separate AR/AP control accounts per branch?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-018 — Chart of Accounts, Tax & Fiscal Calendar
- PRD-019 — General Ledger & Journal Posting Engine
- PRD-021 — Bank, Cash & Payments Reconciliation
