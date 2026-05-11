Using PROJECT_RULES.md,

IMPORTANT CONTEXT:
We are implementing a unified Order system inspired by:
- Odoo sale.order + purchase.order abstraction
- iDempiere C_Order with document type concept

We already have:
- BaseEntity (UUID, audit, isActive soft delete)
- BaseService (generic CRUD + hooks)

DO NOT modify base architecture.

------------------------------------------------------------

Create Order module in package:
com.erp.modules.order

------------------------------------------------------------

ENTITY 1: Order (header/document)

Fields:
- String orderNumber (unique business identifier)
- String orderType (SALES / PURCHASE)
- UUID partyId (customerId or vendorId depending on orderType)
- LocalDateTime orderDate
- String status (DRAFT / CONFIRMED / COMPLETED / CANCELLED)
- Double totalAmount

Rules:
- orderNumber must be unique
- orderType must not be null
- status default = DRAFT

------------------------------------------------------------

ENTITY 2: OrderLine

Fields:
- UUID orderId
- UUID productId
- Double quantity
- Double unitPrice
- Double lineTotal

Rules:
- lineTotal = quantity * unitPrice

------------------------------------------------------------

REPOSITORY:

OrderRepository:
- Optional<Order> findByOrderNumber(String orderNumber)
- List<Order> findByIsActiveTrue()
- List<Order> findByOrderType(String orderType)

OrderLineRepository:
- List<OrderLine> findByOrderId(UUID orderId)

------------------------------------------------------------

SERVICE:

OrderService extends BaseService<Order>

Business rules:
- beforeCreate:
  - generate orderNumber based on type:
    SALES → SO-0001
    PURCHASE → PO-0001

- afterCreate:
  - validate OrderLines
  - ensure quantity > 0
  - compute lineTotal
  - compute totalAmount (sum of lines)

- beforeUpdate:
  - prevent update if status = COMPLETED

------------------------------------------------------------

INTEGRATION RULE (IMPORTANT):

When Order is CONFIRMED:

IF orderType = SALES:
- Create StockMovement:
  - quantity = negative (OUT)
  - movementType = SALE
  - referenceType = SALES_ORDER
  - referenceId = orderId

IF orderType = PURCHASE:
- Create StockMovement:
  - quantity = positive (IN)
  - movementType = PURCHASE
  - referenceType = PURCHASE_ORDER
  - referenceId = orderId

------------------------------------------------------------

CONTROLLER:

Base path: /api/v1/orders

Endpoints:
- POST   /api/v1/orders
- GET    /api/v1/orders
- GET    /api/v1/orders/{id}
- PUT    /api/v1/orders/{id}
- POST   /api/v1/orders/{id}/confirm
- DELETE /api/v1/orders/{id} (soft delete only if DRAFT)

------------------------------------------------------------

GOAL:
Create a unified ERP Order system using orderType (SALES / PURCHASE) that:
- maintains document lifecycle (DRAFT → CONFIRMED → COMPLETED)
- drives inventory movement via StockMovement
- avoids duplication of Sales and Purchase modules
- follows ERP document-based architecture inspired by Odoo and iDempiere