package com.erp.core.workflow.service;

import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.dto.WorkflowTransitionDto;
import com.erp.core.metadata.dto.WorkflowStateDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import com.erp.core.workflow.dto.WorkflowHistoryDto;
import com.erp.core.workflow.dto.WorkflowTransitionRequestDto;
import com.erp.core.workflow.dto.WorkflowTransitionResponseDto;
import com.erp.core.workflow.dto.WorkflowTransitionResultDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowService {

  private final MetadataRegistry metadataRegistry;
  private final WorkflowEngine workflowEngine;

  public WorkflowService(MetadataRegistry metadataRegistry, WorkflowEngine workflowEngine) {
    this.metadataRegistry = metadataRegistry;
    this.workflowEngine = workflowEngine;
  }

  public List<WorkflowTransitionResponseDto> getAvailableTransitions(
      String model,
      UUID recordId,
      String currentState) {

    WorkflowMetadataDto workflow = resolveWorkflow(model);
    Map<String, Object> context = buildTransitionContext(model, recordId);
    return workflowEngine.getAvailableTransitions(workflow, currentState, context).stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());
  }

  public WorkflowTransitionResultDto executeTransition(
      String model,
      UUID recordId,
      WorkflowTransitionRequestDto request) {

    WorkflowMetadataDto workflow = resolveWorkflow(model);
    Map<String, Object> baseContext = buildTransitionContext(model, recordId);
    Map<String, Object> guardContext = mergeContexts(baseContext, request.getContext());
    WorkflowTransitionDto transition = workflowEngine.findTransition(workflow, request.getTransitionCode(), request.getCurrentState());

    if (!workflowEngine.evaluateGuards(transition.getGuards(), guardContext)) {
      throw new IllegalArgumentException("Transition guards not satisfied for: " + transition.getCode());
    }

    String fromState = transition.getFromState();
    String toState = transition.getToState();

    // In this PoC implementation, we only emit a transition result.
    WorkflowTransitionResultDto result = new WorkflowTransitionResultDto();
    result.setRecordId(recordId);
    result.setModelCode(model);
    result.setTransitionCode(transition.getCode());
    result.setFromState(fromState);
    result.setToState(toState);

    return result;
  }

  public List<WorkflowHistoryDto> getHistory(String model, UUID recordId) {
    // Placeholder history for runtime preview; persistent history storage is not implemented yet.
    WorkflowMetadataDto workflow = resolveWorkflow(model);
    WorkflowStateDto initialState = workflowEngine.determineInitialState(workflow);

    WorkflowHistoryDto history = new WorkflowHistoryDto();
    history.setId(UUID.randomUUID());
    history.setModelCode(model);
    history.setRecordId(recordId);
    history.setTransitionCode("INIT");
    history.setFromState(null);
    history.setToState(initialState.getCode());
    history.setOccurredAt(LocalDateTime.now());

    return List.of(history);
  }

  private WorkflowMetadataDto resolveWorkflow(String model) {
    String workflowCode = model + "_workflow";
    return metadataRegistry.findWorkflow(workflowCode);
  }

  private Map<String, Object> buildTransitionContext(String model, UUID recordId) {
    Map<String, Object> context = new HashMap<>();
    context.put("model", model);
    context.put("recordId", recordId);
    return context;
  }

  private Map<String, Object> mergeContexts(Map<String, Object> baseContext, Map<String, Object> additionalContext) {
    Map<String, Object> merged = new HashMap<>(baseContext);
    if (additionalContext != null) {
      merged.putAll(additionalContext);
    }
    return merged;
  }

  private WorkflowTransitionResponseDto toResponseDto(WorkflowTransitionDto transition) {
    return new WorkflowTransitionResponseDto(
        transition.getCode(),
        transition.getLabel(),
        transition.getFromState(),
        transition.getToState()
    );
  }
}
