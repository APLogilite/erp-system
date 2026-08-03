---
id: TASK-064
title: Implement Price Resolution backend service
type: Feature
scope: backend
status: PLANNED
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 4
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-061
  - TASK-067
depends_on:
  - TASK-061
blocks:
  - TASK-067
labels:
  - backend
  - prd-006
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Automatically resolve the correct unit price for a product given customer and quantity, using customer-specific price lists first, then the default price list, then the product's base price.

---

# Description

Implement `PriceResolutionService` in the backend with REST endpoint. Resolution order:

1. Find active price list where `partner_id` = customer AND valid today (valid_from ≤ today ≤ valid_to or null)
2. If none, find price list where `is_default` = true AND valid today
3. Within the resolved price list, find lines matching `product_id`; pick the line with the highest `min_quantity` ≤ requested quantity (volume pricing)
4. If no matching line, fall back to `md_product.unit_price`

REST endpoint (new controller `PricingController`):
```
POST /api/v1/pricing/resolve
Request:  { "productId": "uuid", "partnerId": "uuid", "quantity": 10 }
Response: { "unitPrice": 95.00, "priceListId": "uuid", "source": "customer_price_list" }
```
`source` is one of: `customer_price_list`, `default_price_list`, `product_base`.

Cache resolved price lists in memory with 5-minute TTL (Spring `@Cacheable` or simple ConcurrentHashMap with timestamps) to avoid repeated DB hits during line entry.

---

# Acceptance Criteria

- [ ] `[SE]` Customer-specific price list takes priority over default price list
- [ ] `[SE]` Volume pricing: quantity above a line's min_quantity uses that line's price
- [ ] `[SE]` Falls back to md_product.unit_price when no price list matches
- [ ] `[SE]` Expired price lists (valid_to < today) are ignored
- [ ] `[SE]` Response includes source indicator
- [ ] `[QA]` Unknown productId returns 404
- [ ] `[SE][QA]` Backend starts cleanly (no errors in /tmp/erp-backend.log)

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Query md_price_list / md_price_list_line directly via JPA entities or JdbcTemplate (these are metadata-driven tables; use the same data access approach as other business services)
- Reference PRD-006 FR-002 (Price Resolution Logic)

---

# Files Expected

- `backend/src/main/java/com/erp/modules/pricing/service/PriceResolutionService.java` (new)
- `backend/src/main/java/com/erp/modules/pricing/controller/PricingController.java` (new)
- `backend/src/main/java/com/erp/modules/pricing/dto/PriceResolveRequest.java` (new)
- `backend/src/main/java/com/erp/modules/pricing/dto/PriceResolveResponse.java` (new)

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-006 approved by user

---

# Related Documents

- PRD-006 — Sales Quotation & Price Management (FR-002)
