import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

import type { FormField } from '../types';

export function useFormFields(formId: string | undefined) {
  return useQuery<FormField[]>({
    queryKey: ['admin', 'forms', formId, 'fields'],
    queryFn: async () => {
      const res = await apiClient.get(`/metadata/forms/${formId}/fields`);
      return res.data.data;
    },
    enabled: !!formId,
  });
}

export function useUpdateField(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ fieldId, data }: { fieldId: string; data: Record<string, unknown> }) => {
      const res = await apiClient.put(`/metadata/forms/${formId}/fields/${fieldId}`, data);
      return res.data.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields'] }),
  });
}

export function useAddField(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (data: Record<string, unknown>) => {
      const res = await apiClient.post(`/metadata/forms/${formId}/fields`, data);
      return res.data.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields'] }),
  });
}

export function useDeleteField(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (fieldId: string) => {
      await apiClient.delete(`/metadata/forms/${formId}/fields/${fieldId}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields'] }),
  });
}

export function useReorderFields(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (fieldIds: string[]) => {
      await apiClient.put(`/metadata/forms/${formId}/fields/reorder`, { fieldIds });
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields'] }),
  });
}
