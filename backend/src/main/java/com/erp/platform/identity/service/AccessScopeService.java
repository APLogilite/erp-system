package com.erp.platform.identity.service;

import com.erp.platform.identity.entity.Branch;
import com.erp.platform.identity.entity.Company;
import com.erp.platform.identity.entity.Organization;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.RoleBranch;
import com.erp.platform.identity.entity.RoleCompany;
import com.erp.platform.identity.entity.RoleOrganization;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.BranchRepository;
import com.erp.platform.identity.repository.CompanyRepository;
import com.erp.platform.identity.repository.OrganizationRepository;
import com.erp.platform.identity.repository.RoleRepository;
import com.erp.platform.identity.repository.RoleBranchRepository;
import com.erp.platform.identity.repository.RoleCompanyRepository;
import com.erp.platform.identity.repository.RoleOrganizationRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AccessScopeService {

  private final UserRoleRepository userRoleRepository;
  private final RoleOrganizationRepository roleOrganizationRepository;
  private final RoleCompanyRepository roleCompanyRepository;
  private final RoleBranchRepository roleBranchRepository;
  private final OrganizationRepository organizationRepository;
  private final CompanyRepository companyRepository;
  private final BranchRepository branchRepository;
  private final RoleRepository roleRepository;

  public AccessScopeService(UserRoleRepository userRoleRepository,
                            RoleOrganizationRepository roleOrganizationRepository,
                            RoleCompanyRepository roleCompanyRepository,
                            RoleBranchRepository roleBranchRepository,
                            OrganizationRepository organizationRepository,
                            CompanyRepository companyRepository,
                            BranchRepository branchRepository,
                            RoleRepository roleRepository) {
    this.userRoleRepository = userRoleRepository;
    this.roleOrganizationRepository = roleOrganizationRepository;
    this.roleCompanyRepository = roleCompanyRepository;
    this.roleBranchRepository = roleBranchRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.roleRepository = roleRepository;
  }

  public static class RoleScope {
    private boolean fullAccess;
    private UUID tenantId;
    private List<UUID> organizationIds;
    private List<UUID> companyIds;
    private List<UUID> branchIds;

    public RoleScope() {
      this.organizationIds = new ArrayList<>();
      this.companyIds = new ArrayList<>();
      this.branchIds = new ArrayList<>();
    }

    public boolean isFullAccess() { return fullAccess; }
    public void setFullAccess(boolean fullAccess) { this.fullAccess = fullAccess; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public List<UUID> getOrganizationIds() { return organizationIds; }
    public void setOrganizationIds(List<UUID> organizationIds) { this.organizationIds = organizationIds; }
    public List<UUID> getCompanyIds() { return companyIds; }
    public void setCompanyIds(List<UUID> companyIds) { this.companyIds = companyIds; }
    public List<UUID> getBranchIds() { return branchIds; }
    public void setBranchIds(List<UUID> branchIds) { this.branchIds = branchIds; }
  }

  // ─── Public API ───

  public List<UUID> getAccessibleOrganizationIds(UUID userId) {
    Set<UUID> ids = new HashSet<>();
    for (UserRole ur : userRoleRepository.findByUserId(userId)) {
      ids.addAll(getAccessibleOrgIdsForRole(ur.getRole()));
    }
    return new ArrayList<>(ids);
  }

  public List<UUID> getAccessibleCompanyIds(UUID userId) {
    Set<UUID> ids = new HashSet<>();
    for (UserRole ur : userRoleRepository.findByUserId(userId)) {
      ids.addAll(getAccessibleCoIdsForRole(ur.getRole()));
    }
    return new ArrayList<>(ids);
  }

  public List<UUID> getAccessibleBranchIds(UUID userId) {
    Set<UUID> ids = new HashSet<>();
    for (UserRole ur : userRoleRepository.findByUserId(userId)) {
      ids.addAll(getAccessibleBrIdsForRole(ur.getRole()));
    }
    return new ArrayList<>(ids);
  }

  public Map<String, RoleScope> getRoleScopes(UUID userId) {
    Map<String, RoleScope> scopes = new HashMap<>();
    for (UserRole ur : userRoleRepository.findByUserId(userId)) {
      Role role = ur.getRole();
      scopes.put(role.getCode(), getAccessForRole(role));
    }
    return scopes;
  }

  public RoleScope getAccessForRole(UUID roleId) {
    Role role = roleRepository.findById(roleId).orElse(null);
    if (role == null) return new RoleScope();
    return getAccessForRole(role);
  }

  // ─── Internal Helpers ───

  private RoleScope getAccessForRole(Role role) {
    RoleScope scope = new RoleScope();
    scope.setTenantId(role.getTenant() != null ? role.getTenant().getId() : null);

    List<RoleOrganization> roleOrgs = roleOrganizationRepository.findByRoleId(role.getId());
    scope.setFullAccess(roleOrgs.isEmpty());

    scope.setOrganizationIds(getAccessibleOrgIdsForRole(role));
    scope.setCompanyIds(getAccessibleCoIdsForRole(role));
    scope.setBranchIds(getAccessibleBrIdsForRole(role));

    return scope;
  }

  private List<UUID> getAccessibleOrgIdsForRole(Role role) {
    List<RoleOrganization> entries = roleOrganizationRepository.findByRoleId(role.getId());
    if (entries.isEmpty()) {
      if (role.getTenant() == null) return Collections.emptyList();
      return organizationRepository.findByTenantId(role.getTenant().getId()).stream()
          .map(Organization::getId)
          .collect(Collectors.toList());
    }
    return entries.stream()
        .map(e -> e.getOrganization().getId())
        .collect(Collectors.toList());
  }

  private List<UUID> getAccessibleCoIdsForRole(Role role) {
    List<RoleCompany> entries = roleCompanyRepository.findByRoleId(role.getId());
    if (entries.isEmpty()) {
      List<UUID> orgIds = getAccessibleOrgIdsForRole(role);
      if (orgIds.isEmpty()) return Collections.emptyList();
      return companyRepository.findByOrganizationIdIn(orgIds).stream()
          .map(Company::getId)
          .collect(Collectors.toList());
    }
    return entries.stream()
        .map(e -> e.getCompany().getId())
        .collect(Collectors.toList());
  }

  private List<UUID> getAccessibleBrIdsForRole(Role role) {
    List<RoleBranch> entries = roleBranchRepository.findByRoleId(role.getId());
    if (entries.isEmpty()) {
      List<UUID> coIds = getAccessibleCoIdsForRole(role);
      if (coIds.isEmpty()) return Collections.emptyList();
      return branchRepository.findByCompanyIdIn(coIds).stream()
          .map(Branch::getId)
          .collect(Collectors.toList());
    }
    return entries.stream()
        .map(e -> e.getBranch().getId())
        .collect(Collectors.toList());
  }
}
