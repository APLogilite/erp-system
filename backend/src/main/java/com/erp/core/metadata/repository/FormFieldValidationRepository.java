package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormFieldValidationEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormFieldValidationRepository extends JpaRepository<FormFieldValidationEntity, UUID> {

  List<FormFieldValidationEntity> findByFieldIdIn(List<UUID> fieldIds);

  List<FormFieldValidationEntity> findByFieldId(UUID fieldId);
}
