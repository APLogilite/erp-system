package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormTenantRoleCreateRequest;
import com.erp.core.metadata.dto.FormTenantRoleDto;
import com.erp.core.metadata.entity.FormTenantRoleEntity;
import com.erp.core.metadata.repository.FormTenantRoleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormTenantRoleService {
  private final FormTenantRoleRepository tenantRoleRepository;
  public FormTenantRoleService(FormTenantRoleRepository tenantRoleRepository) { this.tenantRoleRepository = tenantRoleRepository; }

  public List<FormTenantRoleDto> getRoles(UUID formId, UUID tenantId) {
    return tenantRoleRepository.findByFormIdAndTenantId(formId, tenantId).stream().map(this::toDto).toList();
  }

  @Transactional
  public FormTenantRoleDto assignRole(FormTenantRoleCreateRequest req) {
    FormTenantRoleEntity e = new FormTenantRoleEntity();
    e.setFormId(req.getFormId()); e.setTenantId(req.getTenantId()); e.setRoleId(req.getRoleId());
    return toDto(tenantRoleRepository.save(e));
  }

  @Transactional
  public void removeRole(UUID formId, UUID tenantId, UUID roleId) {
    tenantRoleRepository.deleteByFormIdAndTenantId(formId, tenantId);
  }

  public List<FormTenantRoleDto> getFormsByRole(UUID tenantId, UUID roleId) {
    return tenantRoleRepository.findByTenantIdAndRoleId(tenantId, roleId).stream().map(this::toDto).toList();
  }

  private FormTenantRoleDto toDto(FormTenantRoleEntity e) {
    FormTenantRoleDto d = new FormTenantRoleDto();
    d.setId(e.getId()); d.setFormId(e.getFormId()); d.setTenantId(e.getTenantId()); d.setRoleId(e.getRoleId());
    return d;
  }
}
