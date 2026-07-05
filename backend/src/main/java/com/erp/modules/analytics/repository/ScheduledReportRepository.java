package com.erp.modules.analytics.repository;

import com.erp.modules.analytics.entity.ScheduledReport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, UUID> {
  Optional<ScheduledReport> findByScheduleCode(String scheduleCode);
  List<ScheduledReport> findByStatus(String status);
  List<ScheduledReport> findByReportId(String reportId);
}
