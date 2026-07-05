package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.WorkCenter;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkCenterRepository extends JpaRepository<WorkCenter, UUID> {
  Optional<WorkCenter> findByCode(String code);
}
