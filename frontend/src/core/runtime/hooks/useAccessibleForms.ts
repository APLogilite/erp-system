import { useQuery } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

export interface AccessibleForm {
  formCode: string;
  formLabel: string;
  modelName: string;
  modelLabel?: string;
  formId?: string;
}

export function useAccessibleForms() {
  return useQuery<AccessibleForm[]>({
    queryKey: ['runtime', 'accessible-forms'],
    queryFn: async () => {
      const res = await apiClient.get('/runtime/forms');
      return res.data.data ?? [];
    },
    staleTime: Infinity,
    gcTime: Infinity,
  });
}
