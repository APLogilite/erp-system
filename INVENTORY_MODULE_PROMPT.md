Using PROJECT_RULES.md,

IMPORTANT CONTEXT:
ERP follows iDempiere/Odoo-inspired inventory design:
- Stock is NOT stored as a single column
- Stock is calculated from movement ledger (transaction-based model)

We already have:
- BaseEntity (UUID, audit, isActive soft delete)
- BaseService (generic CRUD + hooks)

DO NOT change base architecture.

------------------------------------------------------------

Create Inventory module in package:
com.erp.modules.inventory

------------------------------------------------------------

ENTITY 1: Warehouse

Fields:
- String name
- String location

------------------------------------------------------------

ENTITY 2: StockMovement (CORE iDempiere concept)

Fields:
- UUID productId
- UUID warehouseId
- Double quantity (positive = IN, negative = OUT)
- String movementType (PURCHASE / SALE / ADJUSTMENT / RETURN)
- UUID referenceId (order or manual reference)
- String referenceType (SALES_ORDER / PURCHASE_ORDER / MANUAL)
- LocalDateTime movementDate

Rules:
- This is the ONLY source of truth for stock
- No direct stock quantity column allowed

------------------------------------------------------------

REPOSITORY:

StockMovementRepository:
- List<StockMovement> findByProductId(UUID productId)
- List<StockMovement> findByWarehouseId(UUID warehouseId)

WarehouseRepository:
- List<Warehouse> findByIsActiveTrue()

------------------------------------------------------------

SERVICE:

- Extend BaseService<StockMovement>
- Implement getRepository()

Business logic:
- No stock calculation stored in DB
- Stock is derived from SUM(quantity)

Add method:
- Double getCurrentStock(UUID productId, UUID warehouseId)

------------------------------------------------------------

CONTROLLER:

Stock endpoints:
- GET /api/v1/inventory/stock/{productId}/{warehouseId}
- POST /api/v1/inventory/movement

Warehouse endpoints:
- CRUD for warehouses

------------------------------------------------------------

GOAL:
Build a ledger-based inventory system inspired by iDempiere MTransaction model, ensuring full traceability of stock movement instead of static quantity storage.