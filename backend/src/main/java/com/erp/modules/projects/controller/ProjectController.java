package com.erp.modules.projects.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.projects.dto.ProjectRequest;
import com.erp.modules.projects.dto.ProjectResponse;
import com.erp.modules.projects.dto.TaskResponse;
import com.erp.modules.projects.entity.Project;
import com.erp.modules.projects.entity.Task;
import com.erp.modules.projects.service.ProjectService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody ProjectRequest request) {
    Project entity = new Project();
    entity.setProjectCode(request.getProjectCode());
    entity.setName(request.getName());
    entity.setCustomerId(request.getCustomerId());
    entity.setManagerId(request.getManagerId());
    entity.setStartDate(request.getStartDate());
    entity.setEndDate(request.getEndDate());
    entity.setBudget(request.getBudget());
    Project saved = projectService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Project created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAll() {
    List<ProjectResponse> list = projectService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Projects retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable UUID id) {
    Project entity = projectService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Project retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProjectResponse>> update(@PathVariable UUID id, @RequestBody ProjectRequest request) {
    Project existing = projectService.findByIdOrThrow(id);
    existing.setName(request.getName());
    existing.setCustomerId(request.getCustomerId());
    existing.setManagerId(request.getManagerId());
    existing.setStartDate(request.getStartDate());
    existing.setEndDate(request.getEndDate());
    existing.setBudget(request.getBudget());
    Project updated = projectService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Project updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    projectService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Project deleted"));
  }

  @GetMapping("/{id}/tasks")
  public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(@PathVariable UUID id) {
    List<Task> tasks = projectService.getTasks(id);
    List<TaskResponse> list = tasks.stream().map(this::toTaskResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Tasks retrieved"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    projectService.complete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Project completed"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    projectService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Project closed"));
  }

  private ProjectResponse toResponse(Project entity) {
    ProjectResponse r = new ProjectResponse();
    r.setId(entity.getId());
    r.setProjectCode(entity.getProjectCode());
    r.setName(entity.getName());
    r.setCustomerId(entity.getCustomerId());
    r.setManagerId(entity.getManagerId());
    r.setStartDate(entity.getStartDate());
    r.setEndDate(entity.getEndDate());
    r.setStatus(entity.getStatus());
    r.setBudget(entity.getBudget());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }

  private TaskResponse toTaskResponse(Task task) {
    TaskResponse r = new TaskResponse();
    r.setId(task.getId());
    r.setTaskNumber(task.getTaskNumber());
    r.setTitle(task.getTitle());
    r.setDescription(task.getDescription());
    r.setPriority(task.getPriority());
    r.setAssignedTo(task.getAssignedTo());
    r.setProjectId(task.getProjectId());
    r.setPlannedHours(task.getPlannedHours());
    r.setActualHours(task.getActualHours());
    r.setStatus(task.getStatus());
    return r;
  }
}
