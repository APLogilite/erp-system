package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysTable;
import com.erp.core.layout.repository.SysTableRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysTableService extends BaseService<SysTable> {

  private final SysTableRepository repository;

  public SysTableService(SysTableRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysTable, UUID> getRepository() {
    return repository;
  }

  @Override
  protected void beforeCreate(SysTable entity) {
    if (entity.getName() == null || entity.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Table name is required");
    }
    if (entity.getLabel() == null || entity.getLabel().trim().isEmpty()) {
      throw new IllegalArgumentException("Table label is required");
    }
    if (entity.getTableName() == null || entity.getTableName().trim().isEmpty()) {
      throw new IllegalArgumentException("Physical table name is required");
    }
  }
}
