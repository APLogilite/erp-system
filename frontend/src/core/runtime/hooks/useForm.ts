/**
 * useForm() — Central React hook for the two-request runtime form pattern.
 *
 * Request 1: Form definition (cached 5 min, changed rarely)
 *   GET /api/v1/runtime/forms/{formCode}/definition
 *
 * Request 2: Record data (always fresh)
 *   GET /api/v1/runtime/forms/{formCode}/records (list)
 *   GET /api/v1/runtime/forms/{formCode}/records/{id} (single)
 *
 * Mutations auto-invalidate the data query on success.
 *
 * Usage (list mode):
 *   const { formDefinition, records, isLoading, createRecord } = useForm('sales_order');
 *
 * Usage (single record mode):
 *   const { formDefinition, record, breadcrumb } = useForm('sales_order', { recordId: 'uuid' });
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useMemo } from 'react';

import {
  fetchFormDefinition,
  fetchRecords,
  fetchRecord,
  createRecord as apiCreateRecord,
  updateRecord as apiUpdateRecord,
  deleteRecord as apiDeleteRecord,
} from '../api/runtimeApi';

import type {
  UseFormOptions,
  UseFormResult,
  ListRecordsResponse,
  SingleRecordResponse,
} from './useForm.types';

const DEF_STALE_TIME = 5 * 60 * 1000; // 5 minutes
const DEF_GC_TIME = 30 * 60 * 1000; // 30 minutes in cache

/**
 * Core useForm hook — abstracts the two-request pattern.
 */
export function useForm(formCode: string, options: UseFormOptions = {}): UseFormResult {
  const queryClient = useQueryClient();

  const { recordId, page = 0, pageSize = 20, sortField, sortDir } = options;

  const isSingleRecord = !!recordId;

  // ---------------------------------------------------------------
  // Request 1: Form Definition (cached)
  // ---------------------------------------------------------------

  const definitionQuery = useQuery({
    queryKey: ['form-definition', formCode],
    queryFn: () => fetchFormDefinition(formCode),
    staleTime: DEF_STALE_TIME,
    gcTime: DEF_GC_TIME,
    enabled: !!formCode,
  });

  // ---------------------------------------------------------------
  // Request 2a: List Records (fresh, only when NOT in single-record mode)
  // ---------------------------------------------------------------

  const listQuery = useQuery<ListRecordsResponse>({
    queryKey: ['form-data', formCode, 'list', page, pageSize, sortField, sortDir],
    queryFn: () => fetchRecords(formCode, page, pageSize, sortField, sortDir),
    staleTime: 0,
    enabled: !!formCode && !isSingleRecord,
  });

  // ---------------------------------------------------------------
  // Request 2b: Single Record (fresh, only when in single-record mode)
  // ---------------------------------------------------------------

  const singleQuery = useQuery<SingleRecordResponse>({
    queryKey: ['form-data', formCode, 'record', recordId],
    queryFn: () => fetchRecord(formCode, recordId!),
    staleTime: 0,
    enabled: !!formCode && isSingleRecord,
  });

  // ---------------------------------------------------------------
  // Mutations
  // ---------------------------------------------------------------

  const invalidateData = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['form-data', formCode] });
  }, [formCode, queryClient]);

  const createMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => apiCreateRecord(formCode, data),
    onSuccess: () => invalidateData(),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Record<string, unknown> }) =>
      apiUpdateRecord(formCode, id, data),
    onSuccess: () => invalidateData(),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => apiDeleteRecord(formCode, id),
    onSuccess: () => invalidateData(),
  });

  // ---------------------------------------------------------------
  // Derived state
  // ---------------------------------------------------------------

  const isLoadingDefinition = definitionQuery.isLoading;
  const definitionError = definitionQuery.error as Error | null;

  const isLoadingData = isSingleRecord ? singleQuery.isLoading : listQuery.isLoading;
  const dataError = isSingleRecord
    ? (singleQuery.error as Error | null)
    : (listQuery.error as Error | null);

  const isLoading = isLoadingDefinition || isLoadingData;
  const error = definitionError ?? dataError;

  // Current data
  const listData = listQuery.data;
  const singleData = singleQuery.data;

  // ---------------------------------------------------------------
  // Result assembly
  // ---------------------------------------------------------------

  const result: UseFormResult = useMemo(() => {
    const def = definitionQuery.data;

    const base = {
      formDefinition: def,
      isLoadingDefinition,
      definitionError,
      isLoadingData,
      dataError,
      isLoading,
      error,
      createRecord: async (d: Record<string, unknown>) => createMutation.mutateAsync(d),
      updateRecord: async (id: string, d: Record<string, unknown>) =>
        updateMutation.mutateAsync({ id, data: d }),
      deleteRecord: async (id: string) => deleteMutation.mutateAsync(id),
      refreshData: () => queryClient.invalidateQueries({ queryKey: ['form-data', formCode] }),
      invalidateDefinition: () =>
        queryClient.invalidateQueries({ queryKey: ['form-definition', formCode] }),
    };

    if (isSingleRecord && singleData) {
      return {
        ...base,
        records: undefined,
        record: singleData.record,
        subFormRecords: singleData.subFormRecords,
        breadcrumb: singleData.breadcrumb,
        parent: singleData.parent,
        totalRecords: 0,
        currentPage: 0,
        setPage: () => {},
      };
    }

    if (!isSingleRecord && listData) {
      return {
        ...base,
        records: listData.records,
        record: undefined,
        subFormRecords: undefined,
        breadcrumb: undefined,
        parent: undefined,
        totalRecords: listData.total,
        currentPage: listData.page,
        setPage: () => {
          queryClient.invalidateQueries({ queryKey: ['form-data', formCode] });
        },
      };
    }

    // Data not yet loaded
    return {
      ...base,
      records: undefined,
      record: undefined,
      subFormRecords: undefined,
      breadcrumb: undefined,
      parent: undefined,
      totalRecords: 0,
      currentPage: 0,
      setPage: () => {},
    };
  }, [
    definitionQuery.data,
    isLoadingDefinition,
    definitionError,
    isLoadingData,
    dataError,
    isLoading,
    error,
    isSingleRecord,
    singleData,
    listData,
    formCode,
    createMutation,
    updateMutation,
    deleteMutation,
    queryClient,
  ]);

  return result;
}
