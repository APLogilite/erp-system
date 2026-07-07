package com.erp.platform.identity.dto;

import com.erp.platform.identity.service.AccessScopeService.RoleScope;
import java.util.List;
import java.util.Map;

public class ContextOptionsResponse {

  private List<ContextOption> tenants;
  private List<ContextOption> organizations;
  private List<ContextOption> companies;
  private List<ContextOption> branches;
  private List<ContextOption> departments;
  private List<String> roles;
  private Map<String, RoleScope> roleScopes;

  public List<ContextOption> getTenants() { return tenants; }
  public void setTenants(List<ContextOption> tenants) { this.tenants = tenants; }
  public List<ContextOption> getOrganizations() { return organizations; }
  public void setOrganizations(List<ContextOption> organizations) { this.organizations = organizations; }
  public List<ContextOption> getCompanies() { return companies; }
  public void setCompanies(List<ContextOption> companies) { this.companies = companies; }
  public List<ContextOption> getBranches() { return branches; }
  public void setBranches(List<ContextOption> branches) { this.branches = branches; }
  public List<ContextOption> getDepartments() { return departments; }
  public void setDepartments(List<ContextOption> departments) { this.departments = departments; }
  public List<String> getRoles() { return roles; }
  public void setRoles(List<String> roles) { this.roles = roles; }
  public Map<String, RoleScope> getRoleScopes() { return roleScopes; }
  public void setRoleScopes(Map<String, RoleScope> roleScopes) { this.roleScopes = roleScopes; }
}
