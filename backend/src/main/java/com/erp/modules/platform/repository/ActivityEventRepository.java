package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.ActivityEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> {
  List<ActivityEvent> findByModuleAndRecordIdOrderByOccurredAtDesc(String module, String recordId);
  List<ActivityEvent> findByModuleOrderByOccurredAtDesc(String module);
  List<ActivityEvent> findAllByOrderByOccurredAtDesc();
}
