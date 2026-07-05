import { useState, useEffect, useMemo, type ComponentType } from 'react';

import type { WorkflowTransition } from '@/core/metadata/schema/workflow/WorkflowDefinition';
import { useRegistry } from '@/core/registry';
import { useRuntimeContext } from '@/runtime/context/RuntimeContext';
import { evaluateExpression } from '@/runtime/expression/expressionEngine';

export function WorkflowRenderer() {
  const runtime = useRuntimeContext();
  const workflow = runtime.workflow;
  const registry = useRegistry();

  const currentState = workflow?.states.find((state) => state.initial) ?? workflow?.states[0];

  const visibleTransitions = useMemo<WorkflowTransition[]>(() => {
    if (!workflow) {
      return [];
    }

    const context = runtime.record ?? {};
    return workflow.transitions.filter((transition) =>
      evaluateExpression(transition.guardExpression, context)
    );
  }, [workflow, runtime.record]);

  if (!workflow) {
    return null;
  }

  return (
    <div>
      <div>Current Workflow: {workflow.name}</div>
      <div>State: {currentState?.name ?? 'unknown'}</div>
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        {visibleTransitions.map((transition) => (
          <WorkflowTransitionButton
            key={transition.code}
            transition={transition}
            registry={registry}
            record={runtime.record}
          />
        ))}
      </div>
    </div>
  );
}

interface WorkflowTransitionButtonProps {
  transition: WorkflowTransition;
  registry: ReturnType<typeof useRegistry>;
  record?: Record<string, unknown>;
}

function WorkflowTransitionButton({ transition, registry }: WorkflowTransitionButtonProps) {
  const [component, setComponent] = useState<ComponentType<Record<string, unknown>> | null>(null);

  useEffect(() => {
    let active = true;
    registry.workflows.resolveWorkflow('CUSTOM').then((resolved) => {
      if (active) {
        setComponent(() => resolved);
      }
    });
    return () => {
      active = false;
    };
  }, [registry.workflows]);

  if (!component) {
    return <button disabled>Loading workflow...</button>;
  }

  const onClick = () => {
    // Future: dispatch workflow transition event
  };

  return <button onClick={onClick}>{transition.label ?? transition.code}</button>;
}
