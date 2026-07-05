import { useState, useEffect, type ComponentType } from 'react';

import { LayoutDefinition } from '@/core/metadata/schema/layout/LayoutDefinition';
import { useRegistry } from '@/core/registry';
import { useRuntimeContext } from '@/runtime/context/RuntimeContext';
import { useExpression } from '@/runtime/expression/useExpression';

interface LayoutRendererProps {
  layout: LayoutDefinition;
}

export function LayoutRenderer({ layout }: LayoutRendererProps) {
  const runtime = useRuntimeContext();
  const registry = useRegistry();
  const isVisible = useExpression(layout.config?.visibleWhen, runtime.record ?? {});

  const [LayoutComponent, setLayoutComponent] = useState<ComponentType<
    Record<string, unknown>
  > | null>(null);

  useEffect(() => {
    let active = true;
    registry.layouts.resolveLayout(layout.type).then((component) => {
      if (active) {
        setLayoutComponent(() => component);
      }
    });
    return () => {
      active = false;
    };
  }, [layout.type, registry.layouts]);

  if (!isVisible) {
    return null;
  }

  if (!LayoutComponent) {
    return <div>Resolving layout...</div>;
  }

  return (
    <LayoutComponent config={layout.config}>
      {layout.children?.map((child, index) => (
        <LayoutRenderer key={`${layout.type}-${index}`} layout={child} />
      ))}
    </LayoutComponent>
  );
}
