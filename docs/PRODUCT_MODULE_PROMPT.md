Using PROJECT_RULES.md,

IMPORTANT CONTEXT:
We are building an ERP system inspired by Odoo and iDempiere domain modeling.

We already have:
- BaseEntity (UUID, audit fields, isActive soft delete)
- BaseService (generic CRUD, soft delete, hooks)

DO NOT modify base architecture.

------------------------------------------------------------

Create Product module in package:
com.erp.modules.product

------------------------------------------------------------

ENTITY DESIGN (Odoo-inspired product.product):

Create Product entity extending BaseEntity:

Fields:
- String name (required)
- String sku (unique, business identifier)
- String description
- String category
- String uom (unit of measure)
- String type (STOCKABLE / SERVICE)
- Double costPrice
- Double salePrice

Rules:
- sku must be unique
- name must not be null
- costPrice >= 0
- salePrice >= 0

------------------------------------------------------------

REPOSITORY:

- Extend JpaRepository<Product, UUID>
- Methods:
  Optional<Product> findBySku(String sku)
  List<Product> findByIsActiveTrue()

------------------------------------------------------------

SERVICE:

- Extend BaseService<Product>
- Implement getRepository()

Business rules:
- beforeCreate: validate SKU uniqueness
- beforeUpdate: ensure SKU is not duplicated

DO NOT implement CRUD logic (inherit from BaseService)

------------------------------------------------------------

CONTROLLER:

Base path: /api/v1/products

Endpoints:
- POST   /api/v1/products
- GET    /api/v1/products
- GET    /api/v1/products/{id}
- PUT    /api/v1/products/{id}
- DELETE /api/v1/products/{id} (soft delete)

Rules:
- Use service only
- Never access repository directly
- Return only active products

------------------------------------------------------------

DTOs:

- ProductRequestDTO
- ProductResponseDTO

Rules:
- Never expose entity directly
- Use validation annotations (@NotNull, @PositiveOrZero)

------------------------------------------------------------

GOAL:
Create a clean Product module inspired by Odoo product model with SKU-based business identity and BaseEntity-based system identity.