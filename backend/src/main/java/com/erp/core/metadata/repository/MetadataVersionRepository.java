package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataVersionRepository extends JpaRepository<MetadataVersion, UUID> {

  Optional<MetadataVersion> findFirstByIsActiveTrueOrderByVersionDesc();

  List<MetadataVersion> findByTableIdOrderByVersionAsc(UUID tableId);

  List<MetadataVersion> findByTableIdOrderByVersionDesc(UUID tableId);

  @Query("SELECT COALESCE(MAX(v.version), 0) FROM MetadataVersion v WHERE v.tableId = :tableId")
  Integer findMaxVersionByTableId(@Param("tableId") UUID tableId);
}
