---
id: CHANGE-TASK-064
task_id: TASK-064
parent_prd: PRD-006
type: Feature
status: IMPLEMENTED
related_files:
  - backend/.../common/service/PriceResolutionService.java
  - backend/.../common/controller/PricingController.java
  - backend/.../common/dto/PriceResolveRequest.java
  - backend/.../common/dto/PriceResolveResponse.java
---

# Summary
PriceResolutionService: resolves unit price via customer PL → default PL → product base. POST /api/v1/pricing/resolve endpoint. 5-min cache.

# Validation
- default PL returns price=1200.00 for PROD-LAP-001
- Unknown product → 404 NOT_FOUND
- mvn test 36/36 BUILD SUCCESS
