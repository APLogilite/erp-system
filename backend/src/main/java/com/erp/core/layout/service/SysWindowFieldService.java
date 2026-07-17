package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysWindowField;
import com.erp.core.layout.repository.SysWindowFieldRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SysWindowFieldService extends BaseService<SysWindowField> {

  private final SysWindowFieldRepository repository;

  public SysWindowFieldService(SysWindowFieldRepository repository) {
    this.repository = repository;
  }

  @Override
  protected JpaRepository<SysWindowField, UUID> getRepository() {
    return repository;
  }

  public List<SysWindowField> findByTabIdOrderBySeqNoAsc(UUID tabId) {
    return repository.findByTabIdOrderBySeqNoAsc(tabId);
  }
}
