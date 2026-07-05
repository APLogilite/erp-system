# AI Code Agent Prompt — M4 Accounting Foundation & Financial Integration

You are a principal ERP architect.

Your task is to implement the Accounting Foundation and Financial Integration layer.

IMPORTANT:

This is not a full accounting suite yet.

The goal is to establish:

✓ Chart of Accounts  
✓ Journal Entries  
✓ Financial Posting Engine  
✓ Document-to-Accounting Integration  
✓ General Ledger Foundation

Everything must remain metadata-driven and integrate with the Runtime Engine.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze  
✓ T1–T6 Frontend Runtime  
✓ B1–B5 Backend Runtime  
✓ M1 Foundation Modules  
✓ M2 Sales & Inventory Modules  
✓ M3 Purchasing & Advanced Inventory

Current Goal:

Build Accounting Foundation.

---

# TARGET OUTCOME

After M4:

✓ Chart of Accounts operational  
✓ Journal Entries operational  
✓ Posting Engine operational  
✓ Financial Integration operational  
✓ Accounting workflows operational  
✓ Ledger foundation operational

---

# MODULES

```txt
accounting
finance
```

Backend:

```txt
com.erp.modules.accounting
```

Frontend:

```txt
src/modules/accounting
```

---

# M4.1 — Chart of Accounts

Purpose:

Define all financial accounts.

---

## Account Fields

```txt
accountCode
name
description
accountType
parent
currency
isControlAccount
isActive
```

---

## Account Types

```txt
ASSET
LIABILITY
EQUITY
REVENUE
EXPENSE
```

---

## Relations

```txt
tree -> parent account
```

---

# M4.2 — Journal Entry

Purpose:

Financial transaction document.

---

## Header Fields

```txt
documentNo
documentDate
description
status
totalDebit
totalCredit
```

Relations:

```txt
one2many -> journal_lines
```

---

# M4.3 — Journal Entry Line

Fields:

```txt
lineNo
account
description
debit
credit
businessPartner
product
costCenter
```

Relations:

```txt
many2one -> account
many2one -> business_partner
many2one -> product
```

---

# Accounting Rule

```txt
Total Debit == Total Credit
```

Must always hold.

---

# M4.4 — Journal Workflow

States:

```txt
Draft
↓
Completed
↓
Posted
↓
Closed
```

Transitions:

```txt
COMPLETE
POST
CLOSE
REOPEN
VOID
```

---

# Validation Rules

Cannot post:

```txt
Debit != Credit
No lines
Invalid accounts
```

---

# M4.5 — Posting Engine

Create:

```txt
PostingEngine
```

Responsibilities:

```txt
post document
create journal entries
reverse entries
validate balances
publish events
```

---

# M4.6 — Financial Integration

The following documents must support posting:

---

## Sales Order

Future:

```txt
Revenue posting
Receivable posting
```

---

## Purchase Order

Future:

```txt
Expense posting
Payable posting
```

---

## Inventory Transaction

Future:

```txt
Inventory valuation posting
```

---

For now:

Create extension points only.

---

# M4.7 — Ledger Engine

Create:

```txt
GeneralLedgerService
```

Responsibilities:

```txt
calculate balances
account history
trial balance support
```

---

# M4.8 — Account Balance

Create:

```txt
AccountBalance
```

Fields:

```txt
account
period
openingBalance
debit
credit
closingBalance
```

---

# M4.9 — Financial Events

Publish:

```txt
journal.created
journal.posted
journal.reversed
account.balance.updated
```

---

# M4.10 — Runtime Rendering Validation

The following should render entirely from metadata:

```txt
Chart of Accounts Form
Journal Entry Form
Journal Entry Grid
Account Tree View
```

---

# M4.11 — Search Validation

Support:

```txt
account search
journal search
date filters
balance search
pagination
sorting
```

---

# M4.12 — Permissions

Roles:

---

## Accountant

```txt
Create Journal Entries
Post Journal Entries
```

---

## Finance Manager

```txt
Close Period
Reverse Entries
```

---

## Admin

```txt
Full Access
```

---

# M4.13 — Seed Data

Create:

```txt
Cash
Accounts Receivable
Inventory
Sales Revenue
Cost of Goods Sold
Accounts Payable
```

Sample Journal Entries.

---

# M4.14 — Acceptance Tests

## Chart of Accounts

Expected:

```txt
Tree hierarchy works.
```

---

## Journal Entry Form

Expected:

```txt
Rendered from metadata.
```

---

## Posting

Expected:

```txt
Balanced journal posted.
```

---

## Invalid Posting

Expected:

```txt
Validation failure.
```

---

## Account Balance

Expected:

```txt
Balances calculated correctly.
```

---

## Permissions

Expected:

```txt
Security enforced.
```

---

# SUCCESS CRITERIA

After M4:

```txt
Metadata
      ↓
Runtime Engine
      ↓
Accounting Foundation
      ↓
Financial Integration
```

The platform now supports:

✓ Master Data  
✓ Transactions  
✓ Inventory  
✓ Purchasing  
✓ Accounting Foundation

---

# FINAL DELIVERABLE

Produce:

✓ Chart of Accounts  
✓ Journal Entries  
✓ Posting Engine  
✓ Ledger Foundation  
✓ Account Balances  
✓ Financial Events  
✓ Metadata Definitions  
✓ Workflows  
✓ Permissions  
✓ End-to-End Validation
