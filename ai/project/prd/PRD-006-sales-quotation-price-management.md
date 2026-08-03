---
id: PRD-006
title: Sales Quotation & Price Management
version: 1.0.0
status: APPROVED
priority: High
owner: Product Manager
created: 2026-07-29
updated: 2026-07-29
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-006-sales-quotation-price-management
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

related_tasks: []

related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED (runtime engine and metadata tables exist)
  - PRD-003 must be COMPLETED (tx_order, tx_invoice base tables exist)
  - Flyway must be temporarily enabled to execute the new migrations

change_log:
  - 1.0.0 — Initial PRD: Sales Quotation, Price List, Discount Rules, Auto-numbering

---

# Executive Summary

This PRD adds the **Sales Quotation** and **Price Management** capabilities to the ERP platform. In iDempiere and Odoo, a quotation is the entry point of the sales cycle — a formal offer sent to a customer that can be accepted (converted to a Sales Order) or rejected.

This PRD introduces:
1. **Sales Quotation** — a pre-order document with line items, validity dates, and conversion to Sales Order
2. **Price List** — product pricing rules per customer segment or individual customer
3. **Discount Rules** — volume discounts, customer-specific discounts, percentage/fixed amount discounts
4. **Document Auto-Numbering** — configurable sequence generator (QT-0001, SO-0001, INV-0001, etc.)

All forms are **metadata-driven** (no hardcoded Java entities for UI). Business logic (price resolution, discount calculation, auto-numbering, quotation-to-order conversion) lives in the **backend service layer**. The frontend shows live totals via API calls.

This PRD also **deprecates the hardcoded `sales/` module** (`SalesOrder`, `SalesOrderLine`) — all sales order functionality is consolidated into the metadata-driven `tx_order` table from PRD-003.

---

# Problem Statement

**What problem are we solving?**

Currently, the ERP has no formal quotation process. Sales teams cannot create professional price quotes for customers, track quote validity, or convert accepted quotes to orders. Additionally:
- Product pricing is static (single `unit_price` on `md_product`) — no customer-specific or volume-based pricing
- No discount management — discounts must be manually calculated
- No document numbering — users manually type order numbers, invoice numbers, etc.
- Hardcoded `sales/` module duplicates `tx_order` functionality, creating maintenance burden

**Who experiences this problem?**

- **Sales Representatives** — cannot create professional quotes, must manually calculate prices
- **Sales Managers** — no visibility into quote pipeline, cannot track win/loss rates
- **Customers** — receive informal quotes without validity dates or professional formatting
- **System Administrators** — must maintain duplicate code paths for sales orders

**Why is this feature required?**

A formal quotation process is the foundation of B2B sales. Without it, the ERP cannot support real sales workflows. Price lists and discount rules are essential for competitive pricing strategies. Auto-numbering is required for audit trails and professional document presentation.

---

# Business Goals

1. Enable sales teams to create, send, and track sales quotations
2. Support customer-specific and volume-based pricing via price lists
3. Automate discount calculations (percentage, fixed, volume-based)
4. Generate professional document numbers automatically (QT-0001, SO-0001, etc.)
5. Convert accepted quotations to sales orders with one click
6. Consolidate all sales order functionality into metadata-driven forms (remove hardcoded `sales/` module)
7. All forms rendered via PRD-001's metadata engine — zero new UI code

---

# Functional Requirements

## FR-001: Document Auto-Numbering Service

**Description:** A configurable backend service that generates sequential document numbers with prefixes and padding.

**Priority:** High

**Acceptance Criteria:**

### sys_document_sequence table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| sequence_name | string (100) | ✓ | e.g., "quotation", "sales_order", "sales_invoice" |
| prefix | string (10) | ✓ | e.g., "QT-", "SO-", "INV-" |
| next_value | integer | ✓ | Next number to assign |
| padding | integer | ✓ | Number of digits (e.g., 4 → "0001") |
| current_year | integer | | Optional year reset (e.g., QT-2026-0001) |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### API

```
POST /api/v1/sequences/{sequenceName}/next
Response: { "documentNumber": "QT-0001" }
```

- Backend service `DocumentSequenceService` with method `getNextNumber(String sequenceName)`
- Thread-safe (uses database sequence or pessimistic locking)
- Configurable via admin UI (metadata form for `sys_document_sequence`)
- Default sequences seeded: quotation (QT-), sales_order (SO-), sales_invoice (INV-), purchase_order (PO-), purchase_invoice (PIN-), payment (PAY-), shipment (SHP-)

