package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataModel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataModelRepository extends JpaRepository<MetadataModel, UUID> {
  Optional<MetadataModel> findByName(String name);

  Optional<MetadataModel> findByTableName(String tableName);

  Page<MetadataModel> findByNameContainingIgnoreCaseOrLabelContainingIgnoreCase(
      String name, String label, Pageable pageable);
}
