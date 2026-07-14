import { useQuery } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

/**
 * Represents an accessible window returned from the new PRD-004 Window schema.
 * Replaces the old PRD-001 AccessibleForm which referenced the old form metadata.
 */
export interface AccessibleForm {
  windowId: string;
  windowName: string;
  windowLabel: string;
  tableName: string;
  tableLabel: string;
}

/**
 * Fetches the list of windows the current user has role-based access to.
 * Uses the new PRD-004 /runtime/windows/accessible endpoint which queries
 * the sys_window / sys_window_access tables.
 */
export function useAccessibleForms() {
  return useQuery<AccessibleForm[]>({
    queryKey: ['runtime', 'accessible-windows'],
    queryFn: async () => {
      const res = await apiClient.get('/runtime/windows/accessible');
      return res.data.data ?? [];
    },
    staleTime: Infinity,
    gcTime: Infinity,
  });
}
