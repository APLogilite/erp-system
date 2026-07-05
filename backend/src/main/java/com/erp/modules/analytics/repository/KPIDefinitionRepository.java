package com.erp.modules.analytics.repository;

import com.erp.modules.analytics.entity.KPIDefinition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KPIDefinitionRepository extends JpaRepository<KPIDefinition, UUID> {
  Optional<KPIDefinition> findByKpiCode(String kpiCode);
  List<KPIDefinition> findByCategory(String category);
  List<KPIDefinition> findByIsActiveTrue();
}
