package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.PlatformEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformEventRepository extends JpaRepository<PlatformEvent, UUID> {
  List<PlatformEvent> findBySourceModuleOrderByOccurredAtDesc(String sourceModule);
  List<PlatformEvent> findByEventTypeOrderByOccurredAtDesc(String eventType);
  List<PlatformEvent> findAllByOrderByOccurredAtDesc();
}
