---
id: CHANGE-TASK-096
task_id: TASK-096
parent_prd: PRD-010
branch: feature/TASK-096
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-26
completed: 2026-08-26
duration: 1.5h
related_commits:
  - TBD
related_files:
  - backend/src/main/java/com/erp/modules/purchase/entity/PurchasePriceList.java
  - backend/src/main/java/com/erp/modules/purchase/entity/PurchasePriceListLine.java
  - backend/src/main/java/com/erp/modules/purchase/repository/PurchasePriceListRepository.java
  - backend/src/main/java/com/erp/modules/purchase/repository/PurchasePriceListLineRepository.java
  - backend/src/main/java/com/erp/modules/purchase/service/PurchasePriceResolutionService.java
  - backend/src/main/java/com/erp/modules/purchase/controller/PurchasePriceListController.java
  - backend/src/main/java/com/erp/modules/purchase/dto/PriceResolutionRequest.java
  - backend/src/main/java/com/erp/modules/purchase/dto/PriceResolutionResponse.java
  - backend/src/main/java/com/erp/modules/product/entity/Product.java
review_required: true
test_required: true
---

# Summary

Implemented a backend purchase price resolution service that selects the correct unit price from a vendor's active purchase price list by quantity break, falling back to the product's base unit price when no price-list match exists. Exposed via `POST /api/v1/purchase/price-lists/resolve` with `ApiResponse<T>` envelope.

---

# Scope Verification

- [ ] Frontend
- [x] Backend
- [x] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-005 from PRD-010: Purchase price resolution service
- Returns highest applicable min-qty price
- Falls back to product `unit_price`
- Tenant isolation via existing Hibernate `@Filter` on `BaseEntity.tenantId`

---

# Files Added

| File | Purpose |
|------|---------|
| `PurchasePriceList.java` | JPA entity for `pur_price_list` |
| `PurchasePriceListLine.java` | JPA entity for `pur_price_list_line` |
| `PurchasePriceListRepository.java` | Query active price lists by vendor/date |
| `PurchasePriceListLineRepository.java` | Query matching lines by quantity break |
| `PurchasePriceResolutionService.java` | Price resolution logic |
| `PurchasePriceListController.java` | REST endpoint |
| `PriceResolutionRequest.java` | Request DTO |
| `PriceResolutionResponse.java` | Response DTO |

---

# Files Modified

| File | Summary |
|------|---------|
| `Product.java` | Added `unitPrice` (`BigDecimal`) field to support fallback |

---

# Database Changes

## Tables Added

None.

## Tables Modified

None (JPA entities mapped to existing `pur_price_list`/`pur_price_list_line` tables).

---

# API Changes

## New Endpoints

- `POST /api/v1/purchase/price-lists/resolve`
  - Request: `{ vendorId, productId, uomId?, quantity, date? }`
  - Response: `ApiResponse<PriceResolutionResponse>`

---

# Validation

## Build

PASS

Notes: `mvn clean compile` succeeded.

## Tests

- Existing backend tests still pass.
- Unit tests for price resolution are **out of scope** per role; QA will cover acceptance criteria.

---

# Manual Verification

- Verified endpoint path matches PRD requirement.
- Verified BigDecimal usage for monetary values.
- Verified null date defaults to today.

---

# Breaking Changes

None.

---

# Known Issues

None.

---

# Related Documents

- Task: `ai/project/tasks/TASK-096-implement-purchase-price-resolution-service.md`
- PRD: `ai/project/prd/PRD-010-purchase-management.md`
