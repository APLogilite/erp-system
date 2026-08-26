---
id: PRD-019
title: General Ledger & Journal Posting Engine
version: 1.0.0
status: DRAFT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-019-general-ledger-journal-posting
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
  - PRD-018 (Chart of Accounts, Tax & Fiscal Calendar)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-018 must be COMPLETED (chart of accounts, fiscal periods)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: General ledger and journal posting engine
---

# Executive Summary

This PRD implements the general ledger: journal entries, ledger lines, debit/credit balancing, period validation, and posting from source documents. It is the central accounting engine for AP, AR, inventory, and manufacturing transactions.

---

# Problem Statement

**What problem are we solving?**

- No general ledger to record financial transactions
- No double-entry accounting
- No audit trail from source documents to GL

**Who experiences this problem?**

- Accountants
- Controllers
- Auditors

**Why is this feature required?**

The GL is the core of financial reporting and compliance.

---

# Business Goals

- Support double-entry journal entries
- Validate debits = credits
- Prevent posting to closed periods
- Provide journal entry approval workflow
- Generate GL entries from source documents (invoices, receipts, payments)
- Provide trial balance report

---

# Functional Requirements

## FR-001 — Journal Entry Header

Create journal entries.

- Fields: entry number, entry date, fiscal period, reference, description, status (Draft → Posted → Reversed), source document
- Entry date must fall in an open period

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_journal_entry` table created and registered
- [ ] `[SE]` Journal entry window seeded
- [ ] `[SE]` Document sequence `JE-XXXX` generated
- [ ] `[QA]` Entry can be created

---

## FR-002 — Journal Entry Lines

Add debit/credit lines.

- Fields: account, debit amount, credit amount, description, cost center/dimension (optional)
- Total debits must equal total credits before posting

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_journal_entry_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[SE]` Balance validation implemented
- [ ] `[QA]` Unbalanced entry cannot be posted

---

## FR-003 — Posting Engine

Post journal entries.

- On post:
  - Validate period is open
  - Validate debits = credits
  - Create ledger postings
  - Status → Posted
  - Lock entry from editing

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Posting service implemented
- [ ] `[SE]` Period validation enforced
- [ ] `[SE]` Balanced validation enforced
- [ ] `[QA]` Balanced entry in open period posts successfully

---

## FR-004 — Ledger Postings Table

Immutable ledger records.

- Fields: journal entry, journal entry line, account, fiscal period, posting date, debit, credit, balance
- Once posted, cannot be edited (only reversed)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_ledger_posting` table created
- [ ] `[SE]` Postings created on post
- [ ] `[QA]` Ledger balances update

---

## FR-005 — Source Document Link

Link GL entries to source documents.

- Fields: source_type (Invoice, Receipt, Payment, Adjustment), source_id, source_line_id
- Enables drill-down from GL to source

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Source document fields added
- [ ] `[SE]` Links stored on ledger postings
- [ ] `[QA]` Drill-down metadata present

---

## FR-006 — Trial Balance Report

Report account balances by period.

- Columns: account code, account name, opening debit/credit, period debit/credit, closing balance
- Filter by fiscal period

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Trial balance query implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Report balances are correct

---

## FR-007 — Reversal

Allow reversal of posted entries.

- Creates a reversing journal entry on a specified date
- Original entry marked as Reversed

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Reversal service implemented
- [ ] `[SE]` Reversing entry created
- [ ] `[QA]` Reversal clears original balances

---

# Non-Functional Requirements

- Posting must be atomic
- Ledger postings immutable
- Tenant isolation
- Period closing enforced

---

# User Stories

- As an accountant, I want to post journal entries so that financial transactions are recorded.
- As a controller, I want a trial balance so that I can review account balances.
- As an auditor, I want source document links so that I can trace transactions.

---

# User Flow

```
Chart of Accounts + Fiscal Periods
  ↓
Create Journal Entry
  ↓
Add Debit/Credit Lines
  ↓
Validate Balance
  ↓
Post → Ledger Postings
  ↓
Trial Balance Report
```

---

# Scope

## Included

- Journal entry header and lines
- Posting engine
- Ledger postings table
- Source document links
- Trial balance report
- Reversal

## Excluded

- AP/AR automation (PRD-020)
- Bank reconciliation (PRD-021)
- Financial statements (P&L, Balance Sheet) — can be built from trial balance
- Multi-currency GL
- Budgeting

---

# UI / UX Requirements

- Journal entry window with debit/credit lines tab
- Post button with validation feedback
- Trial balance grid
- Source document drill-down links

---

# API Requirements

- Runtime window CRUD for Journal Entry
- `POST /api/v1/accounting/journal-entries/{id}/post`
- `POST /api/v1/accounting/journal-entries/{id}/reverse`
- `GET /api/v1/accounting/trial-balance?periodId={id}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `acc_journal_entry`
- `acc_journal_entry_line`
- `acc_ledger_posting`

## Relationships
- `acc_journal_entry.period_id` → `acc_fiscal_period.id`
- `acc_journal_entry_line.journal_entry_id` → `acc_journal_entry.id`
- `acc_journal_entry_line.account_id` → `acc_account.id`
- `acc_ledger_posting.journal_entry_line_id` → `acc_journal_entry_line.id`
- `acc_ledger_posting.account_id` → `acc_account.id`
- `acc_ledger_posting.period_id` → `acc_fiscal_period.id`

---

# Security Requirements

- Roles:
  - `JOURNAL_ENTRY_VIEW/CREATE`
  - `JOURNAL_ENTRY_POST`
  - `TRIAL_BALANCE_VIEW`
- Tenant isolation

---

# Performance Requirements

- Posting < 500ms
- Trial balance < 2s for 1M postings

---

# Logging Requirements

- Audit log for posting
- Audit log for reversals

---

# Edge Cases

- Posting to closed period
- Unbalanced entry
- Reversal of already reversed entry
- Entry with zero amount lines

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Data integrity in ledger | Critical | Immutable postings; transactions; no direct edits |

---

# Assumptions

- Single currency per tenant
- PRD-018 COA and periods exist

---

# Dependencies

- PRD-001, PRD-018
- PRD-020 depends on this PRD

---

# Acceptance Criteria

- [ ] Journal entry tables created
- [ ] Posting engine validates balance and period
- [ ] Ledger postings created
- [ ] Trial balance report correct
- [ ] Reversal works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequence `JE`
- Seed menu and access

---

# Testing Requirements

- Unit tests for balance validation
- Integration test: create entry → post → trial balance
- UI scenario: post journal entry and view trial balance

---

# Future Enhancements

- Multi-currency postings
- Budget control
- Allocation journals
- Recurring journals
- Financial statement builder

---

# Open Questions

1. Do we require journal entry approval before posting?
2. Should manual entries and system-generated entries use different document types?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-018 — Chart of Accounts, Tax & Fiscal Calendar
- PRD-020 — Accounts Payable & Receivable
- PRD-021 — Bank, Cash & Payments Reconciliation
