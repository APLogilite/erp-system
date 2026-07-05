package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataVersion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataVersionRepository extends JpaRepository<MetadataVersion, UUID> {
  Optional<MetadataVersion> findFirstByIsActiveTrueOrderByVersionDesc();
}
