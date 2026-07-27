package com.erp.core.layout.repository;

import com.erp.core.layout.entity.SysTab;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysTabRepository extends JpaRepository<SysTab, UUID> {
  List<SysTab> findByWindowIdOrderBySeqNoAsc(UUID windowId);
  List<SysTab> findByWindowIdAndParentLinkColumn_IDIsNullOrderBySeqNoAsc(UUID windowId);
  List<SysTab> findByWindowIdAndParentLinkColumn_IDIsNotNullOrderBySeqNoAsc(UUID windowId);
}
