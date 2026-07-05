package com.erp.modules.projects.service;

import com.erp.common.base.BaseService;
import com.erp.modules.projects.entity.Project;
import com.erp.modules.projects.entity.Task;
import com.erp.modules.projects.repository.ProjectRepository;
import com.erp.modules.projects.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService extends BaseService<Project> {

  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;

  public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
    this.projectRepository = projectRepository;
    this.taskRepository = taskRepository;
  }

  @Override
  protected JpaRepository<Project, UUID> getRepository() {
    return projectRepository;
  }

  @Override
  protected void beforeCreate(Project entity) {
    if (entity.getProjectCode() == null) {
      entity.setProjectCode("PRJ-" + System.currentTimeMillis());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("OPEN");
    }
  }

  @Override
  protected void beforeUpdate(Project newEntity, Project existingEntity) {
    if ("COMPLETED".equals(existingEntity.getStatus()) || "CLOSED".equals(existingEntity.getStatus())) {
      throw new IllegalArgumentException("Cannot modify a " + existingEntity.getStatus() + " project");
    }
    newEntity.setProjectCode(existingEntity.getProjectCode());
  }

  public List<Task> getTasks(UUID projectId) {
    return taskRepository.findByProjectId(projectId);
  }

  @Transactional
  public UUID complete(UUID projectId) {
    Project project = findByIdOrThrow(projectId);
    project.setStatus("COMPLETED");
    List<Task> tasks = taskRepository.findByProjectId(projectId);
    for (Task task : tasks) {
      if (!"COMPLETED".equals(task.getStatus()) && !"CLOSED".equals(task.getStatus())) {
        task.setStatus("COMPLETED");
        taskRepository.save(task);
      }
    }
    return projectRepository.save(project).getId();
  }

  @Transactional
  public UUID close(UUID projectId) {
    Project project = findByIdOrThrow(projectId);
    project.setStatus("CLOSED");
    return projectRepository.save(project).getId();
  }
}
