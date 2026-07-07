package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormFieldRuleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormFieldRuleRepository extends JpaRepository<FormFieldRuleEntity, UUID> {

  List<FormFieldRuleEntity> findByFieldIdIn(List<UUID> fieldIds);

  List<FormFieldRuleEntity> findByFieldId(UUID fieldId);
}
