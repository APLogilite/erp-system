package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataWorkflow;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataWorkflowRepository extends JpaRepository<MetadataWorkflow, UUID> {
  Optional<MetadataWorkflow> findByName(String name);
  Optional<MetadataWorkflow> findByModelName(String modelName);
}