---

## FR-002: Price List Master

**Description:** A price list defines product pricing for a specific customer segment, currency, or time period. Multiple price lists can exist; the system resolves the best price for a product+customer combination.

**Priority:** High

**Acceptance Criteria:**

### md_price_list table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| code | string (50) | ✓ | Unique identifier |
| name | string (200) | ✓ | Display name |
| currency | string (3) | ✓ | ISO currency code |
| valid_from | date | | Start of validity period |
| valid_to | date | | End of validity period (null = no expiry) |
| is_default | boolean | | Fallback price list |
| partner_id | many2one → md_business_partner | | Customer-specific price list (null = general) |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### md_price_list_line table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| price_list_id | many2one → md_price_list | ✓ | Parent price list |
| product_id | many2one → md_product | ✓ | Product |
| min_quantity | decimal (15,3) | | Minimum quantity for this price (volume pricing) |
| unit_price | decimal (15,2) | ✓ | Price for this product at this quantity |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Price Resolution Logic

Backend service `PriceResolutionService`:
1. Look for customer-specific price list (partner_id matches)
2. If not found, look for default price list (is_default = true)
3. Within the price list, find the best price for the product:
   - Match exact product_id
   - If multiple lines match, use the one with highest min_quantity ≤ order quantity
   - If no match, fall back to `md_product.unit_price`

---

## FR-003: Discount Rules

**Description:** Configurable discount rules that apply to quotation/order lines based on customer, product, quantity, or date range.

**Priority:** Medium

**Acceptance Criteria:**

### md_discount_rule table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| code | string (50) | ✓ | Unique identifier |
| name | string (200) | ✓ | Display name |
| discount_type | enum: percentage / fixed_amount | ✓ | |
| discount_value | decimal (15,2) | ✓ | Percentage (0-100) or fixed amount |
| partner_id | many2one → md_business_partner | | Customer-specific (null = all customers) |
| product_id | many2one → md_product | | Product-specific (null = all products) |
| min_quantity | decimal (15,3) | | Minimum quantity to qualify |
| valid_from | date | | Start of validity |
| valid_to | date | | End of validity |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Discount Resolution Logic

Backend service `DiscountResolutionService`:
1. Find all active discount rules matching the context (partner, product, quantity, date)
2. Apply the highest discount value
3. Calculate: `line_total = (unit_price × quantity) - discount_amount`

---

## FR-004: Sales Quotation Entity

**Description:** A formal price quote sent to a customer. Contains line items with products, quantities, and prices. Can be converted to a Sales Order when accepted.

**Priority:** High

**Acceptance Criteria:**

### tx_quotation table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| quote_number | string (50) | ✓ | Auto-generated (QT-0001) |
| quote_date | date | ✓ | Date of quotation |
| valid_until | date | | Quotation expiry date |
| partner_id | many2one → md_business_partner | ✓ | Customer |
| price_list_id | many2one → md_price_list | | Price list used |
| currency | string (3) | | Default: USD |
| subtotal | decimal (15,2) | | Calculated from lines |
| discount_amount | decimal (15,2) | | Total discount applied |
| tax_amount | decimal (15,2) | | Total tax |
| grand_total | decimal (15,2) | | subtotal - discount + tax |
| status | enum: draft / sent / accepted / rejected / converted / expired | ✓ | |
| notes | text | | |
| converted_order_id | many2one → tx_order | | Linked sales order (after conversion) |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### tx_quotation_line table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| quotation_id | many2one → tx_quotation | ✓ | Parent quotation |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| description | text | | |
| quantity | decimal (15,3) | ✓ | |
| uom_id | many2one → md_uom | | |
| unit_price | decimal (15,2) | ✓ | Resolved from price list or product |
| discount_percent | decimal (5,2) | | Applied discount % |
| discount_amount | decimal (15,2) | | Calculated discount |
| line_total | decimal (15,2) | | (qty × price) - discount |
| tax_rate | decimal (5,2) | | Tax percentage |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Status Workflow

```
DRAFT → SENT → ACCEPTED → CONVERTED (creates tx_order)
              → REJECTED
              → EXPIRED (auto when valid_until passes)
```

---

## FR-005: Quotation Forms (Metadata-Driven)

