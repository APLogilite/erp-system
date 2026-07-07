package com.erp.platform.identity.service;

import com.erp.platform.identity.dto.ContextOption;
import com.erp.platform.identity.dto.ContextOptionsResponse;
import com.erp.platform.identity.dto.ContextSwitchRequest;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Branch;
import com.erp.platform.identity.entity.Company;
import com.erp.platform.identity.entity.Department;
import com.erp.platform.identity.entity.Organization;
import com.erp.platform.identity.entity.Tenant;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserPreference;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.BranchRepository;
import com.erp.platform.identity.repository.CompanyRepository;
import com.erp.platform.identity.repository.DepartmentRepository;
import com.erp.platform.identity.repository.OrganizationRepository;
import com.erp.platform.identity.repository.TenantRepository;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.repository.UserPreferenceRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import com.erp.platform.identity.service.AccessScopeService.RoleScope;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RuntimeContextService {

  private final UserAccountRepository userRepository;
  private final TenantRepository tenantRepository;
  private final OrganizationRepository organizationRepository;
  private final CompanyRepository companyRepository;
  private final BranchRepository branchRepository;
  private final DepartmentRepository departmentRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserPreferenceRepository userPreferenceRepository;
  private final AccessScopeService accessScopeService;

  public RuntimeContextService(UserAccountRepository userRepository,
                                TenantRepository tenantRepository,
                                OrganizationRepository organizationRepository,
                                CompanyRepository companyRepository,
                                BranchRepository branchRepository,
                                DepartmentRepository departmentRepository,
                                UserRoleRepository userRoleRepository,
                                UserPreferenceRepository userPreferenceRepository,
                                AccessScopeService accessScopeService) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.departmentRepository = departmentRepository;
    this.userRoleRepository = userRoleRepository;
    this.userPreferenceRepository = userPreferenceRepository;
    this.accessScopeService = accessScopeService;
  }

  @Transactional(readOnly = true)
  public RuntimeContext resolve(UUID userId) {
    return resolve(userId, null);
  }

  @Transactional(readOnly = true)
  public RuntimeContext resolve(UUID userId, UUID sessionId) {
    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    RuntimeContext ctx = new RuntimeContext();
    ctx.setUserId(user.getId());
    ctx.setUsername(user.getUsername());
    ctx.setEmail(user.getEmail());
    ctx.setDisplayName(buildDisplayName(user));

    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    List<String> roleCodes = userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());
    ctx.setRoles(roleCodes);

    // Baseline: first role → first org → first company → first branch
    List<UUID> orgIds = accessScopeService.getAccessibleOrganizationIds(userId);
    if (!orgIds.isEmpty()) {
      organizationRepository.findById(orgIds.get(0)).ifPresent(org -> {
        ctx.setOrganizationId(org.getId());
        ctx.setOrganizationCode(org.getCode());
        ctx.setOrganizationName(org.getName());
        if (org.getTenant() != null) {
          ctx.setTenantId(org.getTenant().getId());
          ctx.setTenantCode(org.getTenant().getCode());
          ctx.setTenantName(org.getTenant().getName());
        }
      });
    }

    List<UUID> coIds = accessScopeService.getAccessibleCompanyIds(userId);
    if (!coIds.isEmpty()) {
      companyRepository.findById(coIds.get(0)).ifPresent(co -> {
        ctx.setCompanyId(co.getId());
        ctx.setCompanyCode(co.getCode());
        ctx.setCompanyName(co.getName());
      });
    }

    List<UUID> branchIds = accessScopeService.getAccessibleBranchIds(userId);
    if (!branchIds.isEmpty()) {
      branchRepository.findById(branchIds.get(0)).ifPresent(b -> {
        ctx.setBranchId(b.getId());
        ctx.setBranchCode(b.getCode());
        ctx.setBranchName(b.getName());
      });
    }

    // Apply persisted context overrides from UserPreference
    UserPreference prefs = userPreferenceRepository.findByUserId(userId).orElse(null);
    if (prefs != null) {
      applyPreferenceOverrides(ctx, prefs);
      ctx.setLanguage(prefs.getLanguage());
      ctx.setTimezone(prefs.getTimezone());
      ctx.setCurrency(prefs.getCurrency());
      ctx.setDateFormat(prefs.getDateFormat());
      ctx.setNumberFormat(prefs.getNumberFormat());
      ctx.setTheme(prefs.getTheme());
    }

    return ctx;
  }

  private void applyPreferenceOverrides(RuntimeContext ctx, UserPreference prefs) {
    UUID id;
    id = prefs.getActiveTenantId();
    if (id != null) {
      tenantRepository.findById(id).ifPresent(t -> {
        ctx.setTenantId(t.getId());
        ctx.setTenantCode(t.getCode());
        ctx.setTenantName(t.getName());
      });
    }
    id = prefs.getActiveOrganizationId();
    if (id != null) {
      organizationRepository.findById(id).ifPresent(o -> {
        ctx.setOrganizationId(o.getId());
        ctx.setOrganizationCode(o.getCode());
        ctx.setOrganizationName(o.getName());
      });
    }
    id = prefs.getActiveCompanyId();
    if (id != null) {
      companyRepository.findById(id).ifPresent(c -> {
        ctx.setCompanyId(c.getId());
        ctx.setCompanyCode(c.getCode());
        ctx.setCompanyName(c.getName());
      });
    }
    id = prefs.getActiveBranchId();
    if (id != null) {
      branchRepository.findById(id).ifPresent(b -> {
        ctx.setBranchId(b.getId());
        ctx.setBranchCode(b.getCode());
        ctx.setBranchName(b.getName());
      });
    }
    id = prefs.getActiveDepartmentId();
    if (id != null) {
      departmentRepository.findById(id).ifPresent(d -> {
        ctx.setDepartmentId(d.getId());
        ctx.setDepartmentCode(d.getCode());
        ctx.setDepartmentName(d.getName());
      });
    }
    if (prefs.getActiveRoleCode() != null) {
      ctx.setRoles(Collections.singletonList(prefs.getActiveRoleCode()));
    }
  }

  private String buildDisplayName(UserAccount user) {
    if (user.getFirstName() != null && user.getLastName() != null) {
      return user.getFirstName() + " " + user.getLastName();
    }
    return user.getUsername();
  }

  @Transactional(readOnly = true)
  public ContextOptionsResponse getAvailableOptions(UUID userId) {
    ContextOptionsResponse options = new ContextOptionsResponse();

    // Roles
    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    List<String> roleCodes = userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());
    options.setRoles(roleCodes);

    // Access scopes
    List<UUID> orgIds = accessScopeService.getAccessibleOrganizationIds(userId);
    List<UUID> coIds = accessScopeService.getAccessibleCompanyIds(userId);
    List<UUID> branchIds = accessScopeService.getAccessibleBranchIds(userId);

    // Tenants (derived from accessible orgs)
    List<Organization> accessibleOrgs = organizationRepository.findAllById(orgIds);
    List<UUID> tenantIds = accessibleOrgs.stream()
        .map(o -> o.getTenant().getId())
        .distinct()
        .collect(Collectors.toList());
    List<Tenant> tenants = tenantRepository.findAllById(tenantIds);
    options.setTenants(tenants.stream()
        .map(t -> new ContextOption(t.getId(), "tenant", t.getCode(), t.getName()))
        .collect(Collectors.toList()));

    // Organizations
    options.setOrganizations(accessibleOrgs.stream()
        .map(o -> new ContextOption(o.getId(), "organization",
            o.getCode(), o.getName(), o.getTenant().getId()))
        .collect(Collectors.toList()));

    // Companies
    List<Company> accessibleCompanies = companyRepository.findAllById(coIds);
    options.setCompanies(accessibleCompanies.stream()
        .map(c -> new ContextOption(c.getId(), "company",
            c.getCode(), c.getName(), c.getOrganization().getId()))
        .collect(Collectors.toList()));

    // Branches
    List<Branch> accessibleBranches = branchRepository.findAllById(branchIds);
    options.setBranches(accessibleBranches.stream()
        .map(b -> new ContextOption(b.getId(), "branch",
            b.getCode(), b.getName(), b.getCompany().getId()))
        .collect(Collectors.toList()));

    // Departments
    if (!branchIds.isEmpty()) {
      List<Department> departments = departmentRepository.findByBranchIdIn(branchIds);
      options.setDepartments(departments.stream()
          .map(d -> new ContextOption(d.getId(), "department",
              d.getCode(), d.getName(), d.getBranch().getId()))
          .collect(Collectors.toList()));
    }

    // Role scopes
    Map<String, RoleScope> roleScopes = accessScopeService.getRoleScopes(userId);
    options.setRoleScopes(roleScopes);

    return options;
  }

  @Transactional
  public RuntimeContext switchContext(UUID userId, ContextSwitchRequest request) {
    RuntimeContext ctx = resolve(userId);

    if (request.getTenantId() != null) {
      Tenant t = tenantRepository.findById(request.getTenantId())
          .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
      ctx.setTenantId(t.getId());
      ctx.setTenantCode(t.getCode());
      ctx.setTenantName(t.getName());
    }

    if (request.getOrganizationId() != null) {
      Organization o = organizationRepository.findById(request.getOrganizationId())
          .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
      List<UUID> accessibleOrgIds = accessScopeService.getAccessibleOrganizationIds(userId);
      if (!accessibleOrgIds.contains(request.getOrganizationId())) {
        throw new SecurityException("Organization not in role scope");
      }
      ctx.setOrganizationId(o.getId());
      ctx.setOrganizationCode(o.getCode());
      ctx.setOrganizationName(o.getName());
    }

    if (request.getCompanyId() != null) {
      Company c = companyRepository.findById(request.getCompanyId())
          .orElseThrow(() -> new IllegalArgumentException("Company not found"));
      List<UUID> accessibleCoIds = accessScopeService.getAccessibleCompanyIds(userId);
      if (!accessibleCoIds.contains(request.getCompanyId())) {
        throw new SecurityException("Company not in role scope");
      }
      ctx.setCompanyId(c.getId());
      ctx.setCompanyCode(c.getCode());
      ctx.setCompanyName(c.getName());
    }

    if (request.getBranchId() != null) {
      Branch b = branchRepository.findById(request.getBranchId())
          .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
      List<UUID> accessibleBranchIds = accessScopeService.getAccessibleBranchIds(userId);
      if (!accessibleBranchIds.contains(request.getBranchId())) {
        throw new SecurityException("Branch not in role scope");
      }
      ctx.setBranchId(b.getId());
      ctx.setBranchCode(b.getCode());
      ctx.setBranchName(b.getName());
    }

    if (request.getDepartmentId() != null) {
      Department d = departmentRepository.findById(request.getDepartmentId())
          .orElseThrow(() -> new IllegalArgumentException("Department not found"));
      ctx.setDepartmentId(d.getId());
      ctx.setDepartmentCode(d.getCode());
      ctx.setDepartmentName(d.getName());
    }

    if (request.getRoleCode() != null) {
      List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
      boolean hasRole = userRoles.stream()
          .anyMatch(ur -> ur.getRole().getCode().equals(request.getRoleCode()));
      if (!hasRole) {
        throw new SecurityException("User does not have this role");
      }
      ctx.setRoles(Collections.singletonList(request.getRoleCode()));
    }

    // Persist context selection to UserPreference
    userPreferenceRepository.findByUserId(userId).ifPresent(prefs -> {
      prefs.setActiveTenantId(request.getTenantId());
      prefs.setActiveOrganizationId(request.getOrganizationId());
      prefs.setActiveCompanyId(request.getCompanyId());
      prefs.setActiveBranchId(request.getBranchId());
      prefs.setActiveDepartmentId(request.getDepartmentId());
      prefs.setActiveRoleCode(request.getRoleCode());
      userPreferenceRepository.save(prefs);
    });

    RuntimeContextHolder.set(ctx);
    return ctx;
  }

  public RuntimeContext getCurrentContext() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null) {
      throw new IllegalStateException("No RuntimeContext available. Request is not authenticated.");
    }
    return ctx;
  }
}
