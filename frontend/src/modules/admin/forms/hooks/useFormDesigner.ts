import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

import type { AvailableTable, FormDefinition } from '../types';

const FORMS_API = '/metadata/forms';

export function useFormList() {
  return useQuery<FormDefinition[]>({
    queryKey: ['admin', 'forms'],
    queryFn: async () => {
      const res = await apiClient.get(FORMS_API);
      return res.data.data;
    },
  });
}

export function useForm(id: string | undefined) {
  return useQuery<FormDefinition>({
    queryKey: ['admin', 'forms', id],
    queryFn: async () => {
      const res = await apiClient.get(`${FORMS_API}/${id}`);
      return res.data.data;
    },
    enabled: !!id,
  });
}

export function useAvailableTables() {
  return useQuery<AvailableTable[]>({
    queryKey: ['admin', 'forms', 'available-tables'],
    queryFn: async () => {
      const res = await apiClient.get(`${FORMS_API}/available-tables`);
      return res.data.data;
    },
  });
}

export function useCreateForm() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (data: Record<string, unknown>) => {
      const res = await apiClient.post(FORMS_API, data);
      return res.data.data as FormDefinition;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms'] }),
  });
}

export function useUpdateForm() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: Record<string, unknown> }) => {
      const res = await apiClient.put(`${FORMS_API}/${id}`, data);
      return res.data.data;
    },
    onSuccess: (_, vars) => {
      qc.invalidateQueries({ queryKey: ['admin', 'forms'] });
      qc.invalidateQueries({ queryKey: ['admin', 'forms', vars.id] });
    },
  });
}

export function useDeleteForm() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await apiClient.delete(`${FORMS_API}/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms'] }),
  });
}

export function useCloneForm() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const res = await apiClient.post(`${FORMS_API}/${id}/clone`);
      return res.data.data as FormDefinition;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin', 'forms'] }),
  });
}
