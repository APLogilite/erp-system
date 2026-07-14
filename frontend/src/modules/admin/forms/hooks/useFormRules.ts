import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';

export interface FieldRule {
  id: string;
  fieldId: string;
  conditionField: string;
  conditionOperator: string;
  conditionValue: string;
  action: string;
  logicGroup: number;
  position: number;
}

export function useFormRules(formId: string | undefined, fieldId: string | undefined) {
  return useQuery<FieldRule[]>({
    queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'rules'],
    queryFn: async () => {
      const res = await apiClient.get(`/metadata/forms/${formId}/fields/${fieldId}/rules`);
      return res.data.data;
    },
    enabled: !!formId && !!fieldId,
  });
}

export function useAddRule(formId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ fieldId, data }: { fieldId: string; data: Record<string, unknown> }) => {
      const res = await apiClient.post(`/metadata/forms/${formId}/fields/${fieldId}/rules`, data);
      return res.data.data;
    },
    onSuccess: (_, vars) => {
      qc.invalidateQueries({
        queryKey: ['admin', 'forms', formId, 'fields', vars.fieldId, 'rules'],
      });
    },
  });
}

export function useUpdateRule(formId: string, fieldId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ ruleId, data }: { ruleId: string; data: Record<string, unknown> }) => {
      const res = await apiClient.put(
        `/metadata/forms/${formId}/fields/${fieldId}/rules/${ruleId}`,
        data
      );
      return res.data.data;
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'rules'] });
    },
  });
}

export function useDeleteRule(formId: string, fieldId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (ruleId: string) => {
      await apiClient.delete(`/metadata/forms/${formId}/fields/${fieldId}/rules/${ruleId}`);
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin', 'forms', formId, 'fields', fieldId, 'rules'] });
    },
  });
}
