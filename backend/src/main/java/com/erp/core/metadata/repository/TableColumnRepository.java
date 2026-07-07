package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.TableColumnEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TableColumnRepository extends JpaRepository<TableColumnEntity, UUID> {

  @Query("SELECT t FROM TableColumnEntity t WHERE t.tableId = :tableId AND t.isActive = true ORDER BY t.position")
  List<TableColumnEntity> findByTableIdAndIsActiveTrueOrderByPosition(@Param("tableId") UUID tableId);

  List<TableColumnEntity> findByTableIdOrderByPosition(UUID tableId);
}
