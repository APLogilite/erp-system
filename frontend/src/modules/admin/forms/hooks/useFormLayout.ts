import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

import type { LayoutSection } from '../types';

export function useFormLayout(formId: string | undefined) {
  return useQuery<LayoutSection[]>({
    queryKey: ['admin', 'forms', formId, 'layout'],
    queryFn: async () => {
      const res = await apiClient.get(`/metadata/forms/${formId}/layout`);
      return res.data.data;
    },
    enabled: !!formId,
  });
}

export function useAddSection(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (data: Record<string, unknown>) => {
      const res = await apiClient.post(`/metadata/forms/${formId}/layout/sections`, data);
      return res.data.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'layout'] }),
  });
}

export function useUpdateSection(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({
      sectionId,
      data,
    }: {
      sectionId: string;
      data: Record<string, unknown>;
    }) => {
      const res = await apiClient.put(
        `/metadata/forms/${formId}/layout/sections/${sectionId}`,
        data
      );
      return res.data.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'layout'] }),
  });
}

export function useDeleteSection(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (sectionId: string) => {
      await apiClient.delete(`/metadata/forms/${formId}/layout/sections/${sectionId}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'layout'] }),
  });
}
