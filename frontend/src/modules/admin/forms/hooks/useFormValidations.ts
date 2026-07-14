import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

export interface FieldValidation {
  id: string;
  fieldId: string;
  type: string;
  value: string;
  message: string;
  position: number;
}

export function useFormValidations(formId: string | undefined, fieldId: string | undefined) {
  return useQuery<FieldValidation[]>({
    queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'validations'],
    queryFn: async () => {
      const res = await apiClient.get(`/metadata/forms/${formId}/fields/${fieldId}/validations`);
      return res.data.data;
    },
    enabled: !!formId && !!fieldId,
  });
}

export function useAddValidation(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ fieldId, data }: { fieldId: string; data: Record<string, unknown> }) => {
      const res = await apiClient.post(
        `/metadata/forms/${formId}/fields/${fieldId}/validations`,
        data
      );
      return res.data.data;
    },
    onSuccess: (_, vars) => {
      qc.invalidateQueries({
        queryKey: ['admin', 'forms', formId, 'fields', vars.fieldId, 'validations'],
      });
    },
  });
}

export function useUpdateValidation(formId: string, fieldId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ valId, data }: { valId: string; data: Record<string, unknown> }) => {
      const res = await apiClient.put(
        `/metadata/forms/${formId}/fields/${fieldId}/validations/${valId}`,
        data
      );
      return res.data.data;
    },
    onSuccess: () => {
      qc.invalidateQueries({
        queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'validations'],
      });
    },
  });
}

export function useDeleteValidation(formId: string, fieldId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (valId: string) => {
      await apiClient.delete(`/metadata/forms/${formId}/fields/${fieldId}/validations/${valId}`);
    },
    onSuccess: () => {
      qc.invalidateQueries({
        queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'validations'],
      });
    },
  });
}
