---
id: PRD-008
title: CRM Pipeline & Sales Team Management
version: 1.0.0
status: APPROVED
priority: Medium
owner: Product Manager
created: 2026-07-29
updated: 2026-07-29
approved_by: user
project: Dynamic ERP Platform
repository: erp-system
prd_branch: prd/PRD-008-crm-pipeline-sales-team
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
  - PRD-006 (Sales Quotation & Price Management v1.0.0) — quotation creation from opportunity
  - PRD-007 (Sales Order Workflow & Customer Management v1.0.0) — customer 360 includes opportunities

related_tasks: []

related_bugs: []

dependencies:
  - PRD-001 must be COMPLETED (runtime engine and metadata tables exist)
  - PRD-006 must be COMPLETED (quotation creation from opportunity)
  - PRD-007 must be COMPLETED (customer 360 includes opportunities)
  - Flyway must be temporarily enabled to execute the new migrations

change_log:
  - 1.0.0 — Initial PRD: Lead/Opportunity pipeline, sales team, commission, CRM dashboard

---

# Executive Summary

This PRD adds a **simple CRM pipeline** (Lead → Opportunity → Quotation → Order) and **Sales Team Management** (salesperson assignment, territory, commission tracking).

In iDempiere and Odoo, the CRM pipeline tracks potential customers from first contact (Lead) through qualified interest (Opportunity) to formal quote (Quotation) and finally to confirmed order. This PRD implements a simplified version suitable for small-to-medium businesses.

The **Sales Team** feature allows assigning salespeople to leads/opportunities, tracking territories, and calculating commissions on closed deals.

This PRD **deprecates the hardcoded `crm/` module** (Lead, Opportunity entities) — all CRM functionality is consolidated into metadata-driven forms.

---

# Problem Statement

**What problem are we solving?**

