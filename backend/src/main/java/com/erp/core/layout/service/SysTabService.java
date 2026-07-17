package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysTab;
import com.erp.core.layout.repository.SysTabRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysTabService extends BaseService<SysTab> {

  private final SysTabRepository repository;

  public SysTabService(SysTabRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysTab, UUID> getRepository() {
    return repository;
  }

  public List<SysTab> findByWindowIdOrderBySeqNoAsc(UUID windowId) {
    return repository.findByWindowIdOrderBySeqNoAsc(windowId);
  }

  public List<SysTab> findMainTabsByWindowId(UUID windowId) {
    return repository.findByWindowIdAndParentColumnIsNullOrderBySeqNoAsc(windowId);
  }

  public List<SysTab> findChildTabsByWindowId(UUID windowId) {
    return repository.findByWindowIdAndParentColumnIsNotNullOrderBySeqNoAsc(windowId);
  }
}
