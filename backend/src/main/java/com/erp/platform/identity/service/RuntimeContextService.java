package com.erp.platform.identity.service;

import com.erp.platform.identity.dto.ContextOption;
import com.erp.platform.identity.dto.ContextOptionsResponse;
import com.erp.platform.identity.dto.ContextSwitchRequest;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Branch;
import com.erp.platform.identity.entity.Company;
import com.erp.platform.identity.entity.Organization;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.Tenant;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserCompany;
import com.erp.platform.identity.entity.UserOrganization;
import com.erp.platform.identity.entity.UserPreference;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.BranchRepository;
import com.erp.platform.identity.repository.CompanyRepository;
import com.erp.platform.identity.repository.OrganizationRepository;
import com.erp.platform.identity.repository.TenantRepository;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.repository.UserCompanyRepository;
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
  private final UserOrganizationRepository userOrganizationRepository;
  private final UserCompanyRepository userCompanyRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserPreferenceRepository userPreferenceRepository;

  public RuntimeContextService(UserAccountRepository userRepository,
                               TenantRepository tenantRepository,
                               OrganizationRepository organizationRepository,
                               CompanyRepository companyRepository,
                               BranchRepository branchRepository,
                               UserOrganizationRepository userOrganizationRepository,
                               UserCompanyRepository userCompanyRepository,
                               UserRoleRepository userRoleRepository,
                               UserPreferenceRepository userPreferenceRepository) {
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.userOrganizationRepository = userOrganizationRepository;
    this.userCompanyRepository = userCompanyRepository;
    this.userRoleRepository = userRoleRepository;
    this.userPreferenceRepository = userPreferenceRepository;
  }

  @Transactional(readOnly = true)
  public RuntimeContext resolve(UUID userId) {
    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    RuntimeContext ctx = new RuntimeContext();
    ctx.setUserId(user.getId());
    ctx.setUsername(user.getUsername());
    ctx.setEmail(user.getEmail());
    ctx.setDisplayName(buildDisplayName(user));

    List<Tenant> tenants = tenantRepository.findAll();
    if (tenants.size() == 1) {
      Tenant t = tenants.get(0);
      ctx.setTenantId(t.getId());
      ctx.setTenantCode(t.getCode());
      ctx.setTenantName(t.getName());

      List<Organization> orgs = organizationRepository.findByTenantId(t.getId());
      if (orgs.size() == 1) {
        Organization o = orgs.get(0);
        ctx.setOrganizationId(o.getId());
        ctx.setOrganizationCode(o.getCode());
        ctx.setOrganizationName(o.getName());

        List<Company> companies = companyRepository.findByOrganizationId(o.getId());
        if (companies.size() == 1) {
          Company c = companies.get(0);
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
      }
    }

    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    List<String> roleCodes = userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());
    ctx.setRoles(roleCodes);

    UserPreference prefs = userPreferenceRepository.findByUserId(userId).orElse(null);
    if (prefs != null) {
      ctx.setLanguage(prefs.getLanguage());
      ctx.setTimezone(prefs.getTimezone());
      ctx.setCurrency(prefs.getCurrency());
      ctx.setDateFormat(prefs.getDateFormat());
      ctx.setNumberFormat(prefs.getNumberFormat());
      ctx.setTheme(prefs.getTheme());
    }

    return ctx;
  }

  @Transactional(readOnly = true)
  public ContextOptionsResponse getAvailableOptions(UUID userId) {
    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    ContextOptionsResponse options = new ContextOptionsResponse();

    List<Tenant> tenants = tenantRepository.findAll();
    options.setTenants(tenants.stream()
        .map(t -> new ContextOption(t.getId(), "tenant", t.getCode(), t.getName()))
        .collect(Collectors.toList()));

    List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(userId);
    options.setOrganizations(userOrgs.stream()
        .map(uo -> new ContextOption(uo.getOrganization().getId(), "organization",
            uo.getOrganization().getCode(), uo.getOrganization().getName()))
        .collect(Collectors.toList()));

    List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
    options.setCompanies(userCompanies.stream()
        .map(uc -> new ContextOption(uc.getCompany().getId(), "company",
            uc.getCompany().getCode(), uc.getCompany().getName()))
        .collect(Collectors.toList()));

    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    options.setRoles(userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList()));

    return options;
  }

  @Transactional(readOnly = true)
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

    if (request.getRoleCode() != null) {
      List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
      boolean hasRole = userRoles.stream()
          .anyMatch(ur -> ur.getRole().getCode().equals(request.getRoleCode()));
      if (!hasRole) {
        throw new SecurityException("User does not have this role");
      }
      ctx.setRoles(Collections.singletonList(request.getRoleCode()));
    }

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

  private String buildDisplayName(UserAccount user) {
    if (user.getFirstName() != null && user.getLastName() != null) {
      return user.getFirstName() + " " + user.getLastName();
    }
    return user.getUsername();
  }
}
