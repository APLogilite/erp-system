# Dynamic ERP Backend

Spring Boot 3.3.4 backend for the Dynamic ERP system. Runs on port `8081`.

## Technology Stack

- **Framework**: Spring Boot 3.3.4
- **Language**: Java 17
- **Database**: PostgreSQL  (H2 in PostgreSQL compatibility mode for tests)
- **Build Tool**: Maven
- **Migrations**: Flyway (V1-V23 + undo scripts)
- **Auth**: JWT + Spring Security
- **Architecture**: Modular monolith with metadata-driven form engine

## Project Structure

```
backend/
├── src/main/java/com/erp/
│   ├── ErpApplication.java        # Main application class
│   ├── IdentitySeedData.java      # Demo data seeder (runs at startup)
│   ├── SeedData.java              # Additional seed data
│   ├── common/                    # Shared utilities and base classes
│   │   ├── base/                  # BaseEntity, BaseService, BaseController
│   │   └── utils/                 # Utility classes
│   ├── config/                    # Configuration (security, JWT, API versioning, CORS)
│   ├── core/                      # Core infrastructure (exceptions, filters, audit)
│   ├── codegen/                   # CodeGenerator.java (DTO + Service scaffolding)
│   ├── platform/                  # Platform modules
│   │   └── identity/              # Auth, RBAC, multi-tenant admin hierarchy
│   │       ├── entity/            # Tenant, Org, Company, Branch, Dept, User, Role, Permission
│   │       ├── repository/
│   │       ├── service/           # JwtProvider, PasswordService, PermissionCache, PermissionEvaluator
│   │       ├── security/          # JWT auth filter, security config
│   │       └── controller/        # Auth, user, role, permission REST APIs
│   └── modules/                   # 19 business modules
│       ├── accounting/            # Chart of accounts, journal entries
│       ├── analytics/             # Dashboards, KPI definitions, scheduled reports
│       ├── assets/                # Fixed asset management
│       ├── auth/                  # Entity-based auth (maps to identity)
│       ├── businesspartner/       # Customers, suppliers, contacts, addresses
│       ├── crm/                   # Leads, opportunities, contacts
│       ├── hr/                    # Employees, departments
│       ├── inventory/             # Stock movements, balances, transactions
│       ├── manufacturing/         # BOM, work orders, routing, work centers
│       ├── order/                 # Sales/purchase orders, order lines
│       ├── product/               # Products, categories
│       ├── projects/              # Projects, tasks
│       ├── purchase/              # Purchase-specific workflows
│       ├── reservation/           # Inventory reservations
│       ├── sales/                 # Sales-specific workflows
│       ├── service/               # Service requests
│       ├── users/                 # User entity management
│       └── warehouse/             # Warehouses, locations
├── src/main/resources/
│   ├── application.properties            # Main configuration (port 8081, PostgreSQL, Flyway disabled)
│   ├── application-local.properties      # Local DB credentials (gitignored)
│   └── db/migration/                     # 34 Flyway migrations (V1-V23 + U3-U13 undo)
├── src/test/
│   ├── java/com/erp/
│   │   ├── DatabaseConnectionTest.java   # Integration test (H2, 3 tests)
│   │   └── platform/identity/            # Unit tests (36 total)
│   │       ├── security/JwtProviderTest
│   │       ├── service/PasswordServiceTest
│   │       └── authorization/
│   │           ├── PermissionCacheTest
│   │           └── PermissionEvaluatorTest
│   └── resources/application.properties  # H2 test config (Flyway disabled)
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (running on port 5432)

### Configuration

1. Copy `src/main/resources/application-local.properties` and configure:
   ```properties
   spring.datasource.username=erp_user
   spring.datasource.password=erp_password
   ```

2. Or set environment variables:
   ```bash
   export DB_USERNAME=erp_user
   export DB_PASSWORD=erp_password
   ```

### Running the Application

```bash
# Build the application
mvn clean compile

# Run tests (H2 in-memory, no PostgreSQL needed)
mvn test

# Run the application (requires PostgreSQL)
mvn spring-boot:run
```

The application will start on `http://localhost:8081`.

### Flyway Migrations

Flyway is **disabled by default** (`spring.flyway.enabled=false`). JPA's `ddl-auto=update` handles schema creation for development.

