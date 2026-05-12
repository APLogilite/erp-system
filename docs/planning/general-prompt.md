Using PROJECT_RULES.md,

References:
- docs/planning/project-rules.md

Create a Spring Boot project structure for an ERP system.

Requirements:
- Base package: com.erp
- Create modules: auth, users, product, inventory, order
- Each module must include:
  - controller/
  - service/
  - repository/
  - entity/
  - dto/

Also create:
- config/
- common/base/
- common/utils/

Do NOT implement business logic.
Only create folder structure and basic class placeholders.

Follow clean architecture and naming conventions.



Now create BaseService class in common/base with:
- Generic CRUD methods
- JpaRepository support
- Transaction management
- Hook methods