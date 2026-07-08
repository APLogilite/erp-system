package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.*;
import com.erp.core.metadata.entity.FormTenantRoleEntity;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.repository.FormTenantRoleRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormTenantRoleService {
  private final FormTenantRoleRepository tenantRoleRepository;
  private final MetadataViewRepository viewRepo;

  public FormTenantRoleService(
      FormTenantRoleRepository tenantRoleRepository,
      MetadataViewRepository viewRepo) {
    this.tenantRoleRepository = tenantRoleRepository;
    this.viewRepo = viewRepo;
  }

  public TenantRoleResponse getRoles(UUID formId, UUID tenantId) {
    List<FormTenantRoleEntity> entries = tenantRoleRepository.findByFormIdAndTenantId(formId, tenantId);
    return toResponse(formId, tenantId, entries);
  }

  @Transactional
  public TenantRoleResponse setRoles(UUID formId, UUID tenantId, TenantRoleRequest request) {
    // Replace-all: delete existing, insert new
    tenantRoleRepository.deleteByFormIdAndTenantId(formId, tenantId);

    if (request.getRoleIds() != null) {
      for (UUID roleId : request.getRoleIds()) {
        FormTenantRoleEntity e = new FormTenantRoleEntity();
        e.setFormId(formId);
        e.setTenantId(tenantId);
        e.setRoleId(roleId);
        tenantRoleRepository.save(e);
      }
    }

    List<FormTenantRoleEntity> updated = tenantRoleRepository.findByFormIdAndTenantId(formId, tenantId);
    return toResponse(formId, tenantId, updated);
  }

  /**
   * System Admin view: get all tenant role assignments for a form.
   */
  public List<TenantRoleResponse> getGlobalTenantRoles(UUID formId) {
    List<FormTenantRoleEntity> all = tenantRoleRepository.findByFormId(formId);
    Map<UUID, List<FormTenantRoleEntity>> byTenant = all.stream()
        .collect(Collectors.groupingBy(FormTenantRoleEntity::getTenantId));

    return byTenant.entrySet().stream()
        .map(e -> toResponse(formId, e.getKey(), e.getValue()))
        .toList();
  }

  /**
   * List all global forms available to the current tenant, indicating
   * whether the tenant has already configured role access.
   */
  public List<GlobalFormDto> getGlobalForms(UUID currentTenantId) {
    List<MetadataView> globalViews = viewRepo.findByScopeAndTypeAndIsActiveTrue("global", "form");
    List<FormTenantRoleEntity> tenantAssignments = tenantRoleRepository.findByTenantId(currentTenantId);
    Set<UUID> configuredFormIds = tenantAssignments.stream()
        .map(FormTenantRoleEntity::getFormId)
        .collect(Collectors.toSet());

    return globalViews.stream().map(v -> {
      GlobalFormDto d = new GlobalFormDto();
      d.setFormId(v.getId());
      d.setFormCode(v.getName());
      d.setFormLabel(v.getName());
      d.setModelName(v.getModelName());
      d.setHasConfiguredAccess(configuredFormIds.contains(v.getId()));
      return d;
    }).toList();
  }

  /**
   * Remove a single role assignment.
   */
  @Transactional
  public void removeRole(UUID formId, UUID tenantId, UUID roleId) {
    tenantRoleRepository.deleteByFormIdAndTenantIdAndRoleId(formId, tenantId, roleId);
  }

  /**
   * Extract current tenant ID from RuntimeContext.
   */
  public UUID getCurrentTenantId() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    return ctx != null ? ctx.getTenantId() : null;
  }

  private TenantRoleResponse toResponse(UUID formId, UUID tenantId, List<FormTenantRoleEntity> entries) {
    TenantRoleResponse r = new TenantRoleResponse();
    r.setFormId(formId);
    r.setTenantId(tenantId);
    r.setRoleIds(entries.stream().map(FormTenantRoleEntity::getRoleId).toList());
    return r;
  }

  // Legacy compatibility methods
  @Transactional
  public FormTenantRoleDto assignRole(FormTenantRoleCreateRequest req) {
    FormTenantRoleEntity e = new FormTenantRoleEntity();
    e.setFormId(req.getFormId());
    e.setTenantId(req.getTenantId());
    e.setRoleId(req.getRoleId());
    return toDto(tenantRoleRepository.save(e));
  }

  public List<FormTenantRoleDto> getFormsByRole(UUID tenantId, UUID roleId) {
    return tenantRoleRepository.findByTenantIdAndRoleId(tenantId, roleId).stream()
        .map(this::toDto).toList();
  }

  private FormTenantRoleDto toDto(FormTenantRoleEntity e) {
    FormTenantRoleDto d = new FormTenantRoleDto();
    d.setId(e.getId()); d.setFormId(e.getFormId());
    d.setTenantId(e.getTenantId()); d.setRoleId(e.getRoleId());
    return d;
  }
}
