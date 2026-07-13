package com.erp.modules.metadata.service;

import com.erp.common.base.BaseService;
import com.erp.modules.metadata.entity.SysWindow;
import com.erp.modules.metadata.repository.SysWindowRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysWindowService extends BaseService<SysWindow> {

  private final SysWindowRepository repository;

  public SysWindowService(SysWindowRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysWindow, UUID> getRepository() {
    return repository;
  }

  public Optional<SysWindow> findByName(String name) {
    return repository.findByName(name);
  }

  @Override
  protected void beforeCreate(SysWindow entity) {
    if (entity.getName() == null || entity.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Window name is required");
    }
  }
}
