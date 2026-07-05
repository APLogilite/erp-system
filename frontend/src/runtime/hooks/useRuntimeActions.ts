import { useRuntimeContext } from '@/runtime/context/RuntimeContext';

export function useRuntimeActions() {
  const runtime = useRuntimeContext();
  return {
    actions: runtime.actions,
    record: runtime.record,
  };
}
