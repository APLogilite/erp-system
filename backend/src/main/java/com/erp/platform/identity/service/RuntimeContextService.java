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
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.Tenant;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserBranch;
import com.erp.platform.identity.entity.UserCompany;
import com.erp.platform.identity.entity.UserOrganization;
import com.erp.platform.identity.entity.UserPreference;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.BranchRepository;
import com.erp.platform.identity.repository.CompanyRepository;
import com.erp.platform.identity.repository.DepartmentRepository;
import com.erp.platform.identity.repository.OrganizationRepository;
import com.erp.platform.identity.repository.TenantRepository;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.repository.UserCompanyRepository;
import com.erp.platform.identity.repository.UserBranchRepository;
import com.erp.platform.identity.repository.UserOrganizationRepository;
import com.erp.platform.identity.repository.UserPreferenceRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import java.util.Collections;
import java.util.List;
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
  private final UserOrganizationRepository userOrganizationRepository;
  private final UserCompanyRepository userCompanyRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserPreferenceRepository userPreferenceRepository;
  private final UserBranchRepository userBranchRepository;

  public RuntimeContextService(UserAccountRepository userRepository,
                                TenantRepository tenantRepository,
                                OrganizationRepository organizationRepository,
                                CompanyRepository companyRepository,
                                BranchRepository branchRepository,
                                DepartmentRepository departmentRepository,
                                UserBranchRepository userBranchRepository,
                                UserOrganizationRepository userOrganizationRepository,
                                UserCompanyRepository userCompanyRepository,
                                UserRoleRepository userRoleRepository,
                                UserPreferenceRepository userPreferenceRepository) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.departmentRepository = departmentRepository;
    this.userBranchRepository = userBranchRepository;
    this.userOrganizationRepository = userOrganizationRepository;
    this.userCompanyRepository = userCompanyRepository;
    this.userRoleRepository = userRoleRepository;
    this.userPreferenceRepository = userPreferenceRepository;
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

    boolean isSysAdmin = roleCodes.contains("sys_admin");

    List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(userId);
    if (!userOrgs.isEmpty()) {
      Organization firstOrg = userOrgs.get(0).getOrganization();
      Tenant orgTenant = firstOrg.getTenant();
      ctx.setTenantId(orgTenant.getId());
      ctx.setTenantCode(orgTenant.getCode());
      ctx.setTenantName(orgTenant.getName());
      ctx.setOrganizationId(firstOrg.getId());
      ctx.setOrganizationCode(firstOrg.getCode());
      ctx.setOrganizationName(firstOrg.getName());

      List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
      if (!userCompanies.isEmpty()) {
        Company firstCompany = userCompanies.get(0).getCompany();
        ctx.setCompanyId(firstCompany.getId());
        ctx.setCompanyCode(firstCompany.getCode());
        ctx.setCompanyName(firstCompany.getName());
      }
    }

    if (isSysAdmin) {
      ctx.setTenantId(null);
      ctx.setTenantCode(null);
      ctx.setTenantName(null);
      ctx.setOrganizationId(null);
      ctx.setOrganizationCode(null);
      ctx.setOrganizationName(null);
      ctx.setCompanyId(null);
      ctx.setCompanyCode(null);
      ctx.setCompanyName(null);
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

    List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(userId);
    List<UUID> userTenantIds = userOrgs.stream()
        .map(uo -> uo.getOrganization().getTenant().getId())
        .distinct()
        .collect(Collectors.toList());
    List<Tenant> tenants = tenantRepository.findAllById(userTenantIds);
    options.setTenants(tenants.stream()
        .map(t -> new ContextOption(t.getId(), "tenant", t.getCode(), t.getName()))
        .collect(Collectors.toList()));

    options.setOrganizations(userOrgs.stream()
        .map(uo -> new ContextOption(uo.getOrganization().getId(), "organization",
            uo.getOrganization().getCode(), uo.getOrganization().getName(),
            uo.getOrganization().getTenant().getId()))
        .collect(Collectors.toList()));

    List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
    options.setCompanies(userCompanies.stream()
        .map(uc -> new ContextOption(uc.getCompany().getId(), "company",
            uc.getCompany().getCode(), uc.getCompany().getName(),
            uc.getCompany().getOrganization().getId()))
        .collect(Collectors.toList()));

    List<UserBranch> userBranches = userBranchRepository.findByUserId(userId);
    List<Branch> userBranchesResolved = userBranches.stream()
        .map(UserBranch::getBranch)
        .collect(Collectors.toList());
    options.setBranches(userBranchesResolved.stream()
        .map(b -> new ContextOption(b.getId(), "branch",
            b.getCode(), b.getName(), b.getCompany().getId()))
        .collect(Collectors.toList()));

    List<UUID> branchIds = userBranchesResolved.stream()
        .map(Branch::getId)
        .collect(Collectors.toList());
    if (!branchIds.isEmpty()) {
      List<Department> departments = departmentRepository.findByBranchIdIn(branchIds);
      options.setDepartments(departments.stream()
          .map(d -> new ContextOption(d.getId(), "department",
              d.getCode(), d.getName(), d.getBranch().getId()))
          .collect(Collectors.toList()));
    }

    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    options.setRoles(userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList()));

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
      List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(userId);
      boolean belongs = userOrgs.stream()
          .anyMatch(uo -> uo.getOrganization().getId().equals(request.getOrganizationId()));
      if (!belongs) {
        throw new SecurityException("User does not belong to this organization");
      }
      ctx.setOrganizationId(o.getId());
      ctx.setOrganizationCode(o.getCode());
      ctx.setOrganizationName(o.getName());
    }

    if (request.getCompanyId() != null) {
      Company c = companyRepository.findById(request.getCompanyId())
          .orElseThrow(() -> new IllegalArgumentException("Company not found"));
      List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
      boolean belongs = userCompanies.stream()
          .anyMatch(uc -> uc.getCompany().getId().equals(request.getCompanyId()));
      if (!belongs) {
        throw new SecurityException("User does not belong to this company");
      }
      ctx.setCompanyId(c.getId());
      ctx.setCompanyCode(c.getCode());
      ctx.setCompanyName(c.getName());

      List<Branch> branches = branchRepository.findByCompanyId(c.getId());
      if (branches.size() == 1) {
        Branch b = branches.get(0);
        ctx.setBranchId(b.getId());
        ctx.setBranchCode(b.getCode());
        ctx.setBranchName(b.getName());
      }
    }

    if (request.getBranchId() != null) {
      Branch b = branchRepository.findById(request.getBranchId())
          .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
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

    // Persist context selection to UserPreference so it survives across sessions
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
