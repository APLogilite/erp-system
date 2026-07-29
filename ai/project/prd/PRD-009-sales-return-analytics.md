---
id: PRD-009
title: Sales Return & Analytics
version: 1.0.0
status: DRAFT
priority: Medium
owner: Product Manager
created: 2026-07-29
updated: 2026-07-29
approved_by:
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-009-sales-return-analytics
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
  - PRD-001 (Dynamic Form Configuration System v1.6.0) — runtime engine dependency
  - PRD-003 (ERP Order Flow Forms v1.0.0) — provides tx_invoice, tx_shipment base
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0) — order workflow

related_tasks: []

related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED (runtime engine and metadata tables exist)
  - PRD-003 must be COMPLETED (tx_invoice, tx_shipment base tables exist)
  - PRD-007 must be COMPLETED (order workflow, customer 360)
  - Flyway must be temporarily enabled to execute the new migrations

change_log:
  - 1.0.0 — Initial PRD: Sales Return (RMA), Credit Memo, Sales Analytics

---

# Executive Summary

This PRD adds **Sales Return (RMA)** processing, **Credit Memo** generation, and **Sales Analytics** reporting.

In iDempiere and Odoo, a sales return allows customers to return products for refund or replacement. The process involves: Return Authorization → Goods Receipt → Credit Memo → Refund.

The **Sales Analytics** dashboard provides insights into sales performance: revenue by period, top customers, top products, conversion rates.

---

# Problem Statement

**What problem are we solving?**

Currently, the ERP has no way to handle product returns:
- No Return Merchandise Authorization (RMA) process
- No credit memo generation (for refunds)
- No return inventory tracking (returned goods must go back to stock)
- No sales analytics (revenue trends, top customers, top products)

**Who experiences this problem?**

- **Customer Service** — cannot process returns, must manually issue refunds
- **Warehouse** — cannot track returned goods back into inventory
- **Finance** — cannot generate credit memos for refunds
- **Sales Managers** — no visibility into sales trends, top performers

**Why is this feature required?**

Returns are a normal part of business. Without an RMA process, returns are handled manually, leading to errors, lost inventory, and unhappy customers. Sales analytics are essential for business decision-making.

---

# Business Goals

1. Implement Return Merchandise Authorization (RMA) process
2. Generate Credit Memos for refunds
3. Track returned goods back into inventory
4. Provide Sales Analytics dashboard (revenue, top customers, top products)
5. All forms metadata-driven

---

# Functional Requirements

## FR-001: Sales Return (RMA) Entity

**Description:** A return authorization allows customers to return products. Contains line items with products, quantities, and return reasons.

**Priority:** High

**Acceptance Criteria:**

### tx_return table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| return_number | string (50) | ✓ | Auto-generated (RMA-0001) |
| return_date | date | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | Customer |
| order_id | many2one → tx_order | | Original order |
| invoice_id | many2one → tx_invoice | | Original invoice |
| reason | enum: defective / wrong_item / not_needed / damaged / other | ✓ | |
| status | enum: requested / approved / received / credited / rejected | ✓ | |
| subtotal | decimal (15,2) | | |
| tax_amount | decimal (15,2) | | |
| credit_amount | decimal (15,2) | | |
| notes | text | | |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### tx_return_line table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| return_id | many2one → tx_return | ✓ | Parent return |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| quantity | decimal (15,3) | ✓ | |
| uom_id | many2one → md_uom | | |
| unit_price | decimal (15,2) | ✓ | |
| line_total | decimal (15,2) | | |
| reason | text | | Line-specific reason |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Status Workflow

```
REQUESTED → APPROVED → RECEIVED → CREDITED
     ↓          ↓
  REJECTED   REJECTED
```

---

## FR-002: Credit Memo

**Description:** A credit memo is a negative invoice that reduces the customer's balance. Generated from an approved return.

**Priority:** High

**Acceptance Criteria:**

### tx_credit_memo table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| credit_memo_number | string (50) | ✓ | Auto-generated (CM-0001) |
| credit_memo_date | date | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | Customer |
| return_id | many2one → tx_return | | Source return |
| invoice_id | many2one → tx_invoice | | Original invoice |
| currency | string (3) | | |
| subtotal | decimal (15,2) | | |
| tax_amount | decimal (15,2) | | |
| credit_amount | decimal (15,2) | ✓ | |
| status | enum: draft / validated / applied / cancelled | ✓ | |
| notes | text | | |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Application

