package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormSectionFieldEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormSectionFieldRepository extends JpaRepository<FormSectionFieldEntity, UUID> {

  List<FormSectionFieldEntity> findBySectionIdIn(List<UUID> sectionIds);

  List<FormSectionFieldEntity> findBySectionId(UUID sectionId);
}
