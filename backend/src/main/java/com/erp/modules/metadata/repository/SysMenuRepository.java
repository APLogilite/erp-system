package com.erp.modules.metadata.repository;

import com.erp.modules.metadata.entity.SysMenu;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, UUID> {
  List<SysMenu> findByParentIdIsNullOrderBySeqNoAsc();
  List<SysMenu> findByParentIdOrderBySeqNoAsc(UUID parentId);
  List<SysMenu> findByTypeOrderBySeqNoAsc(String type);
}
