---
id: CHANGE-TASK-065
task_id: TASK-065
parent_prd: PRD-006
type: Feature
status: IMPLEMENTED
related_files:
  - backend/.../common/service/DiscountResolutionService.java
  - backend/.../common/dto/PriceResolveResponse.java (extended)
  - backend/.../common/controller/PricingController.java (integrated)
---

# Summary
DiscountResolutionService: finds best discount rule (percentage or fixed); integrated into POST /api/v1/pricing/resolve.

# Validation
- VOLUME10 rule → 10% off $1200 = $120 discount ✅
- 36/36 tests BUILD SUCCESS
