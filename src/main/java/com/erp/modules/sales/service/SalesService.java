package com.erp.modules.sales.service;

import com.erp.common.base.BaseService;
import com.erp.modules.sales.entity.SalesEntity;
import com.erp.modules.sales.repository.SalesRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Sales service placeholder.
 * Business logic is intentionally not implemented.
 */
@Service
public class SalesService extends BaseService<SalesEntity> {

  private final SalesRepository salesRepository;

  public SalesService(SalesRepository salesRepository) {
    this.salesRepository = salesRepository;
  }

  @Override
  protected JpaRepository<SalesEntity, UUID> getRepository() {
    return salesRepository;
  }
}
