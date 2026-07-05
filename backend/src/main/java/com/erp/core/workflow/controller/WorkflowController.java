package com.erp.core.workflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.workflow.dto.WorkflowHistoryDto;
import com.erp.core.workflow.dto.WorkflowTransitionRequestDto;
import com.erp.core.workflow.dto.WorkflowTransitionResponseDto;
import com.erp.core.workflow.dto.WorkflowTransitionResultDto;
import com.erp.core.workflow.service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/workflow")
public class WorkflowController {

  private final WorkflowService workflowService;

  public WorkflowController(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @GetMapping("/{model}/{id}/transitions")
  public ResponseEntity<ApiResponse<List<WorkflowTransitionResponseDto>>> getAvailableTransitions(
      @PathVariable String model,
      @PathVariable UUID id,
      @RequestParam(required = false) String currentState) {

    List<WorkflowTransitionResponseDto> transitions = workflowService.getAvailableTransitions(model, id, currentState);
    return ResponseEntity.ok(ApiResponse.success(transitions, "Available transitions retrieved."));
  }

  @PostMapping("/{model}/{id}/transition")
  public ResponseEntity<ApiResponse<WorkflowTransitionResultDto>> executeTransition(
      @PathVariable String model,
      @PathVariable UUID id,
      @RequestBody WorkflowTransitionRequestDto request) {

    WorkflowTransitionResultDto result = workflowService.executeTransition(model, id, request);
    return ResponseEntity.ok(ApiResponse.success(result, "Workflow transition executed."));
  }

  @GetMapping("/{model}/{id}/history")
  public ResponseEntity<ApiResponse<List<WorkflowHistoryDto>>> getWorkflowHistory(
      @PathVariable String model,
      @PathVariable UUID id) {

    List<WorkflowHistoryDto> history = workflowService.getHistory(model, id);
    return ResponseEntity.ok(ApiResponse.success(history, "Workflow history retrieved."));
  }
}
