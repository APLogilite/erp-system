---
id: PRD-007
title: Sales Order Workflow & Customer Management
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-07-29
updated: 2026-07-29
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
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
  - PRD-003 (ERP Order Flow Forms v1.0.0) — provides tx_order, tx_invoice base
  - PRD-006 (Sales Quotation & Price Management v1.0.0) — quotation conversion creates orders

related_tasks: []

related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED (runtime engine and metadata tables exist)
  - PRD-003 must be COMPLETED (tx_order, tx_invoice base tables exist)
  - PRD-006 must be COMPLETED (quotation conversion creates tx_order)
  - Flyway must be temporarily enabled to execute the new migrations

change_log:
  - 1.0.0 — Initial PRD: Order workflow, auto-calculation, payment terms, credit check, customer 360

---

# Executive Summary

This PRD enhances the existing `tx_order` table (from PRD-003) with **order workflow automation**, **auto-calculation of totals**, **payment terms**, **customer credit management**, and a **Customer 360 view**.

In iDempiere and Odoo, a sales order flows through a defined lifecycle: Draft → Confirmed → Delivered → Invoiced → Paid. This PRD implements that workflow with backend-enforced status transitions, automatic total calculations (subtotal, tax, discount, grand total), and customer credit checks.

The **Customer 360 view** provides a unified dashboard showing all customer activity: orders, invoices, payments, quotations, and opportunities in one place.

This PRD also introduces **Payment Terms** (Net 30, Net 60, etc.) which automatically calculate invoice due dates.

---

# Problem Statement

**What problem are we solving?**

Currently, `tx_order` (from PRD-003) is a static data table with no business logic:
- No status workflow — users manually set any status at any time
- No auto-calculation — users manually calculate subtotal, tax, discount, grand_total
- No credit checking — orders can be created for customers who exceed their credit limit
- No payment terms — invoice due dates must be manually calculated
- No unified customer view — users must open multiple forms to see a customer's full history

**Who experiences this problem?**

- **Sales Representatives** — must manually calculate totals, no enforcement of order workflow
- **Sales Managers** — cannot track order pipeline (how many in Draft vs Confirmed vs Delivered)
- **Finance Team** — must manually calculate invoice due dates, no credit limit enforcement
- **Customer Service** — must open 5+ forms to see a customer's full history

**Why is this feature required?**

Order workflow automation prevents errors (e.g., invoicing a Draft order). Auto-calculation eliminates manual math errors. Credit checks prevent bad debt. Payment terms automate due date calculation. Customer 360 improves service quality by providing complete context.

---

# Business Goals

1. Enforce order status workflow (Draft → Confirmed → Delivered → Invoiced → Paid)
2. Auto-calculate order totals (subtotal, tax, discount, grand_total) from line items
3. Implement payment terms (Net 30, Net 60, etc.) with automatic due date calculation
4. Add customer credit limit/check to prevent over-credit orders
5. Provide Customer 360 view (unified dashboard of all customer activity)
6. All forms remain metadata-driven (no new UI code)

---

# Functional Requirements

## FR-001: Order Status Workflow

**Description:** Backend-enforced status transitions for `tx_order`. Users can only transition to valid next statuses.

**Priority:** High

**Acceptance Criteria:**

### Status Flow

```
DRAFT → CONFIRMED → DELIVERED → INVOICED → PAID
  ↓         ↓           ↓
CANCELLED CANCELLED   CANCELLED
```

### Allowed Transitions

| From | To | Trigger |
|------|----|---------|
| DRAFT | CONFIRMED | User clicks "Confirm" |
| DRAFT | CANCELLED | User clicks "Cancel" |
| CONFIRMED | DELIVERED | User clicks "Mark Delivered" (or auto from shipment) |
| CONFIRMED | CANCELLED | User clicks "Cancel" |
| DELIVERED | INVOICED | User clicks "Create Invoice" (or auto from invoice) |
| DELIVERED | CANCELLED | User clicks "Cancel" (with warning) |
| INVOICED | PAID | Auto when payment covers invoice amount |
| Any | CANCELLED | Only if no linked invoice/payment exists |

### Backend Validation

```java
POST /api/v1/orders/{id}/transition
Request: { "targetStatus": "CONFIRMED" }
Response: { "success": true, "newStatus": "CONFIRMED" }
Error: { "success": false, "error": "Cannot transition from DELIVERED to DRAFT" }
```

- Backend service `OrderWorkflowService` validates transitions
- Invalid transitions return 400 error
- Status field on form is read-only (transitions via action buttons)

---

## FR-002: Auto-Calculation of Order Totals

**Description:** Backend automatically calculates subtotal, tax_amount, discount_amount, grand_total from order lines.

**Priority:** High

**Acceptance Criteria:**

### Calculation Logic

```
For each order line:
  line_total = (quantity × unit_price) - discount_amount

subtotal = SUM(line_total for all lines)
discount_amount = SUM(line discount_amount for all lines)
tax_amount = SUM(line_total × tax_rate / 100 for all lines)
grand_total = subtotal - discount_amount + tax_amount
```

