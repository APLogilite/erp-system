# ERP Project Rules

- Use Spring Boot (Java)
- Use Maven
- Use PostgreSQL
- Use UUID as primary key
- Use layered architecture:
  controller → service → repository
- Use DTO pattern (no entity exposure)
- Use BaseService for CRUD
- Use @Transactional in service layer
- Use REST APIs with /api/v1/
- Each module must be isolated

## Modules:
- auth
- users
- product
- inventory
- sales

## Structure:

com.erp
 ├── config/
 ├── common/
 │    ├── base/
 │    ├── utils/
 ├── modules/
      ├── auth/
      ├── users/
      ├── product/
      ├── inventory/
      ├── sales/