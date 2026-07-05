package com.erp.modules.projects.repository;

import com.erp.modules.projects.entity.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  Optional<Task> findByTaskNumber(String taskNumber);
  List<Task> findByProjectId(UUID projectId);
  List<Task> findByAssignedTo(UUID assignedTo);
  List<Task> findByStatus(String status);
}