### API

```
POST /api/v1/orders/{id}/calculate
Response: { subtotal, discountAmount, taxAmount, grandTotal }
```

### Triggers

- Auto-calculate on: line add, line update, line delete
- Frontend calls calculate API after each line change
- Header fields (subtotal, tax, discount, grand_total) are read-only on form

---

## FR-003: Payment Terms

**Description:** Payment terms define when payment is due (Net 30, Net 60, etc.). Assigned to customers, used to auto-calculate invoice due dates.

**Priority:** High

**Acceptance Criteria:**

### md_payment_term table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| code | string (20) | ✓ | e.g., "NET30", "NET60" |
| name | string (100) | ✓ | e.g., "Net 30 Days" |
| days | integer | ✓ | Days until due |
| discount_percent | decimal (5,2) | | Early payment discount % |
| discount_days | integer | | Days to qualify for discount |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Integration

- Add `payment_term_id` to `md_business_partner` (customer default)
- Add `payment_term_id` to `tx_order` (order-specific override)
- Add `payment_term_id` to `tx_invoice` (auto-populated from order or customer)
- Auto-calculate `due_date` on invoice: `invoice_date + payment_term.days`

### Seed Data

| Code | Name | Days |
|------|------|------|
| NET0 | Due on Receipt | 0 |
| NET15 | Net 15 Days | 15 |
| NET30 | Net 30 Days | 30 |
| NET60 | Net 60 Days | 60 |
| NET90 | Net 90 Days | 90 |

---

## FR-004: Customer Credit Limit & Check

**Description:** Each customer has a credit limit. Before confirming an order, the system checks if the customer's total outstanding (open orders + unpaid invoices) exceeds the limit.

**Priority:** High

**Acceptance Criteria:**

### Schema Changes

- Add `credit_limit` (decimal 15,2) to `md_business_partner`
- Add `credit_hold` (boolean) to `md_business_partner` — manual override

### Credit Check Logic

```java
POST /api/v1/orders/{id}/check-credit
Response: { 
  "approved": true, 
  "creditLimit": 50000.00, 
  "outstanding": 12000.00, 
  "available": 38000.00, 
  "orderAmount": 5000.00 
}
Error: { 
  "approved": false, 
  "reason": "Credit limit exceeded", 
  "creditLimit": 50000.00, 
  "outstanding": 48000.00, 
  "orderAmount": 5000.00 
}
```

### Calculation

```
outstanding = SUM(grand_total for open orders where status IN ('CONFIRMED', 'DELIVERED'))
            + SUM(due_amount for unpaid invoices where status IN ('VALIDATED', 'PARTIALLY_PAID'))
```

### Enforcement

- Credit check runs automatically when transitioning order to CONFIRMED
- If credit hold = true, block all order confirmations
- If credit limit exceeded, block confirmation with error message
- Sales manager role can override (with audit log)

---

## FR-005: Customer 360 View

**Description:** A unified dashboard showing all customer activity in one place.

**Priority:** Medium

**Acceptance Criteria:**

### API

```
GET /api/v1/customers/{partnerId}/360
Response: {
  "partner": { id, code, name, email, phone, creditLimit, outstanding },
  "recentOrders": [ { id, orderNumber, orderDate, grandTotal, status } ],
  "recentInvoices": [ { id, invoiceNumber, invoiceDate, grandTotal, dueAmount, status } ],
  "recentPayments": [ { id, paymentNumber, paymentDate, amount, status } ],
  "openQuotations": [ { id, quoteNumber, quoteDate, grandTotal, validUntil, status } ],
  "openOpportunities": [ { id, opportunityNumber, stage, expectedRevenue, expectedCloseDate } ]
}
```

### Frontend

- New metadata-driven form `customer_360` (read-only dashboard)
- Sections: Customer Info, Recent Orders, Recent Invoices, Recent Payments, Open Quotations, Open Opportunities
- Each section shows top 5 records with "View All" link to full list
- Accessible from Business Partner form (button or tab)

---

## FR-006: Enhanced Order Form

**Description:** Update the `sales_order` form (from PRD-003) to include payment terms, credit check status, and action buttons for workflow transitions.

**Priority:** High

**Acceptance Criteria:**

### Form Changes

- Add `payment_term_id` field (dropdown from md_payment_term)
- Make `status` field read-only (transitions via buttons)
- Make `subtotal`, `tax_amount`, `discount_amount`, `grand_total` read-only (auto-calculated)
- Add action buttons: "Confirm", "Mark Delivered", "Create Invoice", "Cancel"
- Show credit check status badge (Approved / Exceeded / On Hold)

---

# Non-Functional Requirements

## NFR-001: Performance

- Credit check cached for 5 minutes per customer (avoid repeated calculations)
- Customer 360 API returns max 5 records per section (pagination for full lists)

## NFR-002: Audit

- All status transitions logged (who, when, from → to)
- Credit check overrides logged (who, when, reason)

---

# User Stories

