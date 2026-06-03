package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataViewRepository extends JpaRepository<MetadataView, UUID> {
  Optional<MetadataView> findByName(String name);
  List<MetadataView> findByModelName(String modelName);
}