Currently, the ERP has basic Lead and Opportunity entities (hardcoded Java) with no pipeline workflow:
- No lead qualification process (when does a Lead become an Opportunity?)
- No lead scoring (which leads are most likely to convert?)
- No sales team assignment (who owns this lead?)
- No commission tracking (how much commission did this salesperson earn?)
- No CRM dashboard (what's in the pipeline?)

**Who experiences this problem?**

- **Sales Representatives** — no clear process for qualifying leads, no visibility into their pipeline
- **Sales Managers** — cannot track team performance, cannot assign territories, cannot calculate commissions
- **Marketing Team** — cannot track which campaigns generate the most qualified leads

**Why is this feature required?**

A CRM pipeline is essential for managing the sales process. Without it, leads fall through the cracks, opportunities are not followed up, and sales performance cannot be measured.

---

# Business Goals

1. Implement Lead → Opportunity → Quotation → Order pipeline
2. Add lead qualification workflow (New → Contacted → Qualified → Converted/Lost)
3. Add lead scoring (Hot/Warm/Cold based on criteria)
4. Implement sales team management (salesperson, territory, manager)
5. Track commissions on closed opportunities
6. Provide CRM dashboard (pipeline stages, conversion rates)
7. Deprecate hardcoded crm/ module

---

# Functional Requirements

## FR-001: Lead Entity (Enhanced)

**Description:** Enhance the existing Lead entity with qualification workflow and scoring.

**Priority:** High

**Acceptance Criteria:**

### tx_lead table (replaces hardcoded leads table)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| lead_number | string (50) | ✓ | Auto-generated (LD-0001) |
| company_name | string (200) | ✓ | |
| contact_name | string (200) | ✓ | |
| email | string (100) | | |
| phone | string (30) | | |
| source | enum: website / referral / cold_call / trade_show / advertisement / other | | |
| status | enum: new / contacted / qualified / converted / lost | ✓ | |
| score | enum: hot / warm / cold | | Auto-calculated |
| salesperson_id | many2one → sys_user | | Assigned salesperson |
| territory_id | many2one → md_sales_territory | | Sales territory |
| expected_revenue | decimal (15,2) | | |
| notes | text | | |
| converted_partner_id | many2one → md_business_partner | | Linked customer (after conversion) |
| converted_opportunity_id | many2one → tx_opportunity | | Linked opportunity (after conversion) |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Lead Scoring Logic

```
score = hot if: expected_revenue > $50,000 OR source = 'referral'
score = warm if: expected_revenue > $10,000 OR source IN ('website', 'trade_show')
score = cold otherwise
```

### Lead Qualification Workflow

```
NEW → CONTACTED → QUALIFIED → CONVERTED (creates Opportunity)
                    ↓
                   LOST
```

---

## FR-002: Opportunity Entity (Enhanced)

**Description:** Enhance the existing Opportunity entity with pipeline stages and conversion to quotation.

**Priority:** High

**Acceptance Criteria:**

### tx_opportunity table (replaces hardcoded opportunities table)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| opportunity_number | string (50) | ✓ | Auto-generated (OPP-0001) |
| name | string (200) | ✓ | Opportunity name |
| partner_id | many2one → md_business_partner | ✓ | Customer |
| lead_id | many2one → tx_lead | | Source lead |
| stage | enum: qualification / proposal / negotiation / closed_won / closed_lost | ✓ | |
| probability | integer | | 0-100% |
| expected_revenue | decimal (15,2) | ✓ | |
| expected_close_date | date | | |
| salesperson_id | many2one → sys_user | | Assigned salesperson |
| territory_id | many2one → md_sales_territory | | Sales territory |
| notes | text | | |
| converted_quotation_id | many2one → tx_quotation | | Linked quotation (after conversion) |
| converted_order_id | many2one → tx_order | | Linked order (after close) |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Pipeline Stages

```
QUALIFICATION → PROPOSAL → NEGOTIATION → CLOSED_WON (creates Order)
                              ↓
                         CLOSED_LOST
```

### Conversion Actions

- **Create Quotation** — generates tx_quotation from opportunity (pre-fills customer, expected_revenue)
- **Close Won** — converts to tx_order (via quotation or direct)
- **Close Lost** — marks as lost with reason

---

## FR-003: Sales Territory

**Description:** Sales territories group customers and leads by geographic region or market segment.

**Priority:** Medium

**Acceptance Criteria:**

### md_sales_territory table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| code | string (20) | ✓ | e.g., "WEST", "EAST" |
| name | string (100) | ✓ | e.g., "West Coast", "East Coast" |
| manager_id | many2one → sys_user | | Territory manager |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Integration

- Add `territory_id` to `md_business_partner` (customer assignment)
- Add `territory_id` to `tx_lead` and `tx_opportunity` (lead/opportunity assignment)

---

## FR-004: Commission Tracking

**Description:** Track salesperson commissions on closed opportunities.

**Priority:** Medium

**Acceptance Criteria:**

### tx_commission table

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| id | UUID PK | ✓ | System column |
| salesperson_id | many2one → sys_user | ✓ | |
| opportunity_id | many2one → tx_opportunity | ✓ | |
| order_id | many2one → tx_order | | Linked order |
| commission_percent | decimal (5,2) | ✓ | e.g., 5.00 = 5% |
| commission_amount | decimal (15,2) | ✓ | Calculated |
| status | enum: pending / approved / paid | ✓ | |
| payment_date | date | | |
| is_active | boolean | ✓ | System column |
| tenant_id | UUID | ✓ | System column |

### Calculation

```
commission_amount = opportunity.expected_revenue × commission_percent / 100
```

- Commission record created automatically when opportunity is CLOSED_WON
- Default commission_percent from salesperson profile (configurable)

---

## FR-005: CRM Dashboard

**Description:** A dashboard showing pipeline stages, conversion rates, and team performance.

**Priority:** Medium

**Acceptance Criteria:**

### API

```
GET /api/v1/crm/dashboard
Response: {
  "pipeline": {
    "qualification": { count: 12, totalRevenue: 150000 },
    "proposal": { count: 8, totalRevenue: 200000 },
    "negotiation": { count: 5, totalRevenue: 180000 },
    "closedWon": { count: 15, totalRevenue: 500000 },
    "closedLost": { count: 3, totalRevenue: 50000 }
  },
  "conversionRates": {
    "leadToOpportunity": 0.25,
    "opportunityToOrder": 0.60
  },
  "teamPerformance": [
    { salespersonId, name, openOpportunities, closedRevenue, commissionEarned }
  ]
}
```

### Frontend

- New metadata-driven form `crm_dashboard` (read-only dashboard)
- Pipeline visualization (funnel chart)
- Team performance table

---

## FR-006: CRM Forms (Metadata-Driven)

**Description:** Form definitions for CRM management.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Layout | Sub-Form Tab |
|---|-----------|-------|--------|:---:|
| 1 | `lead` | tx_lead | 2-col | — |
| 2 | `opportunity` | tx_opportunity | 2-col | — |
| 3 | `sales_territory` | md_sales_territory | 2-col | — |
| 4 | `commission` | tx_commission | 2-col | — |
| 5 | `crm_dashboard` | (virtual) | Dashboard | — |

**Lead Form Sections:**

Section 1 — "Lead Information":
- lead_number (auto), company_name, contact_name, email, phone, source

Section 2 — "Qualification":
- status, score (read-only, auto-calculated), expected_revenue, salesperson_id, territory_id

Section 3 — "Notes":
- notes

**Opportunity Form Sections:**

Section 1 — "Opportunity Information":
- opportunity_number (auto), name, partner_id, lead_id, expected_revenue, expected_close_date

Section 2 — "Pipeline":
- stage, probability, salesperson_id, territory_id

Section 3 — "Notes":
- notes

---

## FR-007: Deprecate Hardcoded CRM Module

**Description:** Remove the hardcoded `crm/` module (Lead, Opportunity entities, controllers, services).

**Priority:** Medium

**Acceptance Criteria:**

- Delete `backend/src/main/java/com/erp/modules/crm/` directory
- Add migration to drop `leads` and `opportunities` tables
- Verify no references to these modules remain in the codebase

---

# Scope

## Included

- Lead entity with qualification workflow and scoring (tx_lead)
- Opportunity entity with pipeline stages (tx_opportunity)
- Sales territory master (md_sales_territory)
- Commission tracking (tx_commission)
- CRM dashboard (pipeline visualization, team performance)
- 5 metadata-driven forms
- Lead → Opportunity → Quotation → Order conversion
- Deprecation of hardcoded crm/ module

## Excluded

- Marketing campaign management
- Lead import from CSV/Excel
- Email integration (track emails to leads)
- Activity tracking (calls, meetings, tasks)
- Advanced lead scoring (ML-based)

---

# Database Changes

## New Tables

| Table | Description |
|-------|-------------|
| `tx_lead` | Lead master (replaces hardcoded leads) |
| `tx_opportunity` | Opportunity master (replaces hardcoded opportunities) |
| `md_sales_territory` | Sales territory master |
| `tx_commission` | Commission tracking |

## Dropped Tables

| Table | Reason |
|-------|--------|
| `leads` | Replaced by metadata-driven tx_lead |
| `opportunities` | Replaced by metadata-driven tx_opportunity |

## Migration Files

| Migration File | Contents |
|---------------|----------|
| `V{next}__drop_legacy_crm_tables.sql` | Drop leads, opportunities tables |
| `V{next}__create_sales_territory_table.sql` | Create md_sales_territory + metadata |
| `V{next}__create_lead_table.sql` | Create tx_lead + metadata |
| `V{next}__create_opportunity_table.sql` | Create tx_opportunity + metadata |
| `V{next}__create_commission_table.sql` | Create tx_commission + metadata |
| `V{next}__seed_crm_forms.sql` | Insert form definitions for all 5 forms |
| `V{next}__add_territory_to_partner.sql` | Add territory_id to md_business_partner |

---

# Acceptance Criteria

1. All 4 new tables created in PostgreSQL
2. All table metadata inserted
3. All 5 form definitions inserted
4. Lead scoring auto-calculates correctly
5. Lead qualification workflow enforced
6. Opportunity pipeline stages enforced
7. Lead → Opportunity conversion works
8. Opportunity → Quotation conversion works
9. Commission calculated on closed opportunities
10. CRM dashboard displays pipeline and team performance
11. Legacy leads/opportunities tables dropped
12. Hardcoded crm/ module deleted
13. Server verification passes

---

# Future Enhancements

1. Marketing campaign management
2. Lead import from CSV/Excel
3. Email integration
4. Activity tracking (calls, meetings, tasks)
5. Advanced lead scoring (ML-based)
6. Mobile CRM app

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial PRD — Lead/Opportunity pipeline, sales team, commission, CRM dashboard | 2026-07-29 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-006 — Sales Quotation & Price Management](../prd/PRD-006-sales-quotation-price-management.md)
- [PRD-007 — Sales Order Workflow & Customer Management](../prd/PRD-007-sales-order-workflow-customer-management.md)
