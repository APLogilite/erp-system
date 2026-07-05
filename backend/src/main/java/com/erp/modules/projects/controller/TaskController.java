package com.erp.modules.projects.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.projects.dto.TaskRequest;
import com.erp.modules.projects.dto.TaskResponse;
import com.erp.modules.projects.entity.Task;
import com.erp.modules.projects.service.TaskService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody TaskRequest request) {
    Task entity = new Task();
    entity.setTaskNumber(request.getTaskNumber());
    entity.setTitle(request.getTitle());
    entity.setDescription(request.getDescription());
    entity.setPriority(request.getPriority());
    entity.setAssignedTo(request.getAssignedTo());
    entity.setProjectId(request.getProjectId());
    entity.setPlannedHours(request.getPlannedHours());
    Task saved = taskService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Task created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TaskResponse>>> getAll() {
    List<TaskResponse> list = taskService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Tasks retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable UUID id) {
    Task entity = taskService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Task retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskResponse>> update(@PathVariable UUID id, @RequestBody TaskRequest request) {
    Task existing = taskService.findByIdOrThrow(id);
    existing.setTitle(request.getTitle());
    existing.setDescription(request.getDescription());
    existing.setPriority(request.getPriority());
    existing.setAssignedTo(request.getAssignedTo());
    existing.setPlannedHours(request.getPlannedHours());
    Task updated = taskService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Task updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    taskService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Task deleted"));
  }

  @PostMapping("/{id}/assign")
  public ResponseEntity<ApiResponse<Void>> assign(@PathVariable UUID id, @RequestBody UUID employeeId) {
    taskService.assign(id, employeeId);
    return ResponseEntity.ok(ApiResponse.successMessage("Task assigned"));
  }

  @PostMapping("/{id}/start")
  public ResponseEntity<ApiResponse<Void>> start(@PathVariable UUID id) {
    taskService.start(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Task started"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    taskService.complete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Task completed"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    taskService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Task closed"));
  }

  private TaskResponse toResponse(Task entity) {
    TaskResponse r = new TaskResponse();
    r.setId(entity.getId());
    r.setTaskNumber(entity.getTaskNumber());
    r.setTitle(entity.getTitle());
    r.setDescription(entity.getDescription());
    r.setPriority(entity.getPriority());
    r.setAssignedTo(entity.getAssignedTo());
    r.setProjectId(entity.getProjectId());
    r.setPlannedHours(entity.getPlannedHours());
    r.setActualHours(entity.getActualHours());
    r.setStatus(entity.getStatus());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
