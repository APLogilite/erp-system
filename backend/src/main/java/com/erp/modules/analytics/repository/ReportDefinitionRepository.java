package com.erp.modules.analytics.repository;

import com.erp.modules.analytics.entity.ReportDefinition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, UUID> {
  Optional<ReportDefinition> findByReportCode(String reportCode);
  List<ReportDefinition> findByReportType(String reportType);
  List<ReportDefinition> findByModelCode(String modelCode);
}
