package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Branch;
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
@RequestMapping("/api/v1/identity/branches")
public class BranchAdminController {

  private final AdminService adminService;
  public BranchAdminController(AdminService adminService) { this.adminService = adminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Branch>>> getAll() { return ResponseEntity.ok(ApiResponse.success(adminService.getAllBranches(), "Branches retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Branch>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(adminService.getBranch(id), "Branch retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Branch>> create(@RequestBody Branch b) { return ResponseEntity.ok(ApiResponse.success(adminService.createBranch(b), "Branch created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Branch>> update(@PathVariable UUID id, @RequestBody Branch b) { return ResponseEntity.ok(ApiResponse.success(adminService.updateBranch(id, b), "Branch updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) { adminService.deleteBranch(id); return ResponseEntity.ok(ApiResponse.successMessage("Branch deleted")); }
  @GetMapping("/by-company") public ResponseEntity<ApiResponse<List<Branch>>> byCompany(@RequestParam UUID companyId) { return ResponseEntity.ok(ApiResponse.success(adminService.getBranchesByCompany(companyId), "Branches by company")); }
}
