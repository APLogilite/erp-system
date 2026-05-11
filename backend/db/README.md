# Database Scripts

This folder contains SQL scripts for setting up the ERP database.

## Files

- `schema.sql`: Creates the database tables and initial schema.
- `data.sql`: Inserts sample data for testing and development.

## Usage

These scripts are automatically run by Spring Boot on application startup if configured in `application.properties`.

For manual execution:

1. Connect to PostgreSQL database.
2. Run `schema.sql` first to create tables.
3. Run `data.sql` to insert sample data.

## Schema Changes

When making changes to entity classes, set `spring.jpa.hibernate.ddl-auto=update` in `application.properties` for development to automatically sync schema changes.

For production, use proper migration tools like Flyway.

## Code Generation

Run the `CodeGenerator` class to generate basic DTO and Service classes for entities.

```bash
mvn exec:java -Dexec.mainClass="com.erp.codegen.CodeGenerator"
```