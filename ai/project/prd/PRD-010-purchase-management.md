---
id: PRD-010
title: Vendor Management & Purchase Price Lists
version: 1.0.0
status: IN_DEVELOPMENT
priority: High
owner: Product Manager
created: 2026-08-26
updated: 2026-08-26
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-010-vendor-management-price-lists
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
  - PRD-006 (Sales Quotation & Price Management v1.0.0)
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0)

related_tasks: []
related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED
  - PRD-003 must be COMPLETED (c_business_partner, m_product, c_uom)
  - PRD-006 must be COMPLETED (document sequence patterns)
  - Flyway must be temporarily enabled to execute the new migrations

change_log:
  - 1.0.0 — Initial PRD: Vendor management and purchase price lists
  - 1.0.0 APPROVED — User approved for implementation
---

# Executive Summary

This PRD establishes the supplier side of procurement. It adds vendor-specific fields to the business partner model and creates purchase price lists so that later purchase order lines can default vendor-specific prices.

This is the foundation for PRD-011 (Purchase Requisition & Purchase Order) and PRD-012 (Goods Receipt & Supplier Invoice).

---

# Problem Statement

**What problem are we solving?**

- The ERP cannot distinguish customers from suppliers on the business partner record
- There is no vendor-specific price list for procurement
- Purchase orders cannot auto-default prices from a vendor agreement

**Who experiences this problem?**

- Procurement officers
- AP clerks
- Warehouse receivers who need vendor context

**Why is this feature required?**

Vendor management and purchase pricing are prerequisites for any purchase-to-pay process.

---

# Business Goals

- Identify and manage supplier business partners
- Record vendor addresses, contacts, tax IDs, and payment terms
- Maintain vendor-specific purchase price lists with effective dates
- Deprecate hardcoded vendor lookup tables in the legacy `purchase/` module

---

# Functional Requirements

## FR-001 — Vendor Flag on Business Partner

Extend `c_business_partner` to support vendors.

- Add `is_vendor` boolean
- Add vendor category lookup
- Add default currency and payment term fields

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `is_vendor` column added to `c_business_partner`
- [ ] `[SE]` Vendor category reference added
- [ ] `[QA]` User can mark a business partner as vendor

---

## FR-002 — Vendor Window

Create a vendor-specific window using the runtime form engine.

- Header: code, name, tax ID, vendor category, payment term, default currency, status
- Tabs: addresses, contacts, bank accounts
- Filter to show only `is_vendor = true`

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` Vendor window registered in `sys_window`, `sys_tab`, `sys_window_field`
- [ ] `[SE]` Menu entry added under **Purchasing**
- [ ] `[QA]` Vendor CRUD works in runtime UI

---

## FR-003 — Purchase Price List Header

Create purchase price list header.

- Fields: name, vendor, currency, valid from, valid to, is_active

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pur_price_list` table created and registered
- [ ] `[SE]` Price list window seeded
- [ ] `[QA]` Header can be created

---

## FR-004 — Purchase Price List Lines

Create price list lines per product.

- Fields: product, UOM, minimum quantity, unit price, is_active
- Validation: only one active price per product/UOM/min-qty within a date range

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `pur_price_list_line` table created and registered
- [ ] `[SE]` Lines tab seeded under price list window
- [ ] `[SE]` Overlap validation implemented
- [ ] `[QA]` Lines can be added and validated

---

## FR-005 — Price Lookup Service

Backend service to resolve purchase price.

- Inputs: vendor, product, quantity, date
- Returns: matching unit price from active price list
- Falls back to product purchase price if no price list matches

**Priority:** High

**Acceptance Criteria**
- [ ] `[SE]` `PurchasePriceResolutionService` implemented
- [ ] `[SE]` Service returns correct price by quantity break
- [ ] `[QA]` Lookup returns expected price

---

# Non-Functional Requirements

- Tenant isolation on all vendor and price list records
- Price list overlap check performance < 200ms
- Soft-delete on all records

---

# User Stories

- As a procurement officer, I want to record vendor details so that I can raise POs to them.
- As a buyer, I want to set up vendor-specific prices so that POs default the agreed price.

---

# User Flow

```
Business Partner
  ↓
Mark as Vendor
  ↓
Create Purchase Price List
  ↓
Add Product Lines (qty breaks, prices)
  ↓
Used in Purchase Order (PRD-011)
```

---

# Scope

## Included

- Vendor flag and vendor window
- Vendor category
- Purchase price list header and lines
- Purchase price resolution service

## Excluded

- Purchase orders (PRD-011)
- Goods receipt (PRD-012)
- Vendor performance/scorecards
- Vendor contracts/blanket agreements

---

# UI / UX Requirements

- Vendor window mirrors customer window layout
- Price list window resembles sales price list UX from PRD-006
- Lines tab supports inline grid editing

---

# API Requirements

- Runtime window CRUD APIs for Vendor, Vendor Category, Purchase Price List
- `POST /api/v1/purchase/price-lists/resolve` — resolve unit price

All endpoints return `ApiResponse<T>`.

---

# Database Changes

## New Tables
- `pur_vendor_category`
- `pur_price_list`
- `pur_price_list_line`

## Modified Tables
- `c_business_partner` — add `is_vendor`, `vendor_category_id`, `default_purchase_currency`, `payment_term_id`

## Indexes
- `pur_price_list_line(product_id, pur_price_list_id)`

---

# Security Requirements

- Roles:
  - `VENDOR_VIEW`
  - `VENDOR_CREATE`
  - `PURCHASE_PRICE_LIST_VIEW`
  - `PURCHASE_PRICE_LIST_CREATE`
- Tenant isolation enforced

---

# Performance Requirements

- Price lookup < 200ms
- Vendor list < 1s for 10,000 records

---

# Logging Requirements

- Audit log for price list changes

---

# Edge Cases

- Vendor also a customer (`is_customer` and `is_vendor` both true)
- Overlapping price list dates
- Missing price list fallback

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Overlap validation performance | Low | Index by product and date range |

---

# Assumptions

- `c_business_partner` already has `is_customer` from PRD-007
- Address/contact tabs already exist

---

# Dependencies

- PRD-001, PRD-003, PRD-006, PRD-007
- PRD-011 depends on this PRD

---

# Acceptance Criteria

- [ ] Vendor flag and category implemented
- [ ] Vendor window seeded and functional
- [ ] Purchase price list header/lines implemented
- [ ] Price resolution service works
- [ ] Legacy vendor lookup tables deprecated

---

# Deployment Requirements

- Enable Flyway temporarily
- Seed menu and window access

---

# Testing Requirements

- Unit tests for price resolution
- UI test: create vendor + price list + resolve price

---

# Future Enhancements

- Vendor contracts
- Vendor evaluation
- Multi-currency price lists

---

# Open Questions

1. Do we need a separate vendor onboarding approval workflow?
2. Should price lists support multiple UOMs per product line?

---

# Change History

| Version | Date | Reason |
|---------|------|--------|
| 1.0.0 | 2026-08-26 | Initial draft |

---

# Related Documents

- PRD-011 — Purchase Requisition & Purchase Order
- PRD-012 — Goods Receipt & Supplier Invoice Matching
