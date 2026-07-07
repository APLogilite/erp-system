package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormRoleFilterEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRoleFilterRepository extends JpaRepository<FormRoleFilterEntity, UUID> {

  List<FormRoleFilterEntity> findByFormIdAndRoleId(UUID formId, UUID roleId);

  List<FormRoleFilterEntity> findByFormId(UUID formId);
}
