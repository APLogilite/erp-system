package com.erp.core.workflow.service;

import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.dto.WorkflowTransitionDto;
import com.erp.core.metadata.dto.WorkflowStateDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkflowEngine {

  private final WorkflowGuardService guardService;

  public WorkflowEngine(WorkflowGuardService guardService) {
    this.guardService = guardService;
  }

  public List<WorkflowTransitionDto> getAvailableTransitions(
      WorkflowMetadataDto workflow,
      String currentState,
      Map<String, Object> context) {

    String effectiveState = determineCurrentState(workflow, currentState);

    return workflow.getTransitions().stream()
        .filter(transition -> effectiveState.equals(transition.getFromState()))
        .filter(transition -> guardService.evaluateGuards(transition.getGuards(), context))
        .collect(Collectors.toList());
  }

  public WorkflowTransitionDto findTransition(
      WorkflowMetadataDto workflow,
      String transitionCode,
      String currentState) {

    String effectiveState = determineCurrentState(workflow, currentState);

    return workflow.getTransitions().stream()
        .filter(transition -> transitionCode.equals(transition.getCode()))
        .filter(transition -> effectiveState.equals(transition.getFromState()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Transition not available for current state: " + transitionCode));
  }

  public boolean evaluateGuards(Map<String, Object> guards, Map<String, Object> context) {
    return guardService.evaluateGuards(guards, context);
  }

  public WorkflowStateDto determineInitialState(WorkflowMetadataDto workflow) {
    return workflow.getStates().stream()
        .filter(WorkflowStateDto::isInitial)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Workflow has no initial state."));
  }

  private String determineCurrentState(WorkflowMetadataDto workflow, String currentState) {
    if (currentState != null && !currentState.isBlank()) {
      return currentState;
    }
    return determineInitialState(workflow).getCode();
  }
}