**Description:** Form definitions for quotation management, following PRD-003's metadata-driven approach.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Layout | Sub-Form Tab |
|---|-----------|-------|--------|:---:|
| 1 | `sales_quotation` | tx_quotation | 2-col header | Quotation Lines |
| 2 | `quotation_line` | tx_quotation_line | Inline grid | — |
| 3 | `price_list` | md_price_list | 2-col | — |
| 4 | `price_list_line` | md_price_list_line | Inline grid | — |
| 5 | `discount_rule` | md_discount_rule | 2-col | — |
| 6 | `document_sequence` | sys_document_sequence | 2-col | — |

**Quotation Form Sections:**

Section 1 — "Quote Information":
- quote_number (auto-generated, read-only), quote_date, valid_until, partner_id, price_list_id, currency

Section 2 — "Amounts":
- status, subtotal (read-only), discount_amount (read-only), tax_amount (read-only), grand_total (read-only)

Section 3 — "Notes":
- notes

**Sub-form configuration:**

| Parent Form | Relation Code | Child Form Code | Tab Label | Position |
|-------------|--------------|----------------|-----------|:---:|
| sales_quotation | quotation_id | quotation_line | Quotation Lines | 1 |

**Live Total Calculation:**
- Frontend calls backend API to recalculate totals when line items change
- `POST /api/v1/quotations/{id}/calculate` returns updated subtotal, discount, tax, grand_total

---

## FR-006: Quotation to Order Conversion

**Description:** When a quotation is accepted, the user clicks "Convert to Order" which creates a `tx_order` record with `order_type = 'sales'` and copies all line items.

**Priority:** High

**Acceptance Criteria:**

### API

```
POST /api/v1/quotations/{id}/convert-to-order
Response: { "orderId": "uuid", "orderNumber": "SO-0001" }
```

### Backend Logic

1. Validate quotation status is ACCEPTED
2. Generate new order number via DocumentSequenceService ("sales_order")
3. Create `tx_order` record:
   - order_type = 'sales'
   - partner_id = quotation.partner_id
   - Copy amounts (subtotal, discount, tax, grand_total)
   - status = 'draft'
4. Create `tx_order_line` records from quotation lines
5. Update quotation: status = 'converted', converted_order_id = new order ID
6. Return the new order

---

## FR-007: Deprecate Hardcoded Sales Module

**Description:** Remove the hardcoded `sales/` module (`SalesOrder`, `SalesOrderLine` entities, controllers, services) since all functionality is now in metadata-driven `tx_order`.

**Priority:** Medium

**Acceptance Criteria:**

- Delete `backend/src/main/java/com/erp/modules/sales/` directory
- Delete `backend/src/main/java/com/erp/modules/order/` directory (redundant with tx_order)
- Remove any Flyway migrations that created `sales_orders` or `orders` tables (if they exist)
- Add migration to drop `sales_orders` and `orders` tables if they exist
- Verify no references to these modules remain in the codebase

---

# Non-Functional Requirements

## NFR-001: Migration Safety

- All migrations use `DROP TABLE IF EXISTS ... CASCADE` before `CREATE TABLE`
- Metadata insertions use delete-and-reinsert pattern to be idempotent
- Migrations are ordered: master data → transaction tables → forms → sub-form configs

## NFR-002: Performance

- Price resolution cached in-memory (5-minute TTL)
- Document sequence generation uses database-level locking (no application-level synchronization needed)
- Quotation list queries paginated (default 50 records)

## NFR-003: Security

- All APIs require JWT authentication
- Tenant isolation enforced on all queries
- Price list access controlled by role (sales reps see only their assigned price lists)

---

# User Stories

## US-001: Sales Rep Creates a Quotation

> As a Sales Rep, I want to create a quotation for a customer, select products from the price list, see live totals with discounts applied, and send it to the customer, so that I can provide professional price quotes.

## US-002: Sales Rep Converts Quote to Order

> As a Sales Rep, I want to convert an accepted quotation to a sales order with one click, so that I don't have to re-enter all the line items.

## US-003: Sales Manager Configures Price Lists

> As a Sales Manager, I want to create customer-specific price lists with volume discounts, so that we can offer competitive pricing to our best customers.

## US-004: System Generates Document Numbers

> As a System Admin, I want document numbers to be auto-generated (QT-0001, SO-0001, INV-0001), so that all documents have consistent, professional numbering.

---

# User Flow

