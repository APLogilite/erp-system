package com.erp.core.layout.repository;

import com.erp.core.layout.entity.SysColumn;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysColumnRepository extends JpaRepository<SysColumn, UUID> {
  List<SysColumn> findByTableId(UUID tableId);
  Optional<SysColumn> findByTableIdAndCode(UUID tableId, String code);
}
