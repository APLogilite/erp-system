package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormLayoutSectionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormLayoutSectionRepository extends JpaRepository<FormLayoutSectionEntity, UUID> {

  List<FormLayoutSectionEntity> findByFormIdOrderByPosition(UUID formId);
}
