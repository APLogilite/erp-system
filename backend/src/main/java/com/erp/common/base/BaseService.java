package com.erp.common.base;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reusable generic base service for common CRUD operations.
 * Concrete services provide the repository implementation.
 */
public abstract class BaseService<T extends BaseEntity> {

  protected abstract JpaRepository<T, UUID> getRepository();

  @Transactional(readOnly = true)
  public List<T> findAll() {
    return getRepository().findAll().stream().filter(this::isActive).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Optional<T> findById(UUID id) {
    return getRepository().findById(id).filter(this::isActive);
  }

  @Transactional(readOnly = true)
  public T findByIdOrThrow(UUID id) {
    return findById(id).orElseThrow(() -> new IllegalArgumentException("Entity not found for id: " + id));
  }

  @Transactional
  public T create(T entity) {
    entity.setIsActive(true);
    beforeCreate(entity);
    T savedEntity = getRepository().save(entity);
    afterCreate(savedEntity);
    return savedEntity;
  }

  @Transactional
  public T update(T entity) {
    UUID id = entity.getId();
    T existingEntity =
        findById(id).orElseThrow(() -> new IllegalArgumentException("Entity not found for id: " + id));
    entity.setIsActive(existingEntity.getIsActive());
    beforeUpdate(entity, existingEntity);
    T updatedEntity = getRepository().save(entity);
    afterUpdate(updatedEntity);
    return updatedEntity;
  }

  @Transactional
  public void delete(UUID id) {
    T existingEntity =
        findById(id).orElseThrow(() -> new IllegalArgumentException("Entity not found for id: " + id));
    beforeDelete(existingEntity);
    existingEntity.softDelete();
    T deletedEntity = getRepository().save(existingEntity);
    afterDelete(deletedEntity);
  }

  protected void beforeCreate(T entity) {}

  protected void afterCreate(T entity) {}

  protected void beforeUpdate(T newEntity, T existingEntity) {}

  protected void afterUpdate(T entity) {}

  protected void beforeDelete(T entity) {}

  protected void afterDelete(T entity) {}

  private boolean isActive(T entity) {
    return Boolean.TRUE.equals(entity.getIsActive());
  }
}
