# Dynamic ERP Backend

Spring Boot-based backend for the Dynamic ERP system.

## Overview

This backend provides REST APIs for managing ERP operations including authentication, inventory, orders, products, and users.

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL/MySQL (configurable)
- **Build Tool**: Maven
- **Architecture**: Layered architecture with controllers, services, repositories

## Project Structure

```
backend/
├── src/main/java/com/erp/
│   ├── ErpApplication.java          # Main application class
│   ├── common/                      # Shared utilities and base classes
│   │   ├── base/                    # BaseEntity, BaseService
│   │   └── utils/                   # Utility classes
│   ├── config/                      # Configuration classes
│   ├── modules/                     # Business modules
│   │   ├── auth/                    # Authentication module
│   │   ├── inventory/               # Inventory management
│   │   ├── order/                   # Order processing
│   │   ├── product/                 # Product management
│   │   └── users/                   # User management
│   └── codegen/                     # Code generation utilities
├── src/main/resources/
│   ├── application.properties       # Main configuration
│   ├── application-local.properties # Local overrides
│   └── db/                          # Database scripts
└── src/test/                        # Unit tests
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL or MySQL database

### Configuration

1. Copy `src/main/resources/application-local.properties` and configure:
   - Database connection settings
   - Other environment-specific properties

2. Update database settings in `application.properties` if needed

### Running the Application

```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Database Setup

Run the SQL scripts in `src/main/resources/db/` to set up the database schema and initial data.

## API Documentation

API endpoints are documented in the `docs/api-endpoints.md` file.

## Demo Users

On first startup, `IdentitySeedData` creates test users for development:

| Username | Password | Role | Tenant | Scope |
|----------|----------|------|--------|-------|
| `admin` | `Admin@123` | `sys_admin` | All tenants | Full cross-tenant access |
| `jane.smith` | `User@123` | `tnt_admin` | ACME | ACME tenant administration |
| `diana.prince` | `User@123` | `tnt_admin` | Globex | Globex tenant administration |
| `john.doe` | `User@123` | `user` | ACME | Regular ACME user |
| `alice.johnson` | `User@123` | `user` | ACME | Regular ACME user |
| `bob.wilson` | `User@123` | `viewer` | Globex | Read-only Globex user |
| `charlie.brown` | `User@123` | `user` | Globex | Regular Globex user |

**Data isolation:** All API endpoints enforce tenant-scoped access via Hibernate `@Filter` annotations. Users only see entities belonging to their tenant. The `sys_admin` role bypasses all filters.

## Modules

### Authentication (`auth`)
- User login/logout
- JWT token management (includes tenant/org context)
- Role-based access control

### Inventory (`inventory`)
- Stock management
- Warehouse operations
- Stock movements tracking

### Orders (`order`)
- Sales and purchase order management
- Order line items
- Order status tracking

### Products (`product`)
- Product catalog management
- Product categories
- Pricing information

### Users (`users`)
- User profile management
- User roles and permissions

## Development

### Code Generation

Use the `CodeGenerator.java` class to generate boilerplate code for new entities.

### Base Classes

- `BaseEntity`: Provides common fields (id, timestamps)
- `BaseService`: Provides common CRUD operations

### Testing

Run unit tests with:
```bash
mvn test
```

## Configuration

The application supports multiple profiles:
- `default`: Production configuration
- `local`: Development configuration (loaded from `application-local.properties`)

## Contributing

1. Follow the existing code structure
2. Add tests for new features
3. Update API documentation
4. Ensure code compiles and tests pass