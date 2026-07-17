package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysColumn;
import com.erp.core.layout.repository.SysColumnRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysColumnService extends BaseService<SysColumn> {

  private final SysColumnRepository repository;

  public SysColumnService(SysColumnRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysColumn, UUID> getRepository() {
    return repository;
  }

  public List<SysColumn> findByTableId(UUID tableId) {
    return repository.findByTableId(tableId);
  }

  @Override
  protected void beforeCreate(SysColumn entity) {
    if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Column code is required");
    }
    if (entity.getLabel() == null || entity.getLabel().trim().isEmpty()) {
      throw new IllegalArgumentException("Column label is required");
    }
    if (entity.getType() == null || entity.getType().trim().isEmpty()) {
      throw new IllegalArgumentException("Column type is required");
    }
  }
}
