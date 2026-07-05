import { Suspense, useEffect, useState, type ComponentType } from 'react';

import { useRegistry } from '@/core/registry';
import { useRuntimeContext } from '@/runtime/context/RuntimeContext';

export function ViewRenderer() {
  const runtime = useRuntimeContext();
  const registry = useRegistry();

  const ComponentPromise = registry.views.resolveView(runtime.currentView.viewType);

  return (
    <Suspense fallback={<div>Loading view...</div>}>
      <ViewRendererContent componentPromise={ComponentPromise} />
    </Suspense>
  );
}

function ViewRendererContent({
  componentPromise,
}: {
  componentPromise: Promise<ComponentType<Record<string, unknown>>>;
}) {
  const [Component, setComponent] = useState<ComponentType<Record<string, unknown>> | null>(null);

  useEffect(() => {
    let active = true;
    componentPromise.then((resolved) => {
      if (active) {
        setComponent(() => resolved);
      }
    });
    return () => {
      active = false;
    };
  }, [componentPromise]);

  if (!Component) {
    return <div>Resolving view...</div>;
  }

  return <Component />;
}
