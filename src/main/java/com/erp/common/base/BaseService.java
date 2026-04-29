package com.erp.common.base;

import jakarta.transaction.Transactional;
import java.util.Optional;

/**
 * Generic service placeholder for CRUD-style operations.
 * Intentionally does not implement business logic.
 */
public abstract class BaseService<E, D, ID> {

  @Transactional
  public D create(D dto) {
    throw new UnsupportedOperationException("TODO: implement create");
  }

  @Transactional
  public D update(ID id, D dto) {
    throw new UnsupportedOperationException("TODO: implement update");
  }

  @Transactional
  public Optional<D> findById(ID id) {
    throw new UnsupportedOperationException("TODO: implement findById");
  }

  @Transactional
  public void deleteById(ID id) {
    throw new UnsupportedOperationException("TODO: implement deleteById");
  }
}
