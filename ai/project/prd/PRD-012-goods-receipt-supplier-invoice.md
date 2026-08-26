---
id: PRD-012
title: Goods Receipt & Supplier Invoice Matching
version: 1.0.0
status: DRAFT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-012-goods-receipt-supplier-invoice
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
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0)
  - PRD-010 (Vendor Management & Purchase Price Lists)
  - PRD-011 (Purchase Requisition & Purchase Order)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED
  - PRD-007 must be COMPLETED
  - PRD-010 must be COMPLETED
  - PRD-011 must be COMPLETED (purchase orders exist)
  - Flyway must be temporarily enabled

change_log:
  - 1.0.0 — Initial PRD: Goods receipt and supplier invoice matching
---

# Executive Summary

This PRD completes the procure-to-pay cycle. It records goods receipts against purchase orders, updates inventory, and creates supplier invoices matched to receipts. This sets up accounts payable entries for PRD-020.

---

# Problem Statement

**What problem are we solving?**

- No way to record received goods from vendors
- No inventory update on receipt
- No supplier invoice document for AP

**Who experiences this problem?**

- Warehouse receivers
- Inventory managers
- AP clerks

**Why is this feature required?**

Receipts drive inventory. Invoices drive payments. Both must match the PO.

---

# Business Goals

- Record goods receipts with accepted/rejected quantities
- Update purchase order received quantities
- Create inventory stock moves on receipt completion
- Create supplier invoices matched to PO/receipt
- Validate invoice quantities against received quantities

---

# Functional Requirements

## FR-001 — Goods Receipt Header

Create receipts against a PO.

- Fields: receipt number, PO reference, vendor, receipt date, warehouse, status (Draft → Completed → Cancelled)
- Auto-populate lines from PO on selection

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_goods_receipt` table created and registered
- [ ] `[SE]` Receipt window seeded
- [ ] `[SE]` Document sequence `GR-XXXX` generated
- [ ] `[QA]` Receipt can be created from a PO

---

## FR-002 — Goods Receipt Lines

Record line-level receipt quantities.

- Fields: product, PO line reference, ordered qty, received qty, accepted qty, rejected qty, location
- Accepted qty updates inventory; rejected qty does not

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `m_goods_receipt_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[QA]` Lines accept/reject quantities correctly

---

## FR-003 — Receipt Completion

On complete:

- Update PO received quantities
- Create inventory stock move (inbound) for accepted qty
- Status → Completed

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Receipt completion service implemented
- [ ] `[SE]` PO received qty updated
- [ ] `[SE]` Inventory stock move created
- [ ] `[QA]` On-hand stock increases by accepted qty

---

## FR-004 — Supplier Invoice Header

Create supplier invoices.

- Fields: invoice number, vendor, invoice date, due date, PO reference, receipt reference, status (Draft → Validated → Paid)
- Auto-populate lines from receipt

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `ap_invoice` table created and registered
- [ ] `[SE]` Supplier invoice window seeded
- [ ] `[SE]` Document sequence `AP-XXXX` generated
- [ ] `[QA]` Invoice can be created from receipt

---

## FR-005 — Supplier Invoice Lines

Invoice line details.

- Fields: product, quantity, unit price, line total, tax
- Quantity cannot exceed received quantity (configurable override)

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `ap_invoice_line` table created and registered
- [ ] `[SE]` Lines tab seeded
- [ ] `[SE]` Validation against receipt qty implemented
- [ ] `[QA]` Over-invoicing is blocked

---

## FR-006 — Invoice Validation

On validate:

- Status → Validated
- Create AP entry (pending PRD-019/020)
- Lock invoice from editing

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Validation service implemented
- [ ] `[SE]` Invoice becomes read-only after validation
- [ ] `[QA]` Validation works end-to-end

---

# Non-Functional Requirements

- Receipt completion transactional
- Invoice validation idempotent
- Tenant isolation

---

# User Stories

- As a warehouse clerk, I want to receive goods against a PO so that inventory is accurate.
- As an AP clerk, I want to create an invoice from a receipt so that I can pay the vendor.
- As a manager, I want to see unmatched receipts to avoid missing invoices.

---

# User Flow

```
Purchase Order (Ordered)
  ↓
Create Goods Receipt
  ↓
Enter Accepted/Rejected Qty
  ↓
Complete Receipt → Inventory Updated
  ↓
Create Supplier Invoice from Receipt
  ↓
Validate Invoice → AP Entry
```

---

# Scope

## Included

- Goods receipt header and lines
- Receipt completion with inventory update
- Supplier invoice header and lines
- Invoice validation
- PO/receipt/invoice matching

## Excluded

- Outgoing payments (PRD-021)
- Full GL posting (PRD-019)
- Advanced three-way matching analytics
- Return to vendor (future)

---

# UI / UX Requirements

- Receipt window with "Receive All" shortcut
- Invoice window with PO/receipt lookup
- Status badges for matched/unmatched

---

# API Requirements

- Runtime window CRUD for Goods Receipt and Supplier Invoice
- `POST /api/v1/purchase/receipts/{id}/complete`
- `POST /api/v1/purchase/invoices/{id}/validate`
- `POST /api/v1/purchase/invoices/{id}/create-from-receipt/{receiptId}`

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `m_goods_receipt`
- `m_goods_receipt_line`
- `ap_invoice`
- `ap_invoice_line`

## Relationships
- `m_goods_receipt.order_id` → `pur_order.id`
- `m_goods_receipt_line.order_line_id` → `pur_order_line.id`
- `ap_invoice.order_id` → `pur_order.id`
- `ap_invoice.receipt_id` → `m_goods_receipt.id`

---

# Security Requirements

- Roles:
  - `GOODS_RECEIPT_CREATE`
  - `SUPPLIER_INVOICE_CREATE/VALIDATE`
- Tenant isolation

---

# Performance Requirements

- Receipt completion < 500ms
- Invoice validation < 200ms

---

# Logging Requirements

- Audit log for receipt completion
- Audit log for invoice validation

---

# Edge Cases

- Receive more than ordered
- Invoice before receipt
- Partial receipt then full invoice
- Cancel completed receipt

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Inventory update failure | High | Single transaction; rollback on error |

---

# Assumptions

- PRD-011 purchase orders exist
- Inventory location structure from PRD-013
- GL posting stub ready for PRD-019

---

# Dependencies

- PRD-001, PRD-003, PRD-007, PRD-010, PRD-011
- PRD-013 (Inventory) extends stock move posting
- PRD-019/020 use supplier invoices

---

# Acceptance Criteria

- [ ] Goods receipt tables and forms created
- [ ] Receipt completion updates PO and inventory
- [ ] Supplier invoice tables and forms created
- [ ] Invoice validation blocks over-invoicing
- [ ] End-to-end PO → Receipt → Invoice flow works

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed document sequences `GR`, `AP`
- Seed menu and access

---

# Testing Requirements

- Integration test: PO → Receipt → Invoice
- Unit tests for receipt completion
- UI scenario: receive goods and validate invoice

---

# Future Enhancements

- Three-way matching dashboard
- Return to vendor
- Landed costs

---

# Open Questions

1. Should receipt auto-create invoice, or remain manual?
2. Do we allow invoice before receipt in v1.0?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-010 — Vendor Management & Purchase Price Lists
- PRD-011 — Purchase Requisition & Purchase Order
- PRD-013 — Warehouse Structure & Location Management
- PRD-019 — General Ledger & Journal Posting Engine
- PRD-020 — Accounts Payable & Receivable
