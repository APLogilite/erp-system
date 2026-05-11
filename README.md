# ERP System

Modular ERP system built with Spring Boot, designed for scalability and enterprise use. Features comprehensive inventory, order, and product management with a clean, extensible architecture.

## Features Completed ✅

### Core Architecture
- **Modular Design**: Separate modules for Auth, Product, Inventory, Order, and Users
- **Base Entity Framework**: UUID-based entities with auditing (created_at, updated_at, created_by, updated_by, is_active, deleted_at)
- **Generic Services**: BaseService with common CRUD operations
- **REST API Controllers**: DTO-based endpoints with proper HTTP status codes

### Database & Persistence
- **PostgreSQL Integration**: Production-ready database configuration
- **H2 for Testing**: In-memory database with PostgreSQL compatibility mode
- **JPA/Hibernate**: Entity mappings with automatic schema updates
- **SQL Scripts**: Schema creation and sample data in `/db` folder
- **Code Generation**: Automated DTO and Service class generation

### Modules Implemented
- **Auth Module**: User authentication entities and services
- **Product Module**: Product catalog with SKU, pricing, categories
- **Inventory Module**: Warehouse management and stock movements
- **Order Module**: Sales and purchase order processing
- **Users Module**: User management entities

### Testing & Quality
- **Unit Tests**: Database connection and schema validation tests
- **Test Configuration**: Separate H2 setup for reliable testing
- **Build Success**: All tests passing with Maven

## Tech Stack
- **Backend**: Spring Boot 3.3.4, Java 17
- **Database**: PostgreSQL (production), H2 (testing)
- **Build Tool**: Maven
- **ORM**: Hibernate/JPA
- **Testing**: JUnit 5, AssertJ

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production)

### Database Setup
1. **Install PostgreSQL** (if not already installed):
   ```bash
   # Ubuntu/Debian
   sudo apt update
   sudo apt install postgresql postgresql-contrib

   # Or for other systems, follow PostgreSQL installation guide
   ```

2. **Setup Database and User**:
   ```bash
   # Copy the template and customize with your credentials
   cp db-setup-template.sql db-setup.sql
   # Edit db-setup.sql with your preferred password

   # Run the database setup script as postgres user
   sudo -u postgres psql -f db-setup.sql
   ```

3. **Verify Database Connection**:
   ```bash
   # Test connection
   psql -h localhost -U erp_user -d erp_db -c "SELECT version();"
   ```

### Build and Run
```bash
# Clone repository
git clone <repository-url>
cd erp-system

# Build
mvn clean compile

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### Configuration
- **Database**: PostgreSQL connection configured in `application.properties`
- **Port**: Server runs on port 8081 (configurable via `SERVER_PORT` environment variable)
- **Profiles**: Use `--spring.profiles.active=local` for local development with `application-local.properties`
- **Security**: `application-local.properties` and `db-setup.sql` are gitignored for security

### Environment Variables
Override default configuration using environment variables:
```bash
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export SERVER_PORT=8082
mvn spring-boot:run
```

### API Documentation
See [API Endpoints](api-endpoints.md) for detailed REST API documentation with request/response examples.

## Development

### Code Generation
Generate DTO and Service classes for new entities:
```bash
mvn compile exec:java
```

### Database Schema Updates
- Development: `spring.jpa.hibernate.ddl-auto=update` automatically syncs schema
- Production: Use migration scripts (Flyway/Liquibase planned)

### Testing
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=DatabaseConnectionTest
```

## Project Structure
```
src/
├── main/
│   ├── java/com/erp/
│   │   ├── common/base/          # BaseEntity, BaseService
│   │   ├── config/               # Database, API version configs
│   │   ├── codegen/              # Code generation utilities
│   │   └── modules/              # Feature modules
│   │       ├── auth/
│   │       ├── inventory/
│   │       ├── order/
│   │       ├── product/
│   │       └── users/
│   └── resources/                # Application properties
└── test/                         # Test classes and configs

db/                               # Database scripts
├── schema.sql                    # Table creation
├── data.sql                      # Sample data
└── README.md                     # Database documentation
```

## Status
🟢 **Phase 1 Complete**: Basic ERP foundation with all core modules implemented and tested.

### Next Steps (Phase 2)
- [ ] Frontend React application
- [ ] Advanced inventory features (stock levels, alerts)
- [ ] Order workflow and status management
- [ ] User authentication and authorization
- [ ] API documentation with Swagger
- [ ] Docker containerization
- [ ] CI/CD pipeline

## Contributing
This project uses AI-assisted development. Follow the established patterns:
- Extend BaseEntity for new entities
- Use BaseService for CRUD operations
- Implement DTOs for API contracts
- Add comprehensive tests

## License
See LICENSE file for details.
