References:
- docs/planning/project-rules.md
- docs/planning/project-structure-prompt.md

IMPORTANT CONTEXT (DO NOT IGNORE):
We already have a BaseEntity class in the system. All entities extend it.

BaseEntity contains the following structure:

4. UUID primary key:
   - private UUID id
   - @Id and @GeneratedValue(strategy = GenerationType.UUID)

5. Audit fields:
   - private LocalDateTime createdAt
   - private LocalDateTime updatedAt
   - private UUID createdBy
   - private UUID updatedBy

6. Soft delete support:
   - private Boolean isActive = true
   - private LocalDateTime deletedAt

7. JPA lifecycle hooks:
   - @PrePersist:
       - set createdAt = now
       - set updatedAt = now
       - set isActive = true
   - @PreUpdate:
       - set updatedAt = now

8. Helper methods:
   - void softDelete():
       → set isActive = false
       → set deletedAt = now
   - void restore():
       → set isActive = true
       → set deletedAt = null

All entities in the system MUST follow this structure.

------------------------------------------------------------

Now create a production-ready BaseService class in package: com.erp.common.base

Requirements:

1. Use Java with Spring Boot

2. Make it a generic abstract class:
   BaseService<T extends BaseEntity>

3. Use JpaRepository for persistence:
   - Define abstract method:
     protected abstract JpaRepository<T, UUID> getRepository();

4. Implement CRUD methods:

   READ:
   - List<T> findAll()
     → MUST return only entities where isActive = true
   - Optional<T> findById(UUID id)
     → MUST return only if isActive = true
   - T findByIdOrThrow(UUID id)
     → throw IllegalArgumentException if not found or inactive

   CREATE:
   - T create(T entity)

   UPDATE:
   - T update(T entity)

   DELETE (SOFT DELETE ONLY):
   - void delete(UUID id)
     → MUST call entity.softDelete()
     → MUST NOT physically delete from database

5. Transaction management:
   - @Transactional(readOnly = true) for read methods
   - @Transactional for create, update, delete

6. Validation rules:
   - For update/delete:
     - fetch entity using findById
     - throw IllegalArgumentException if not found
   - Do NOT use existsById

7. Lifecycle hook methods:

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
   fetch existing → beforeDelete → softDelete → save → afterDelete

9. Coding rules:
   - Use Optional properly
   - Do not expose repository outside
   - Avoid redundant DB calls
   - Follow clean architecture strictly
   - Always respect isActive = true in read operations

10. Exception handling:
   - Use IllegalArgumentException for not found or inactive entities
   - Do NOT use RuntimeException

11. Do NOT add business logic

12. Add all required imports and annotations

GOAL:
This BaseService must strictly align with the existing BaseEntity structure, enforce soft delete behavior, ensure UUID-based identity consistency, and provide reusable lifecycle hooks across all ERP modules without changing the architecture.