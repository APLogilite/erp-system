package com.erp.modules.projects.service;

import com.erp.common.base.BaseService;
import com.erp.modules.projects.entity.Task;
import com.erp.modules.projects.repository.TaskRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService extends BaseService<Task> {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  protected JpaRepository<Task, UUID> getRepository() {
    return taskRepository;
  }

  @Override
  protected void beforeCreate(Task entity) {
    if (entity.getTaskNumber() == null) {
      entity.setTaskNumber("TSK-" + System.currentTimeMillis());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("OPEN");
    }
    if (entity.getPriority() == null) {
      entity.setPriority("MEDIUM");
    }
  }

  @Override
  protected void beforeUpdate(Task newEntity, Task existingEntity) {
    if ("CLOSED".equals(existingEntity.getStatus())) {
      throw new IllegalArgumentException("Cannot modify a CLOSED task");
    }
    newEntity.setTaskNumber(existingEntity.getTaskNumber());
  }

  @Transactional
  public UUID assign(UUID taskId, UUID employeeId) {
    Task task = findByIdOrThrow(taskId);
    task.setAssignedTo(employeeId);
    if ("OPEN".equals(task.getStatus())) {
      task.setStatus("ASSIGNED");
    }
    return taskRepository.save(task).getId();
  }

  @Transactional
  public UUID start(UUID taskId) {
    Task task = findByIdOrThrow(taskId);
    if (!"ASSIGNED".equals(task.getStatus()) && !"OPEN".equals(task.getStatus())) {
      throw new IllegalArgumentException("Task must be OPEN or ASSIGNED to start");
    }
    task.setStatus("IN_PROGRESS");
    return taskRepository.save(task).getId();
  }

  @Transactional
  public UUID complete(UUID taskId) {
    Task task = findByIdOrThrow(taskId);
    if (!"IN_PROGRESS".equals(task.getStatus())) {
      throw new IllegalArgumentException("Only IN_PROGRESS tasks can be completed");
    }
    task.setStatus("COMPLETED");
    return taskRepository.save(task).getId();
  }

  @Transactional
  public UUID close(UUID taskId) {
    Task task = findByIdOrThrow(taskId);
    if (!"COMPLETED".equals(task.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED tasks can be closed");
    }
    task.setStatus("CLOSED");
    return taskRepository.save(task).getId();
  }
}