```
Sales Rep opens Sales Quotation form
  ↓
System auto-generates quote_number (QT-0001)
  ↓
Sales Rep selects customer (partner_id)
  ↓
System resolves price list for this customer
  ↓
Sales Rep adds products in Quotation Lines tab
  ↓
System resolves unit_price from price list (or product default)
  ↓
System applies discount rules
  ↓
System calculates line_total, subtotal, discount, tax, grand_total (live)
  ↓
Sales Rep sets valid_until date
  ↓
Sales Rep saves (status = DRAFT)
  ↓
Sales Rep clicks "Send" (status = SENT)
  ↓
Customer accepts → Sales Rep clicks "Accept" (status = ACCEPTED)
  ↓
Sales Rep clicks "Convert to Order"
  ↓
System creates tx_order with order_type = 'sales'
  ↓
System generates SO-0001, copies all lines
  ↓
Quotation status = CONVERTED, linked to order
```

---

# Scope

## Included

- Document auto-numbering service (sys_document_sequence + API)
- Price list master + lines (md_price_list, md_price_list_line)
- Discount rules (md_discount_rule)
- Sales quotation entity + lines (tx_quotation, tx_quotation_line)
- Price resolution backend service
- Discount resolution backend service
- Quotation → Order conversion service
- 6 metadata-driven forms (quotation, quotation_line, price_list, price_list_line, discount_rule, document_sequence)
- Sub-form configuration (quotation → lines)
- Live total calculation API
- Deprecation of hardcoded sales/ and order/ modules

## Excluded

- CRM pipeline (Lead → Opportunity → Quotation) — deferred to PRD-008
- Sales team management (salesperson, territory, commission) — deferred to PRD-008
- Customer 360 view — deferred to PRD-007
- Credit limit/check — deferred to PRD-007
- Payment terms — deferred to PRD-007
- Sales return (RMA) — deferred to PRD-009
- Tax calculation engine — simple tax_rate field only
- Multi-currency exchange rates — deferred to PRD-009
- Email/PDF generation for quotations
- Customer portal (view quotes online)

---

# API Requirements

## Document Sequence

| Endpoint | Method | Request | Response |
|----------|--------|---------|----------|
| `/api/v1/sequences/{name}/next` | POST | — | `{ documentNumber: "QT-0001" }` |
| `/api/v1/sequences/{name}/peek` | GET | — | `{ nextNumber: "QT-0002" }` (no increment) |

## Price Resolution

| Endpoint | Method | Request | Response |
|----------|--------|---------|----------|
| `/api/v1/pricing/resolve` | POST | `{ productId, partnerId, quantity }` | `{ unitPrice, discountPercent, discountAmount }` |

## Quotation Calculation

| Endpoint | Method | Request | Response |
|----------|--------|---------|----------|
| `/api/v1/quotations/{id}/calculate` | POST | — | `{ subtotal, discountAmount, taxAmount, grandTotal }` |

## Quotation Conversion

| Endpoint | Method | Request | Response |
|----------|--------|---------|----------|
| `/api/v1/quotations/{id}/convert-to-order` | POST | — | `{ orderId, orderNumber }` |

---

# Database Changes

## New Tables

| Table | Description |
|-------|-------------|
| `sys_document_sequence` | Auto-numbering configuration |
| `md_price_list` | Price list header |
| `md_price_list_line` | Price list lines |
| `md_discount_rule` | Discount rules |
| `tx_quotation` | Quotation header |
| `tx_quotation_line` | Quotation lines |

## Dropped Tables

| Table | Reason |
|-------|--------|
| `sales_orders` | Replaced by metadata-driven tx_order |
| `sales_order_lines` | Replaced by metadata-driven tx_order_line |
| `orders` | Replaced by metadata-driven tx_order |
| `order_lines` | Replaced by metadata-driven tx_order_line |

## Migration Files

| Migration File | Contents |
|---------------|----------|
| `V{next}__drop_legacy_sales_tables.sql` | Drop sales_orders, sales_order_lines, orders, order_lines |
| `V{next+1}__create_sequence_table.sql` | Create sys_document_sequence + seed default sequences |
| `V{next+2}__create_price_list_tables.sql` | Create md_price_list, md_price_list_line + metadata |
| `V{next+3}__create_discount_table.sql` | Create md_discount_rule + metadata |
| `V{next+4}__create_quotation_tables.sql` | Create tx_quotation, tx_quotation_line + metadata |
| `V{next+5}__seed_quotation_forms.sql` | Insert form definitions for all 6 forms |
| `V{next+6}__seed_sub_form_configs.sql` | Insert sub-form configurations |

