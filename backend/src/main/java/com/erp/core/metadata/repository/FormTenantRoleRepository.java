package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.FormTenantRoleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FormTenantRoleRepository extends JpaRepository<FormTenantRoleEntity, UUID> {

  List<FormTenantRoleEntity> findByFormIdAndTenantId(UUID formId, UUID tenantId);

  List<FormTenantRoleEntity> findByFormId(UUID formId);

  List<FormTenantRoleEntity> findByTenantId(UUID tenantId);

  List<FormTenantRoleEntity> findByTenantIdAndRoleId(UUID tenantId, UUID roleId);

  @Transactional
  void deleteByFormIdAndTenantId(UUID formId, UUID tenantId);

  @Transactional
  void deleteByFormIdAndTenantIdAndRoleId(UUID formId, UUID tenantId, UUID roleId);
}
