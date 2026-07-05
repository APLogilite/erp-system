import { useRuntimeContext } from '@/runtime/context/RuntimeContext';

export function useRuntimeWorkflow() {
  const runtime = useRuntimeContext();
  return {
    workflow: runtime.workflow,
    currentView: runtime.currentView,
  };
}
