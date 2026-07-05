package com.erp.modules.hr.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.hr.dto.DepartmentRequest;
import com.erp.modules.hr.dto.DepartmentResponse;
import com.erp.modules.hr.entity.Department;
import com.erp.modules.hr.service.DepartmentService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  public DepartmentController(DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<DepartmentResponse>> create(@RequestBody DepartmentRequest request) {
    Department entity = new Department();
    entity.setDepartmentCode(request.getDepartmentCode());
    entity.setName(request.getName());
    entity.setParentDepartmentId(request.getParentDepartmentId());
    entity.setManagerId(request.getManagerId());
    Department saved = departmentService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Department created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll() {
    List<DepartmentResponse> list = departmentService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Departments retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable UUID id) {
    Department entity = departmentService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Department retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<DepartmentResponse>> update(@PathVariable UUID id, @RequestBody DepartmentRequest request) {
    Department existing = departmentService.findByIdOrThrow(id);
    existing.setDepartmentCode(request.getDepartmentCode());
    existing.setName(request.getName());
    existing.setParentDepartmentId(request.getParentDepartmentId());
    existing.setManagerId(request.getManagerId());
    Department updated = departmentService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Department updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    departmentService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Department deleted"));
  }

  @GetMapping("/roots")
  public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getRoots() {
    List<DepartmentResponse> list = departmentService.getRootDepartments().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Root departments retrieved"));
  }

  @GetMapping("/{parentId}/children")
  public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getChildren(@PathVariable UUID parentId) {
    List<DepartmentResponse> list = departmentService.getChildren(parentId).stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Child departments retrieved"));
  }

  private DepartmentResponse toResponse(Department entity) {
    DepartmentResponse r = new DepartmentResponse();
    r.setId(entity.getId());
    r.setDepartmentCode(entity.getDepartmentCode());
    r.setName(entity.getName());
    r.setParentDepartmentId(entity.getParentDepartmentId());
    r.setManagerId(entity.getManagerId());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
