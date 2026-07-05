package com.erp.modules.analytics.repository;

import com.erp.modules.analytics.entity.DashboardWidget;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, UUID> {
  List<DashboardWidget> findByDashboardIdOrderByPositionY(UUID dashboardId);
  void deleteByDashboardId(UUID dashboardId);
}
