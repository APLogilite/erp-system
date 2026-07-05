package com.erp.modules.projects.repository;

import com.erp.modules.projects.entity.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
  Optional<Project> findByProjectCode(String projectCode);
  List<Project> findByCustomerId(UUID customerId);
  List<Project> findByStatus(String status);
}
