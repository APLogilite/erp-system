import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import type {
  CreateTablePayload,
  TableDefinition,
  UpdateTablePayload,
  VersionHistoryEntry,
} from '../types';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

export function useTableList(search?: string) {
  return useQuery<TableDefinition[]>({
    queryKey: ['admin', 'tables', { search }],
    queryFn: async () => {
      const params: Record<string, string | number> = { size: 50 };
      if (search) params.search = search;
      const res = await apiClient.get(ENDPOINTS.metadata.tables.base, { params });
      return res.data.data;
    },
  });
}

export function useTable(id: string | undefined) {
  return useQuery<TableDefinition>({
    queryKey: ['admin', 'tables', id],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.metadata.tables.detail(id!));
      return res.data.data;
    },
    enabled: !!id,
  });
}

export function useTableHistory(tableId: string | undefined) {
  return useQuery<VersionHistoryEntry[]>({
    queryKey: ['admin', 'tables', tableId, 'history'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.metadata.tables.history(tableId!));
      return res.data.data;
    },
    enabled: !!tableId,
  });
}

export function useCreateTable() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (payload: CreateTablePayload) => {
      const res = await apiClient.post(ENDPOINTS.metadata.tables.base, payload);
      return res.data.data as TableDefinition;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables'] });
    },
  });
}

export function useUpdateTable() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, payload }: { id: string; payload: UpdateTablePayload }) => {
      const res = await apiClient.put(ENDPOINTS.metadata.tables.detail(id), payload);
      return res.data.data as TableDefinition;
    },
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables'] });
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables', variables.id] });
    },
  });
}

export function useDeleteTable() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await apiClient.delete(ENDPOINTS.metadata.tables.detail(id));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin', 'tables'] });
    },
  });
}
