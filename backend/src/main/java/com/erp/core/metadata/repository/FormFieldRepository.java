package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormFieldEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormFieldRepository extends JpaRepository<FormFieldEntity, UUID> {

  List<FormFieldEntity> findByFormIdAndIsActiveTrueOrderByPosition(UUID formId);

  List<FormFieldEntity> findByFormIdOrderByPosition(UUID formId);
}
