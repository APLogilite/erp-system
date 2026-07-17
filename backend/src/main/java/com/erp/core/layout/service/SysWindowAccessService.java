package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysWindowAccess;
import com.erp.core.layout.repository.SysWindowAccessRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysWindowAccessService extends BaseService<SysWindowAccess> {

  private final SysWindowAccessRepository repository;

  public SysWindowAccessService(SysWindowAccessRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysWindowAccess, UUID> getRepository() {
    return repository;
  }

  public List<SysWindowAccess> findByWindowId(UUID windowId) {
    return repository.findByWindowId(windowId);
  }

  public List<SysWindowAccess> findByRoleId(UUID roleId) {
    return repository.findByRoleId(roleId);
  }
}
