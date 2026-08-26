---
id: PRD-021
title: Bank, Cash & Payments Reconciliation
version: 1.0.0
status: DRAFT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-021-bank-cash-payments-reconciliation
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
  - PRD-019 (General Ledger & Journal Posting Engine)
  - PRD-020 (Accounts Payable & Receivable)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-018 must be COMPLETED
  - PRD-019 must be COMPLETED
  - PRD-020 must be COMPLETED (AR/AP open balances)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Bank accounts, cash journals, payments, and reconciliation
---

# Executive Summary

This PRD adds bank accounts, cash journals, customer receipts, vendor payments, and bank reconciliation. It completes the cash management cycle and links payments to AR/AP invoices.

---

# Problem Statement

**What problem are we solving?**

- No bank or cash accounts
- No customer receipts or vendor payments
- No bank reconciliation

**Who experiences this problem?**

- Cashiers
- AR/AP clerks
- Accountants

**Why is this feature required?**

Cash transactions close the AR/AP cycle and must be reconciled against bank statements.

---

# Business Goals

- Define bank accounts and cash journals
- Record customer receipts against AR invoices
- Record vendor payments against AP invoices
- Post payments to GL
- Reconcile bank statements with system transactions

---

# Functional Requirements

## FR-001 — Bank Account

Create bank accounts.

- Fields: name, bank name, account number, GL account, currency, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_bank_account` table created
- [ ] `[SE]` Bank account window seeded
- [ ] `[QA]` Bank accounts can be maintained

---

## FR-002 — Cash Journal

Create cash journals.

- Fields: name, GL account, currency, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_cash_journal` table created
- [ ] `[SE]` Cash journal window seeded
- [ ] `[QA]` Cash journals can be maintained

---

## FR-003 — Customer Receipt

Record incoming customer payments.

- Header: receipt number, customer, receipt date, bank account/cash journal, amount, status
- Lines: invoice, amount applied
- On complete: post GL entry (Debit Bank/Cash, Credit AR)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `ar_receipt` and `ar_receipt_line` tables created
- [ ] `[SE]` Receipt window seeded
- [ ] `[SE]` Receipt completion posts GL and updates invoice open balance
- [ ] `[QA]` Customer receipt reduces AR balance

---

## FR-004 — Vendor Payment

Record outgoing vendor payments.

- Header: payment number, vendor, payment date, bank account/cash journal, amount, status
- Lines: invoice, amount applied
- On complete: post GL entry (Debit AP, Credit Bank/Cash)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `ap_payment` and `ap_payment_line` tables created
- [ ] `[SE]` Payment window seeded
- [ ] `[SE]` Payment completion posts GL and updates invoice open balance
- [ ] `[QA]` Vendor payment reduces AP balance

---

## FR-005 — Bank Statement

Import or enter bank statement lines.

- Header: bank account, statement date, starting balance, ending balance
- Lines: date, reference, description, amount (debit/credit), reconciled flag

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `acc_bank_statement` and `acc_bank_statement_line` tables created
- [ ] `[SE]` Bank statement window seeded
- [ ] `[QA]` Statement lines can be entered

---

## FR-006 — Bank Reconciliation

Match statement lines to system payments/receipts.

- Auto-match by reference/amount
- Manual match UI
- On reconcile: mark both statement line and payment reconciled

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Reconciliation service implemented
- [ ] `[SE]` Auto-match logic implemented
- [ ] `[QA]` Matched transactions marked reconciled

---

## FR-007 — Cash Position Report

Show bank/cash balances and unreconciled items.

- Filter by account and date

**Priority:** Medium

**Acceptance Criteria**
- [ ] `[SE]` Cash position query implemented
- [ ] `[SE]` Report window/screen seeded
- [ ] `[QA]` Report shows correct balances

---

# Non-Functional Requirements

- Payment posting transactional
- Reconciliation idempotent
- Tenant isolation

---

# User Stories

- As a cashier, I want to record customer receipts so that AR balances are reduced.
- As an AP clerk, I want to record vendor payments so that AP balances are reduced.
- As an accountant, I want to reconcile bank statements so that cash balances are accurate.

---

# User Flow

```
Bank Account / Cash Journal
  ↓
Customer Receipt / Vendor Payment
  ↓
Post to GL
  ↓
Import Bank Statement
  ↓
Reconcile Lines
  ↓
Cash Position Report
```

---

# Scope

## Included

- Bank accounts and cash journals
- Customer receipts
- Vendor payments
- Bank statement import/entry
- Bank reconciliation
- Cash position report

## Excluded

- Check printing
- Electronic payment integration
- Multi-currency bank reconciliation
- Cash flow forecasting

---

# UI / UX Requirements

- Bank statement lines grid with match status
- Reconciliation screen: statement lines vs system transactions
- Receipt/payment windows with invoice allocation lines

---

# API Requirements

- Runtime window CRUD for Bank Account, Cash Journal, Receipt, Payment, Bank Statement
- `POST /api/v1/accounting/receipts/{id}/complete`
- `POST /api/v1/accounting/payments/{id}/complete`
- `POST /api/v1/accounting/bank-statements/{id}/reconcile`
- `GET /api/v1/accounting/cash-position?accountId={id}&date={date}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `acc_bank_account`
- `acc_cash_journal`
- `ar_receipt`
- `ar_receipt_line`
- `ap_payment`
- `ap_payment_line`
- `acc_bank_statement`
- `acc_bank_statement_line`

## Relationships
- `ar_receipt.customer_id` → `c_business_partner.id`
- `ar_receipt_line.invoice_id` → `ar_invoice.id`
- `ap_payment.vendor_id` → `c_business_partner.id`
- `ap_payment_line.invoice_id` → `ap_invoice.id`
- `acc_bank_statement_line.bank_statement_id` → `acc_bank_statement.id`

---

# Security Requirements

- Roles:
  - `RECEIPT_CREATE`
  - `PAYMENT_CREATE`
  - `BANK_RECONCILIATION_CREATE`
  - `CASH_POSITION_VIEW`
- Tenant isolation

---

# Performance Requirements

- Reconciliation auto-match < 2s
- Cash position report < 1s

---

# Logging Requirements

- Audit log for payments and receipts
- Audit log for reconciliation

---

# Edge Cases

- Over-payment of invoice
- Payment in different currency
- Unmatched statement lines
- Reconciling a reversed payment

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Bank statement import formats | Medium | Support manual entry first; CSV import later |

---

# Assumptions

- Single currency
- PRD-020 AR/AP balances exist

---

# Dependencies

- PRD-001, PRD-018, PRD-019, PRD-020

---

# Acceptance Criteria

- [ ] Bank/cash accounts created
- [ ] Customer receipts post to GL and reduce AR
- [ ] Vendor payments post to GL and reduce AP
- [ ] Bank reconciliation works
- [ ] Cash position report accurate

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequences `REC`, `PAY`, `STMT`
- Configure bank GL accounts
- Seed menu and access

---

# Testing Requirements

- Integration test: invoice → receipt → GL → reconciliation
- Unit tests for auto-match
- UI scenario: reconcile bank statement

---

# Future Enhancements

- Check printing
- Electronic bank feeds
- Multi-currency payments
- Payment batches

---

# Open Questions

1. Do we need CSV import for bank statements in v1.0?
2. Should receipts/payments support unapplied (on-account) amounts?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-018 — Chart of Accounts, Tax & Fiscal Calendar
- PRD-019 — General Ledger & Journal Posting Engine
- PRD-020 — Accounts Payable & Receivable
