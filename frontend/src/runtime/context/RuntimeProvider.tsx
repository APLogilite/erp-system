import { ReactNode, useMemo } from 'react';

import type { RuntimeRenderOptions, RuntimeContextValue } from './runtime.types';
import { RuntimeProvider as ContextProvider } from './RuntimeContext';

export function RuntimeProvider({
  options,
  children,
}: {
  options: RuntimeRenderOptions;
  children: ReactNode;
}) {
  const contextValue = useMemo<RuntimeContextValue>(() => {
    const view = options.metadataBundle.views.find((v) => v.code === options.viewCode);
    if (!view) {
      throw new Error(`View not found: ${options.viewCode}`);
    }

    return {
      metadataBundle: options.metadataBundle,
      currentView: view,
      currentLayout: view.layout,
      record: options.record,
      mode: options.mode ?? 'VIEW',
      loading: false,
      permissions: options.permissions ?? [],
      actions: options.metadataBundle.actions ?? [],
      workflow: options.metadataBundle.workflow,
    };
  }, [options]);

  return <ContextProvider value={contextValue}>{children}</ContextProvider>;
}