---

# Security Requirements

- All APIs require JWT authentication
- Tenant isolation enforced via `tenant_id` on all tables
- Price list access: sales reps see only price lists for their assigned customers
- Document sequence numbers are tenant-scoped (each tenant has independent sequences)

---

# Edge Cases

| Edge Case | Handling |
|-----------|----------|
| Concurrent quote number generation | Database-level locking on sequence table |
| Price list not found for customer | Fall back to default price list, then product.unit_price |
| Discount rule overlaps | Apply highest discount value |
| Quote expires while in DRAFT | Status auto-updated to EXPIRED by scheduled job |
| Convert already-converted quote | Return error: "Quotation already converted" |
| Delete product used in price list | Prevent deletion or cascade (configurable) |

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Dropping legacy tables breaks existing code | High | Search codebase for references before dropping; add migration guards |
| Price resolution performance | Medium | Cache resolved prices; index on product_id + partner_id |
| Sequence number gaps | Low | Acceptable — gaps occur on rollback; document in admin guide |

---

# Assumptions

1. PRD-001 runtime engine is fully functional and COMPLETED
2. PRD-003 tx_order table exists and is used for sales orders
3. Hardcoded sales/ and order/ modules have no critical business logic (only CRUD)
4. Users will manually set quotation status to ACCEPTED/REJECTED (no automated approval workflow)
5. Tax calculation is simple percentage (no complex tax rules engine)

---

# Dependencies

| Dependency | Description | Status |
|-----------|-------------|--------|
| PRD-001 Runtime Engine | DynamicFormRenderer, useForm(), RuntimeFormController | COMPLETED |
| PRD-003 tx_order table | Base table for order conversion | COMPLETED |
| PRD-003 md_product table | Product master for price list lines | COMPLETED |
| PRD-003 md_business_partner table | Customer master | COMPLETED |

---

# Acceptance Criteria

1. All 6 new tables physically created in PostgreSQL
2. All table metadata inserted into sys_metadata_models and sys_table_columns
3. All 6 form definitions inserted into sys_metadata_views
4. All form fields inserted into sys_form_fields
5. Form layouts inserted into sys_form_layout_sections and sys_form_section_fields
6. Sub-form configurations inserted into sys_form_sub_forms
7. Document sequence service generates QT-0001, QT-0002, etc.
8. Price resolution returns correct price for product+customer+quantity
9. Discount resolution applies correct discount
10. Quotation form renders with line items sub-form
11. Live total calculation works on frontend
12. Convert to Order creates tx_order with correct data
13. Legacy sales_orders/orders tables dropped
14. Hardcoded sales/ and order/ modules deleted
15. All 6 forms appear in GET /api/runtime/forms
16. Full CRUD works on every form
17. Server verification passes (backend + frontend start without errors)

---

# Deployment Requirements

- Temporarily set `spring.flyway.enabled=true`
- Place 7 migration files in `db/migration/`
- Start application → Flyway executes migrations
- Verify forms load via `bash start-all.sh` and check `/tmp/erp-backend.log` + `/tmp/erp-frontend.log`
- Revert `spring.flyway.enabled=false`

---

# Testing Requirements

- Verify all 6 tables exist in PostgreSQL
- Open all 6 forms in browser
- Full CRUD cycle on each form
- Create quotation → add lines → verify live totals
- Convert quotation to order → verify order created with correct data
- Test price resolution with different customer/quantity combinations
- Test discount application
- Test auto-numbering (create multiple quotes, verify sequential numbers)
- Verify legacy tables dropped
- Server verification (start-all.sh, check logs)

---

# Future Enhancements

1. Email quotation to customer (PDF attachment)
2. Customer portal (view/accept quotes online)
3. Quote versioning (track changes to sent quotes)
4. Quote templates (predefined line items for common quotes)
5. Integration with CRM pipeline (Lead → Opportunity → Quote)
6. Commission calculation on converted quotes
7. Multi-currency support with exchange rates
8. Complex tax engine (tax rules by jurisdiction)

---

# Open Questions

*(none)*

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial PRD — Sales Quotation, Price List, Discount Rules, Auto-numbering | 2026-07-29 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-003 — ERP Order Flow Forms](../prd/PRD-003-erp-order-flow-forms.md)
- [PROJECT_BOARD.md](../PROJECT_BOARD.md)
- [CHANGELOG.md](../docs/changelog.md)
