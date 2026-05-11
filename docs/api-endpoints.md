# ERP System API Endpoints

This document provides examples of REST API endpoints available in the ERP system. All endpoints use JSON for request/response bodies and follow RESTful conventions.

## Base URL
```
http://localhost:8080/api/v1
```

## Authentication
Currently, no authentication is implemented. All endpoints are open.

## Common Response Format
### Success Response
```json
{
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2026-05-11T10:00:00Z"
}
```

### Error Response
```json
{
  "error": "Error message",
  "timestamp": "2026-05-11T10:00:00Z"
}
```

---

## Product Module

### Get All Products
**GET** `/products`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Laptop",
    "sku": "LAP001",
    "description": "High-performance laptop",
    "category": "Electronics",
    "uom": "Piece",
    "type": "STOCKABLE",
    "costPrice": 800.00,
    "salePrice": 1200.00,
    "isActive": true,
    "createdAt": "2026-05-11T09:00:00Z",
    "updatedAt": "2026-05-11T09:00:00Z"
  }
]
```

### Get Product by ID
**GET** `/products/{id}`

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Laptop",
  "sku": "LAP001",
  "description": "High-performance laptop",
  "category": "Electronics",
  "uom": "Piece",
  "type": "STOCKABLE",
  "costPrice": 800.00,
  "salePrice": 1200.00,
  "isActive": true,
  "createdAt": "2026-05-11T09:00:00Z",
  "updatedAt": "2026-05-11T09:00:00Z"
}
```

### Create Product
**POST** `/products`

**Request:**
```json
{
  "name": "Wireless Mouse",
  "sku": "MOU002",
  "description": "Ergonomic wireless mouse",
  "category": "Electronics",
  "uom": "Piece",
  "type": "STOCKABLE",
  "costPrice": 20.00,
  "salePrice": 35.00
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Wireless Mouse",
  "sku": "MOU002",
  "description": "Ergonomic wireless mouse",
  "category": "Electronics",
  "uom": "Piece",
  "type": "STOCKABLE",
  "costPrice": 20.00,
  "salePrice": 35.00,
  "isActive": true,
  "createdAt": "2026-05-11T10:00:00Z",
  "updatedAt": "2026-05-11T10:00:00Z"
}
```

### Update Product
**PUT** `/products/{id}`

**Request:**
```json
{
  "name": "Wireless Mouse Pro",
  "sku": "MOU002",
  "description": "Professional ergonomic wireless mouse",
  "category": "Electronics",
  "uom": "Piece",
  "type": "STOCKABLE",
  "costPrice": 25.00,
  "salePrice": 40.00
}
```

**Response:** Same as create response

### Delete Product
**DELETE** `/products/{id}`

**Response:** HTTP 204 No Content

---

## Inventory Module

### Get All Warehouses
**GET** `/inventory/warehouses`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "Main Warehouse",
    "location": "New York",
    "isActive": true,
    "createdAt": "2026-05-11T09:00:00Z",
    "updatedAt": "2026-05-11T09:00:00Z"
  }
]
```

### Get Warehouse by ID
**GET** `/inventory/warehouses/{id}`

**Response:** Single warehouse object

### Create Warehouse
**POST** `/inventory/warehouses`

**Request:**
```json
{
  "name": "Secondary Warehouse",
  "location": "Los Angeles"
}
```

**Response:** Created warehouse object

### Update Warehouse
**PUT** `/inventory/warehouses/{id}`

**Request:**
```json
{
  "name": "Secondary Warehouse",
  "location": "Los Angeles, CA"
}
```

### Get Stock Level
**GET** `/inventory/stock/{productId}/{warehouseId}`

**Response:**
```json
{
  "stock": 150.0
}
```

### Get Stock Movements
**GET** `/inventory/stock-movements`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "productId": "550e8400-e29b-41d4-a716-446655440000",
    "warehouseId": "550e8400-e29b-41d4-a716-446655440002",
    "quantity": 100.0,
    "movementType": "PURCHASE",
    "referenceType": "PURCHASE_ORDER",
    "movementDate": "2026-05-11T09:00:00Z",
    "isActive": true,
    "createdAt": "2026-05-11T09:00:00Z",
    "updatedAt": "2026-05-11T09:00:00Z"
  }
]
```

---

## Order Module

### Get All Orders
**GET** `/orders`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440004",
    "orderNumber": "SO001",
    "orderType": "SALES",
    "partyId": "550e8400-e29b-41d4-a716-446655440005",
    "orderDate": "2026-05-11T09:00:00Z",
    "status": "CONFIRMED",
    "totalAmount": 1225.00,
    "isActive": true,
    "createdAt": "2026-05-11T09:00:00Z",
    "updatedAt": "2026-05-11T09:00:00Z"
  }
]
```

### Get Order by ID
**GET** `/orders/{id}`

**Response:** Single order object

### Create Order
**POST** `/orders`

**Request:**
```json
{
  "orderNumber": "SO002",
  "orderType": "SALES",
  "partyId": "550e8400-e29b-41d4-a716-446655440005",
  "orderDate": "2026-05-11T10:00:00Z",
  "status": "DRAFT",
  "totalAmount": 2400.00
}
```

### Get Order Lines
**GET** `/orders/{orderId}/lines`

**Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440006",
    "orderId": "550e8400-e29b-41d4-a716-446655440004",
    "productId": "550e8400-e29b-41d4-a716-446655440000",
    "quantity": 1.0,
    "unitPrice": 1200.00,
    "lineTotal": 1200.00,
    "isActive": true,
    "createdAt": "2026-05-11T09:00:00Z",
    "updatedAt": "2026-05-11T09:00:00Z"
  }
]
```

---

## Error Handling

### 400 Bad Request
```json
{
  "error": "Validation failed",
  "details": [
    "SKU must be unique",
    "Name is required"
  ],
  "timestamp": "2026-05-11T10:00:00Z"
}
```

### 404 Not Found
```json
{
  "error": "Entity not found for id: 550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-05-11T10:00:00Z"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error",
  "timestamp": "2026-05-11T10:00:00Z"
}
```

## Notes
- All UUIDs are generated automatically
- Timestamps are in ISO 8601 format
- Soft delete is implemented (is_active = false)
- All monetary values are in decimal format
- Pagination and filtering will be added in future versions