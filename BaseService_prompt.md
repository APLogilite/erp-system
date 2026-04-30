Using PROJECT_RULES.md,

Create a production-ready BaseService class in package: com.erp.common.base

Assume that all entities extend a common BaseEntity class with:
- UUID id field
- public UUID getId()
- Boolean isActive field (soft delete support)

Requirements:

1. Use Java with Spring Boot

2. Make it a generic abstract class:
   BaseService<T extends BaseEntity>

3. Use JpaRepository for persistence:
   - Define an abstract method:
     protected abstract JpaRepository<T, UUID> getRepository();

4. Implement CRUD methods:

   READ:
   - List<T> findAll()
   - Optional<T> findById(UUID id)
   - T findByIdOrThrow(UUID id)

   CREATE:
   - T create(T entity)

   UPDATE:
   - T update(T entity)

   DELETE (SOFT DELETE ONLY):
   - void delete(UUID id)
   - Do NOT physically delete records
   - Instead set entity.isActive = false and persist update

5. Transaction management:
   - Use @Transactional(readOnly = true) for read methods
   - Use @Transactional for create, update, delete

6. Validation rules:
   - For update and delete:
     - Fetch entity using findById
     - Throw IllegalArgumentException if not found
   - Only consider records where isActive = true in normal find operations
   - Do NOT use existsById (avoid extra DB calls)

7. Lifecycle hook methods (empty by default):

   CREATE:
   - protected void beforeCreate(T entity)
   - protected void afterCreate(T entity)

   UPDATE:
   - protected void beforeUpdate(T newEntity, T existingEntity)
   - protected void afterUpdate(T entity)

   DELETE:
   - protected void beforeDelete(T entity)
   - protected void afterDelete(T entity)

8. Hook execution flow:

   CREATE:
   beforeCreate → save → afterCreate

   UPDATE:
   fetch existing → beforeUpdate(new, existing) → save → afterUpdate

   DELETE:
   fetch existing → beforeDelete → set isActive=false → save → afterDelete

9. Coding best practices:
   - Use Optional properly
   - Do not expose repository outside
   - Keep class clean and reusable
   - Avoid redundant DB calls
   - Follow clean architecture
   - Ensure soft deleted records are not returned in normal findAll/findById

10. Exception handling:
   - Use IllegalArgumentException for "not found" cases
   - Do NOT use generic RuntimeException

11. Do NOT add any business logic

12. Add all necessary imports and annotations

Goal:
This BaseService must be reusable across all modules (Product, User, etc.), enforce a consistent UUID-based identity via BaseEntity, support soft delete using isActive flag, and provide clean lifecycle hooks for extensibility.