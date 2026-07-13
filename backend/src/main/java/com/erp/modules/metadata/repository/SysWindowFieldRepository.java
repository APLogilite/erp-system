package com.erp.modules.metadata.repository;

import com.erp.modules.metadata.entity.SysWindowField;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysWindowFieldRepository extends JpaRepository<SysWindowField, UUID> {
  List<SysWindowField> findByTabIdOrderBySeqNoAsc(UUID tabId);
}
