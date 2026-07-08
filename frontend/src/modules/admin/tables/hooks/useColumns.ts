import { useMutation, useQueryClient } from '@tanstack/react-query';

import type { ColumnReorderPayload, CreateColumnPayload, UpdateColumnPayload } from '../types';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

export function useAddColumn(tableId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (payload: CreateColumnPayload) => {
      const res = await apiClient.post(ENDPOINTS.metadata.tables.columns(tableId), payload);
      return res.data.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables', tableId] });
    },
  });
}

export function useUpdateColumn(tableId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ colId, payload }: { colId: string; payload: UpdateColumnPayload }) => {
      const res = await apiClient.put(ENDPOINTS.metadata.tables.column(tableId, colId), payload);
      return res.data.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables', tableId] });
    },
  });
}

export function useDeleteColumn(tableId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (colId: string) => {
      await apiClient.delete(ENDPOINTS.metadata.tables.column(tableId, colId));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables', tableId] });
    },
  });
}

export function useReorderColumns(tableId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (payload: ColumnReorderPayload) => {
      const res = await apiClient.put(ENDPOINTS.metadata.tables.reorder(tableId), payload);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables', tableId] });
    },
  });
}
