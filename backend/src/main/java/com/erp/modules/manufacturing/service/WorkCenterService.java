package com.erp.modules.manufacturing.service;

import com.erp.common.base.BaseService;
import com.erp.modules.manufacturing.entity.WorkCenter;
import com.erp.modules.manufacturing.repository.WorkCenterRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkCenterService extends BaseService<WorkCenter> {

  private final WorkCenterRepository workCenterRepository;

  public WorkCenterService(WorkCenterRepository workCenterRepository) {
    this.workCenterRepository = workCenterRepository;
  }

  @Override
  protected JpaRepository<WorkCenter, UUID> getRepository() {
    return workCenterRepository;
  }

  @Override
  protected void beforeCreate(WorkCenter entity) {
    if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Work center code is required");
    }
    if (workCenterRepository.findByCode(entity.getCode()).isPresent()) {
      throw new IllegalArgumentException("Work center code must be unique");
    }
    if (entity.getEfficiency() == null) {
      entity.setEfficiency(100.0);
    }
  }

  @Override
  protected void beforeUpdate(WorkCenter newEntity, WorkCenter existingEntity) {
    if (!newEntity.getCode().equals(existingEntity.getCode())
        && workCenterRepository.findByCode(newEntity.getCode()).isPresent()) {
      throw new IllegalArgumentException("Work center code must be unique");
    }
  }
}
