package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormSubFormEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormSubFormRepository extends JpaRepository<FormSubFormEntity, UUID> {

  List<FormSubFormEntity> findByParentFormIdOrderByPosition(UUID parentFormId);
}