- Credit memo can be applied to open invoices (reduces due_amount)
- Or refunded to customer (creates payment with negative amount)

---

## FR-003: Return Inventory Receipt

**Description:** When returned goods arrive at the warehouse, they must be received back into inventory.

**Priority:** Medium

**Acceptance Criteria:**

- Link `tx_return` to `tx_material_receipt` (return receipt)
- On receipt, update inventory levels (add returned quantity back to stock)
- Mark returned goods as "Returned" quality status (may require inspection)

---

## FR-004: Sales Analytics Dashboard

**Description:** A dashboard showing sales performance metrics.

**Priority:** Medium

**Acceptance Criteria:**

### API

```
GET /api/v1/analytics/sales
Query params: startDate, endDate, groupBy (day/week/month)
Response: {
  "revenue": { total: 500000, trend: [ { date, amount } ] },
  "topCustomers": [ { partnerId, name, totalRevenue, orderCount } ],
  "topProducts": [ { productId, name, totalRevenue, quantitySold } ],
  "conversionRates": { quotationToOrder: 0.65, opportunityToOrder: 0.45 },
  "orderStatus": { draft: 5, confirmed: 12, delivered: 8, invoiced: 10, paid: 15 }
}
```

### Frontend

- New metadata-driven form `sales_analytics` (read-only dashboard)
- Charts: revenue trend, top customers, top products
- Filters: date range, group by period

---

## FR-005: Return & Analytics Forms

**Description:** Form definitions for return and analytics management.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Layout | Sub-Form Tab |
|---|-----------|-------|--------|:---:|
| 1 | `sales_return` | tx_return | 2-col header | Return Lines |
| 2 | `return_line` | tx_return_line | Inline grid | — |
| 3 | `credit_memo` | tx_credit_memo | 2-col | — |
| 4 | `sales_analytics` | (virtual) | Dashboard | — |

**Return Form Sections:**

Section 1 — "Return Information":
- return_number (auto), return_date, partner_id, order_id, invoice_id, reason

Section 2 — "Amounts":
- status, subtotal, tax_amount, credit_amount

Section 3 — "Notes":
- notes

**Sub-form configuration:**

| Parent Form | Relation Code | Child Form Code | Tab Label | Position |
|-------------|--------------|----------------|-----------|:---:|
| sales_return | return_id | return_line | Return Lines | 1 |

---

# Scope

## Included

- Sales Return (RMA) entity + lines (tx_return, tx_return_line)
- Credit Memo entity (tx_credit_memo)
- Return inventory receipt integration
- Sales Analytics dashboard
- 4 metadata-driven forms
- Return → Credit Memo workflow

## Excluded

- Exchange (return + replacement in one transaction)
- Restocking fees
- Return shipping labels
- Advanced analytics (forecasting, cohort analysis)

---

# Database Changes

## New Tables

| Table | Description |
|-------|-------------|
| `tx_return` | Return header |
| `tx_return_line` | Return lines |
| `tx_credit_memo` | Credit memo |

## Migration Files

| Migration File | Contents |
|---------------|----------|
| `V{next}__create_return_tables.sql` | Create tx_return, tx_return_line + metadata |
| `V{next}__create_credit_memo_table.sql` | Create tx_credit_memo + metadata |
| `V{next}__seed_return_forms.sql` | Insert form definitions for all 4 forms |
| `V{next}__seed_return_sub_form_configs.sql` | Insert sub-form configurations |

---

# Acceptance Criteria

1. All 3 new tables created in PostgreSQL
2. All table metadata inserted
3. All 4 form definitions inserted
4. Return workflow enforced (Requested → Approved → Received → Credited)
5. Credit memo generated from approved return
6. Return receipt updates inventory
7. Sales analytics dashboard displays metrics
8. Server verification passes

---

# Future Enhancements

1. Exchange (return + replacement in one transaction)
2. Restocking fees
3. Return shipping labels
4. Advanced analytics (forecasting, cohort analysis)
5. Customer self-service return portal

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial PRD — Sales Return (RMA), Credit Memo, Sales Analytics | 2026-07-29 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-003 — ERP Order Flow Forms](../prd/PRD-003-erp-order-flow-forms.md)
- [PRD-007 — Sales Order Workflow & Customer Management](../prd/PRD-007-sales-order-workflow-customer-management.md)