## US-001: Sales Rep Confirms Order

> As a Sales Rep, I want to confirm an order after verifying customer credit, so that the system prevents orders for customers who exceed their credit limit.

## US-002: Finance Creates Invoice from Order

> As an Accountant, I want to create an invoice from a delivered order, so that the due date is automatically calculated from payment terms.

## US-003: Customer Service Views Customer 360

> As a Customer Service Rep, I want to see all customer activity in one place, so that I can answer questions without opening multiple forms.

---

# User Flow

```
Sales Rep opens Sales Order form
  ↓
System auto-generates order_number (SO-0001)
  ↓
Sales Rep selects customer
  ↓
System loads payment_term_id from customer default
  ↓
Sales Rep adds products in Order Lines tab
  ↓
System auto-calculates line_total, subtotal, tax, discount, grand_total
  ↓
Sales Rep clicks "Confirm"
  ↓
System checks customer credit limit
  ↓
If approved: status → CONFIRMED
If exceeded: error message, status stays DRAFT
  ↓
Warehouse ships goods → clicks "Mark Delivered" (status → DELIVERED)
  ↓
Finance clicks "Create Invoice" (status → INVOICED)
  ↓
System creates tx_invoice with due_date = invoice_date + payment_term.days
  ↓
Customer pays → payment recorded → status → PAID (auto)
```

---

# Scope

## Included

- Order status workflow (Draft → Confirmed → Delivered → Invoiced → Paid)
- Auto-calculation of order totals
- Payment terms master (md_payment_term)
- Customer credit limit/check
- Customer 360 API + dashboard form
- Enhanced sales order form (payment terms, action buttons, read-only calculated fields)

## Excluded

- CRM pipeline (Lead → Opportunity) — PRD-008
- Sales team management — PRD-008
- Sales return (RMA) — PRD-009
- Multi-currency exchange rates — PRD-009
- Automated invoice generation from shipment — manual trigger only
- Recurring orders/subscriptions

---

# API Requirements

| Endpoint | Method | Request | Response |
|----------|--------|---------|----------|
| `/api/v1/orders/{id}/transition` | POST | `{ targetStatus }` | `{ success, newStatus }` |
| `/api/v1/orders/{id}/calculate` | POST | — | `{ subtotal, discountAmount, taxAmount, grandTotal }` |
| `/api/v1/orders/{id}/check-credit` | POST | — | `{ approved, creditLimit, outstanding, available }` |
| `/api/v1/customers/{partnerId}/360` | GET | — | `{ partner, recentOrders, recentInvoices, recentPayments, openQuotations, openOpportunities }` |

---

# Database Changes

## New Tables

| Table | Description |
|-------|-------------|
| `md_payment_term` | Payment terms master |

## Modified Tables

| Table | Changes |
|-------|---------|
| `md_business_partner` | Add `credit_limit`, `credit_hold`, `payment_term_id` |
| `tx_order` | Add `payment_term_id` |
| `tx_invoice` | Add `payment_term_id` |

## Migration Files

| Migration File | Contents |
|---------------|----------|
| `V{next}__create_payment_term_table.sql` | Create md_payment_term + seed data |
| `V{next}__add_credit_to_partner.sql` | Add credit_limit, credit_hold, payment_term_id to md_business_partner |
| `V{next}__add_payment_term_to_order_invoice.sql` | Add payment_term_id to tx_order, tx_invoice |
| `V{next}__seed_payment_term_form.sql` | Insert payment term form definition |
| `V{next}__seed_customer_360_form.sql` | Insert customer 360 form definition |
| `V{next}__update_sales_order_form.sql` | Update sales order form with new fields |

---

# Edge Cases

| Edge Case | Handling |
|-----------|----------|
| Customer has no payment term | Default to NET30 |
| Credit check fails but manager overrides | Allow with audit log |
| Order cancelled after invoice created | Block cancellation, require credit memo |
| Payment exceeds invoice amount | Apply to other open invoices or credit balance |
| Customer 360 for new customer (no history) | Show empty sections with "No records" message |

---

# Acceptance Criteria

1. Payment terms table created with seed data
2. Credit limit and payment_term_id added to md_business_partner
3. Order status workflow enforced (invalid transitions blocked)
4. Auto-calculation works on order lines (add/update/delete)
5. Credit check blocks confirmation when limit exceeded
6. Customer 360 API returns all customer data
7. Customer 360 form renders as dashboard
8. Sales order form updated with payment terms and action buttons
9. Server verification passes

---

# Future Enhancements

1. Automated invoice generation from shipment
2. Recurring orders/subscriptions
3. Order approval workflow (manager approval for large orders)
4. Dunning management (payment reminders)
5. Customer portal (view orders, invoices online)

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial PRD — Order workflow, auto-calculation, payment terms, credit check, customer 360 | 2026-07-29 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-003 — ERP Order Flow Forms](../prd/PRD-003-erp-order-flow-forms.md)
- [PRD-006 — Sales Quotation & Price Management](../prd/PRD-006-sales-quotation-price-management.md)
