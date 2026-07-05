package com.erp.modules.hr.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.hr.dto.EmployeeRequest;
import com.erp.modules.hr.dto.EmployeeResponse;
import com.erp.modules.hr.entity.Employee;
import com.erp.modules.hr.service.EmployeeService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<EmployeeResponse>> create(@RequestBody EmployeeRequest request) {
    Employee entity = new Employee();
    entity.setEmployeeCode(request.getEmployeeCode());
    entity.setFirstName(request.getFirstName());
    entity.setLastName(request.getLastName());
    entity.setEmail(request.getEmail());
    entity.setPhone(request.getPhone());
    entity.setDepartmentId(request.getDepartmentId());
    entity.setDesignation(request.getDesignation());
    entity.setManagerId(request.getManagerId());
    entity.setJoiningDate(request.getJoiningDate());
    Employee saved = employeeService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Employee created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAll() {
    List<EmployeeResponse> list = employeeService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Employees retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable UUID id) {
    Employee entity = employeeService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Employee retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<EmployeeResponse>> update(@PathVariable UUID id, @RequestBody EmployeeRequest request) {
    Employee existing = employeeService.findByIdOrThrow(id);
    existing.setEmployeeCode(request.getEmployeeCode());
    existing.setFirstName(request.getFirstName());
    existing.setLastName(request.getLastName());
    existing.setEmail(request.getEmail());
    existing.setPhone(request.getPhone());
    existing.setDepartmentId(request.getDepartmentId());
    existing.setDesignation(request.getDesignation());
    existing.setManagerId(request.getManagerId());
    existing.setJoiningDate(request.getJoiningDate());
    Employee updated = employeeService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Employee updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    employeeService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Employee deleted"));
  }

  private EmployeeResponse toResponse(Employee entity) {
    EmployeeResponse r = new EmployeeResponse();
    r.setId(entity.getId());
    r.setEmployeeCode(entity.getEmployeeCode());
    r.setFirstName(entity.getFirstName());
    r.setLastName(entity.getLastName());
    r.setEmail(entity.getEmail());
    r.setPhone(entity.getPhone());
    r.setDepartmentId(entity.getDepartmentId());
    r.setDesignation(entity.getDesignation());
    r.setManagerId(entity.getManagerId());
    r.setJoiningDate(entity.getJoiningDate());
    r.setStatus(entity.getStatus());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
