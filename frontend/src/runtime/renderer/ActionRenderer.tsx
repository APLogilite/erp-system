import { useState, useEffect } from 'react';

import { ActionDefinition } from '@/core/metadata/schema/action/ActionDefinition';
import { useRegistry } from '@/core/registry';
import type { ActionHandler, ActionExecutor } from '@/core/registry/registry.types';
import { useRuntimeContext } from '@/runtime/context/RuntimeContext';
import { useExpression } from '@/runtime/expression/useExpression';

interface ActionRendererProps {
  action: ActionDefinition;
}

export function ActionRenderer({ action }: ActionRendererProps) {
  const runtime = useRuntimeContext();
  const registry = useRegistry();
  const isVisible = useExpression(action.visibleWhen, runtime.record ?? {});
  const isEnabled = useExpression(action.enabledWhen, runtime.record ?? {});

  const [handler, setHandler] = useState<ActionHandler | null>(null);

  useEffect(() => {
    let active = true;
    registry.actions.resolveAction(action.actionType).then((resolved) => {
      if (active) {
        setHandler(() => resolved);
      }
    });
    return () => {
      active = false;
    };
  }, [action.actionType, registry.actions]);

  if (!isVisible) {
    return null;
  }

  if (!handler) {
    return <button disabled>Loading action...</button>;
  }

  const onClick = async () => {
    if (handler && typeof handler === 'object' && 'run' in handler) {
      await handler.run({ action, record: runtime.record });
      return;
    }

    if (typeof handler === 'function') {
      await (handler as ActionExecutor)({ action, record: runtime.record });
      return;
    }
  };

  const label = action.name || action.code;

  return (
    <button disabled={!isEnabled} onClick={onClick}>
      {label}
    </button>
  );
}
