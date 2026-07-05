package com.erp.modules.analytics.repository;

import com.erp.modules.analytics.entity.Dashboard;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
  Optional<Dashboard> findByDashboardCode(String dashboardCode);
  List<Dashboard> findByIsDefaultTrue();
}
