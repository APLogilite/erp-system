package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Company;
import com.erp.platform.identity.service.AdminService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/companies")
public class CompanyAdminController {

  private final AdminService adminService;
  public CompanyAdminController(AdminService adminService) { this.adminService = adminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Company>>> getAll() { return ResponseEntity.ok(ApiResponse.success(adminService.getAllCompanies(), "Companies retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Company>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(adminService.getCompany(id), "Company retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Company>> create(@RequestBody Company c) { return ResponseEntity.ok(ApiResponse.success(adminService.createCompany(c), "Company created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Company>> update(@PathVariable UUID id, @RequestBody Company c) { return ResponseEntity.ok(ApiResponse.success(adminService.updateCompany(id, c), "Company updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) { adminService.deleteCompany(id); return ResponseEntity.ok(ApiResponse.successMessage("Company deleted")); }
  @GetMapping("/by-org") public ResponseEntity<ApiResponse<List<Company>>> byOrg(@RequestParam UUID orgId) { return ResponseEntity.ok(ApiResponse.success(adminService.getCompaniesByOrganization(orgId), "Companies by org")); }
}
