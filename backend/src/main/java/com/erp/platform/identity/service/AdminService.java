package com.erp.platform.identity.service;

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
import com.erp.platform.identity.sdk.annotation.EnableTenantFilter;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

  private final TenantRepository tenantRepository;
  private final OrganizationRepository organizationRepository;
  private final CompanyRepository companyRepository;
  private final BranchRepository branchRepository;
  private final DepartmentRepository departmentRepository;

  public AdminService(TenantRepository tenantRepository,
                      OrganizationRepository organizationRepository,
                      CompanyRepository companyRepository,
                      BranchRepository branchRepository,
                      DepartmentRepository departmentRepository) {
    this.tenantRepository = tenantRepository;
    this.organizationRepository = organizationRepository;
    this.companyRepository = companyRepository;
    this.branchRepository = branchRepository;
    this.departmentRepository = departmentRepository;
  }

  // --- Tenant ---
  public List<Tenant> getAllTenants() { return tenantRepository.findAll(); }
  public Tenant getTenant(UUID id) { return tenantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tenant not found")); }
  @Transactional public Tenant createTenant(Tenant t) { if (tenantRepository.findByCode(t.getCode()).isPresent()) throw new IllegalArgumentException("Tenant code already exists"); return tenantRepository.save(t); }
  @Transactional public Tenant updateTenant(UUID id, Tenant req) { Tenant t = getTenant(id); t.setName(req.getName()); t.setDomain(req.getDomain()); t.setLogoUrl(req.getLogoUrl()); t.setDefaultLanguage(req.getDefaultLanguage()); t.setDefaultTimezone(req.getDefaultTimezone()); t.setDefaultCurrency(req.getDefaultCurrency()); return tenantRepository.save(t); }
  @Transactional public void deactivateTenant(UUID id) { Tenant t = getTenant(id); t.setIsActive(false); tenantRepository.save(t); }
  @Transactional public void deleteTenant(UUID id) { tenantRepository.deleteById(id); }

  // --- Organization ---
  @EnableTenantFilter
  public List<Organization> getAllOrganizations() { return organizationRepository.findAllWithTenant(); }
  public Organization getOrganization(UUID id) { return organizationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Organization not found")); }
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
  @Transactional public Organization updateOrganization(UUID id, Organization req) { Organization o = getOrganization(id); o.setName(req.getName()); o.setDescription(req.getDescription()); return organizationRepository.save(o); }
  @Transactional public void deleteOrganization(UUID id) { organizationRepository.deleteById(id); }
  public List<Organization> getOrganizationTree(UUID tenantId) { return organizationRepository.findByTenantId(tenantId); }

  // --- Company ---
  @EnableTenantFilter
  public List<Company> getAllCompanies() { return companyRepository.findAllWithOrganization(); }
  public Company getCompany(UUID id) { return companyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company not found")); }
  @Transactional public Company createCompany(Company c) {
    if (companyRepository.findByCode(c.getCode()).isPresent()) throw new IllegalArgumentException("Company code already exists");
    if (c.getTenant() == null && c.getOrganization() != null && c.getOrganization().getId() != null) {
      Organization org = getOrganization(c.getOrganization().getId());
      c.setTenant(org.getTenant());
    }
    return companyRepository.save(c);
  }
  @Transactional public Company updateCompany(UUID id, Company req) { Company c = getCompany(id); c.setName(req.getName()); c.setTaxId(req.getTaxId()); c.setAddress(req.getAddress()); c.setPhone(req.getPhone()); c.setEmail(req.getEmail()); c.setCurrency(req.getCurrency()); return companyRepository.save(c); }
  @Transactional public void deleteCompany(UUID id) { companyRepository.deleteById(id); }
  public List<Company> getCompaniesByOrganization(UUID orgId) { return companyRepository.findByOrganizationId(orgId); }

  // --- Branch ---
  @EnableTenantFilter
  public List<Branch> getAllBranches() { return branchRepository.findAllWithCompany(); }
  public Branch getBranch(UUID id) { return branchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Branch not found")); }
  @Transactional public Branch createBranch(Branch b) {
    if (branchRepository.findByCode(b.getCode()).isPresent()) throw new IllegalArgumentException("Branch code already exists");
    if (b.getTenant() == null && b.getCompany() != null && b.getCompany().getId() != null) {
      Company company = getCompany(b.getCompany().getId());
      b.setTenant(company.getTenant());
    }
    return branchRepository.save(b);
  }
  @Transactional public Branch updateBranch(UUID id, Branch req) { Branch b = getBranch(id); b.setName(req.getName()); b.setAddress(req.getAddress()); b.setPhone(req.getPhone()); b.setEmail(req.getEmail()); b.setIsHeadOffice(req.getIsHeadOffice()); return branchRepository.save(b); }
  @Transactional public void deleteBranch(UUID id) { branchRepository.deleteById(id); }
  public List<Branch> getBranchesByCompany(UUID companyId) { return branchRepository.findByCompanyId(companyId); }

  // --- Department ---
  @EnableTenantFilter
  public List<Department> getAllDepartments() { return departmentRepository.findAllWithBranch(); }
  public Department getDepartment(UUID id) { return departmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found")); }
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
  @Transactional public Department updateDepartment(UUID id, Department req) { Department d = getDepartment(id); d.setName(req.getName()); d.setDescription(req.getDescription()); d.setCostCenter(req.getCostCenter()); return departmentRepository.save(d); }
  @Transactional public void deleteDepartment(UUID id) { departmentRepository.deleteById(id); }
  public List<Department> getDepartmentsByBranch(UUID branchId) { return departmentRepository.findByBranchId(branchId); }
}