To seed metadata tables and ERP data, enable Flyway:
```properties
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

This runs 34 migrations:
- **V1-V14**: Identity schema, metadata tables (form engine infrastructure)
- **V15-V18**: Admin configuration forms for managing metadata entities
- **V19-V23**: ERP order flow — master data and transaction tables + forms
- **U3-U13**: Rollback scripts for undo operations

## Architecture

### Metadata-Driven Form Engine

Forms are defined at runtime via metadata tables — no code changes needed to add new forms:

1. **sys_metadata_models** — Table definitions (name, label, type)
2. **sys_table_columns** — Column definitions (type, validation, relations)
3. **sys_form_fields** — Form field definitions (position, visibility, rules)
4. **sys_form_layout_sections** — Layout sections (tabs, columns)
5. **sys_form_sub_forms** — Sub-form (one2many) configurations
6. **sys_form_field_rules** — Field-level rules (show/hide, require)
7. **sys_form_field_validations** — Field validation rules
8. **sys_form_role_filters** — Row-level data access filters per role
9. **sys_form_tenant_role** — Per-tenant role access to forms

Runtime flow: The frontend requests a form definition bundle (cached 5 min) + fresh data in two requests. The DynamicFormRenderer renders forms from metadata.

### Multi-Tenancy

Data isolation via tenant hierarchy: `Tenant → Org → Company → Branch → Dept`

- Enforced at the database level via Hibernate `@Filter` annotations
- `sys_admin` role bypasses all filters (cross-tenant access)
- Every entity inheriting from `BaseEntity` includes `tenant_id`

### Modules

The backend has 19 business modules + 1 platform module:

- **Platform**: `identity/` — Authentication, authorization, RBAC, multi-tenant hierarchy
- **Core Business**: `order/`, `product/`, `inventory/`, `warehouse/`, `businesspartner/`
- **Commerce**: `sales/`, `purchase/`, `reservation/`
- **Operations**: `manufacturing/`, `projects/`, `service/`, `assets/`
- **Support**: `accounting/`, `analytics/`, `crm/`, `hr/`, `users/`, `auth/`

Each module follows a standard layered structure: `controller/ → dto/ → entity/ → repository/ → service/`.

### Code Generation

Use `CodeGenerator.java` to scaffold DTO + Service for new entities:
```bash
mvn exec:java
```

Edit the hardcoded entity list in `CodeGenerator.main()` to add more.

## API

- **Base URL**: `http://localhost:8081/api/v1`
- **Envelope**: All endpoints return `ApiResponse<T>` (`{ success, data, message, errorCode, details }`)
- **Auth**: JWT Bearer token in `Authorization` header
- **Endpoints**:
  - `POST /api/v1/auth/login` — Login, returns JWT
  - `POST /api/v1/auth/refresh` — Refresh token
  - `GET /api/v1/users/me` — Current user profile
  - `GET/POST/PUT/DELETE /api/v1/{module}/{entity}` — CRUD for all entities

## Testing

```bash
# Run all tests (H2 in-memory, no PostgreSQL needed)
mvn test

# Run a specific test class
mvn test -Dtest=DatabaseConnectionTest
```

36 tests total:
| Test Class | Tests | Description |
|-----------|-------|-------------|
| DatabaseConnectionTest | 3 | Integration: connection, table existence, queryability |
| JwtProviderTest | 5 | JWT token generation and validation |
| PasswordServiceTest | 13 | Password hashing, validation, policy |
| PermissionCacheTest | 6 | Permission caching and invalidation |
| PermissionEvaluatorTest | 9 | Permission evaluation logic |

**Test database**: H2 in PostgreSQL compatibility mode (`jdbc:h2:mem:testdb;MODE=PostgreSQL`).

## Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8081 | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/erp_db` | Database URL |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema generation |
| `spring.flyway.enabled` | `false` | Flyway migrations (enable for seed data) |
| `app.jwt.secret` | (env var) | JWT signing key |
| `app.jwt.access-token-expiration-ms` | 900000 | Access token TTL (15 min) |
| `app.jwt.refresh-token-expiration-ms` | 604800000 | Refresh token TTL (7 days) |

### Profiles

- **default**: Production configuration (PostgreSQL, JWT via env vars)
- **local**: Auto-imported from `application-local.properties` (DB credentials)

## Demo Users

| Username | Password | Role | Scope |
|----------|----------|------|-------|
| `admin` | `Admin@123` | `sys_admin` | Cross-tenant |
| `jane.smith` | `User@123` | `tnt_admin` | ACME tenant |
| `diana.prince` | `User@123` | `tnt_admin` | Globex tenant |
| `john.doe` | `User@123` | `user` | ACME |
| `alice.johnson` | `User@123` | `user` | ACME |
| `bob.wilson` | `User@123` | `viewer` | Globex |
| `charlie.brown` | `User@123` | `user` | Globex |

## Development

1. Fork the repository and create a feature branch from the relevant PRD branch
2. Follow existing code conventions (BaseEntity, BaseService pattern)
3. Add tests for new features
4. Run `mvn test` — all 36 must pass
5. Update API documentation
6. Create a change report in `ai/changes/`
