package com.erp.platform.identity.service;

import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Branch;
import com.erp.platform.identity.entity.Company;
import com.erp.platform.identity.entity.Department;
import com.erp.platform.identity.entity.Organization;
import com.erp.platform.identity.entity.Tenant;
import com.erp.platform.identity.repository.BranchRepository;
import com.erp.platform.identity.repository.CompanyRepository;
import com.erp.platform.identity.repository.DepartmentRepository;
import com.erp.platform.identity.repository.OrganizationRepository;
import com.erp.platform.identity.repository.TenantRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

  private final TenantRepository tenantRepository;
  private final OrganizationRepository organizationRepository;
  private final CompanyRepository companyRepository;
  private final BranchRepository branchRepository;
  private final DepartmentRepository departmentRepository;
  private final AccessScopeService accessScopeService;

  public AdminService(TenantRepository tenantRepository,
                      OrganizationRepository organizationRepository,
                      CompanyRepository companyRepository,
                      BranchRepository branchRepository,
                      DepartmentRepository departmentRepository,
                      AccessScopeService accessScopeService) {
    this.tenantRepository = tenantRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.departmentRepository = departmentRepository;
    this.accessScopeService = accessScopeService;
  }

  private UUID currentUserId() {
    var ctx = RuntimeContextHolder.get();
    return ctx != null ? ctx.getUserId() : null;
  }

  // ─── Tenant ───
  public List<Tenant> getAllTenants() { return tenantRepository.findAll(); }
  public Tenant getTenant(UUID id) { return tenantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tenant not found")); }
  @Transactional public Tenant createTenant(Tenant t) { if (tenantRepository.findByCode(t.getCode()).isPresent()) throw new IllegalArgumentException("Tenant code already exists"); return tenantRepository.save(t); }
  @Transactional public Tenant updateTenant(UUID id, Tenant req) { Tenant t = getTenant(id); t.setName(req.getName()); t.setDomain(req.getDomain()); t.setLogoUrl(req.getLogoUrl()); t.setDefaultLanguage(req.getDefaultLanguage()); t.setDefaultTimezone(req.getDefaultTimezone()); t.setDefaultCurrency(req.getDefaultCurrency()); return tenantRepository.save(t); }
  @Transactional public void deactivateTenant(UUID id) { Tenant t = getTenant(id); t.setIsActive(false); tenantRepository.save(t); }
  @Transactional public void deleteTenant(UUID id) { tenantRepository.deleteById(id); }

  // ─── Organization ───
  public List<Organization> getAllOrganizations() {
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> ids = accessScopeService.getAccessibleOrganizationIds(userId);
    return ids.isEmpty() ? List.of() : organizationRepository.findByIdInWithTenant(ids);
  }
  public Organization getOrganization(UUID id) {
    Organization o = organizationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Organization not found"));
    UUID userId = currentUserId();
    if (userId != null && !accessScopeService.getAccessibleOrganizationIds(userId).contains(id))
      throw new SecurityException("Access denied to organization");
    return o;
  }
  @Transactional public Organization createOrganization(Organization o) {
    if (organizationRepository.findByCode(o.getCode()).isPresent()) throw new IllegalArgumentException("Organization code already exists");
    if (o.getParent() != null && o.getParent().getId() != null) {
      Organization parent = getOrganization(o.getParent().getId());
      o.setLevel(parent.getLevel() + 1);
      o.setPath(parent.getPath() + "/" + o.getCode());
      if (o.getTenant() == null) o.setTenant(parent.getTenant());
    } else {
      o.setLevel(0);
      o.setPath("/" + o.getCode());
    }
    return organizationRepository.save(o);
  }
  @Transactional public Organization updateOrganization(UUID id, Organization req) { getOrganization(id); Organization o = getOrganization(id); o.setName(req.getName()); o.setDescription(req.getDescription()); if (req.getTenant() != null) o.setTenant(req.getTenant()); if (req.getParent() != null) o.setParent(req.getParent()); return organizationRepository.save(o); }
  @Transactional public void deleteOrganization(UUID id) { getOrganization(id); organizationRepository.deleteById(id); }
  public List<Organization> getOrganizationTree(UUID tenantId) {
    List<Organization> all = organizationRepository.findByTenantId(tenantId);
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> accessibleIds = accessScopeService.getAccessibleOrganizationIds(userId);
    return all.stream().filter(o -> accessibleIds.contains(o.getId())).toList();
  }

  // ─── Company ───
  public List<Company> getAllCompanies() {
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> ids = accessScopeService.getAccessibleCompanyIds(userId);
    return ids.isEmpty() ? List.of() : companyRepository.findByIdInWithOrganization(ids);
  }
  public Company getCompany(UUID id) {
    Company c = companyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company not found"));
    UUID userId = currentUserId();
    if (userId != null && !accessScopeService.getAccessibleCompanyIds(userId).contains(id))
      throw new SecurityException("Access denied to company");
    return c;
  }
  @Transactional public Company createCompany(Company c) {
    if (companyRepository.findByCode(c.getCode()).isPresent()) throw new IllegalArgumentException("Company code already exists");
    if (c.getTenant() == null && c.getOrganization() != null && c.getOrganization().getId() != null) {
      Organization org = getOrganization(c.getOrganization().getId());
      c.setTenant(org.getTenant());
    }
    return companyRepository.save(c);
  }
  @Transactional public Company updateCompany(UUID id, Company req) { getCompany(id); Company c = getCompany(id); c.setName(req.getName()); c.setTaxId(req.getTaxId()); c.setRegistrationNumber(req.getRegistrationNumber()); c.setAddress(req.getAddress()); c.setPhone(req.getPhone()); c.setEmail(req.getEmail()); c.setCurrency(req.getCurrency()); if (req.getOrganization() != null) c.setOrganization(req.getOrganization()); return companyRepository.save(c); }
  @Transactional public void deleteCompany(UUID id) { getCompany(id); companyRepository.deleteById(id); }
  public List<Company> getCompaniesByOrganization(UUID orgId) {
    List<Company> all = companyRepository.findByOrganizationId(orgId);
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> accessibleIds = accessScopeService.getAccessibleCompanyIds(userId);
    return all.stream().filter(c -> accessibleIds.contains(c.getId())).toList();
  }

  // ─── Branch ───
  public List<Branch> getAllBranches() {
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> ids = accessScopeService.getAccessibleBranchIds(userId);
    return ids.isEmpty() ? List.of() : branchRepository.findByIdInWithCompany(ids);
  }
  public Branch getBranch(UUID id) {
    Branch b = branchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Branch not found"));
    UUID userId = currentUserId();
    if (userId != null && !accessScopeService.getAccessibleBranchIds(userId).contains(id))
      throw new SecurityException("Access denied to branch");
    return b;
  }
  @Transactional public Branch createBranch(Branch b) {
    if (branchRepository.findByCode(b.getCode()).isPresent()) throw new IllegalArgumentException("Branch code already exists");
    if (b.getTenant() == null && b.getCompany() != null && b.getCompany().getId() != null) {
      Company company = getCompany(b.getCompany().getId());
      b.setTenant(company.getTenant());
    }
    return branchRepository.save(b);
  }
  @Transactional public Branch updateBranch(UUID id, Branch req) { getBranch(id); Branch b = getBranch(id); b.setName(req.getName()); b.setAddress(req.getAddress()); b.setPhone(req.getPhone()); b.setEmail(req.getEmail()); b.setIsHeadOffice(req.getIsHeadOffice()); if (req.getCompany() != null) b.setCompany(req.getCompany()); return branchRepository.save(b); }
  @Transactional public void deleteBranch(UUID id) { getBranch(id); branchRepository.deleteById(id); }
  public List<Branch> getBranchesByCompany(UUID companyId) {
    List<Branch> all = branchRepository.findByCompanyId(companyId);
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> accessibleIds = accessScopeService.getAccessibleBranchIds(userId);
    return all.stream().filter(b -> accessibleIds.contains(b.getId())).toList();
  }

  // ─── Department ───
  public List<Department> getAllDepartments() {
    UUID userId = currentUserId();
    if (userId == null) return List.of();
    List<UUID> ids = accessScopeService.getAccessibleBranchIds(userId);
    return ids.isEmpty() ? List.of() : departmentRepository.findByBranchIdInWithBranch(ids);
  }
  public Department getDepartment(UUID id) {
    Department d = departmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found"));
    UUID userId = currentUserId();
    if (userId != null) {
      List<UUID> branchIds = accessScopeService.getAccessibleBranchIds(userId);
      if (d.getBranch() != null && !branchIds.contains(d.getBranch().getId()))
        throw new SecurityException("Access denied to department");
    }
    return d;
  }
  @Transactional public Department createDepartment(Department d) {
    if (departmentRepository.findByCode(d.getCode()).isPresent()) throw new IllegalArgumentException("Department code already exists");
    if (d.getParent() != null && d.getParent().getId() != null) {
      Department parent = getDepartment(d.getParent().getId());
      d.setLevel(parent.getLevel() + 1);
      if (d.getTenant() == null) d.setTenant(parent.getTenant());
    } else {
      d.setLevel(0);
    }
    if (d.getTenant() == null && d.getBranch() != null && d.getBranch().getId() != null) {
      Branch branch = getBranch(d.getBranch().getId());
      d.setTenant(branch.getTenant());
    }
    return departmentRepository.save(d);
  }
  @Transactional public Department updateDepartment(UUID id, Department req) { getDepartment(id); Department d = getDepartment(id); d.setName(req.getName()); d.setDescription(req.getDescription()); d.setCostCenter(req.getCostCenter()); if (req.getBranch() != null) d.setBranch(req.getBranch()); return departmentRepository.save(d); }
  @Transactional public void deleteDepartment(UUID id) { getDepartment(id); departmentRepository.deleteById(id); }
  public List<Department> getDepartmentsByBranch(UUID branchId) {
    List<Department> all = departmentRepository.findByBranchId(branchId);
    UUID userId = currentUserId();
    if (userId != null) {
      List<UUID> accessibleBranchIds = accessScopeService.getAccessibleBranchIds(userId);
      return all.stream().filter(d -> d.getBranch() == null || accessibleBranchIds.contains(d.getBranch().getId())).toList();
    }
    return all;
  }
}
