package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Department;
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
@RequestMapping("/api/v1/identity/departments")
public class DepartmentAdminController {

  private final AdminService adminService;
  public DepartmentAdminController(AdminService adminService) { this.adminService = adminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Department>>> getAll() { return ResponseEntity.ok(ApiResponse.success(adminService.getAllDepartments(), "Departments retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Department>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(adminService.getDepartment(id), "Department retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Department>> create(@RequestBody Department d) { return ResponseEntity.ok(ApiResponse.success(adminService.createDepartment(d), "Department created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Department>> update(@PathVariable UUID id, @RequestBody Department d) { return ResponseEntity.ok(ApiResponse.success(adminService.updateDepartment(id, d), "Department updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) { adminService.deleteDepartment(id); return ResponseEntity.ok(ApiResponse.successMessage("Department deleted")); }
  @GetMapping("/by-branch") public ResponseEntity<ApiResponse<List<Department>>> byBranch(@RequestParam UUID branchId) { return ResponseEntity.ok(ApiResponse.success(adminService.getDepartmentsByBranch(branchId), "Departments by branch")); }
}
