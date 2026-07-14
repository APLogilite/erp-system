/**
 * API client functions for the runtime form system.
 *
 * Uses the existing axios-based apiClient (with JWT interceptor).
 * All functions unwrap the ApiResponse envelope and return the data directly.
 */

import type {
  ApiResponse,
  FormDefinition,
  ListRecordsResponse,
  RecordData,
  RecordEntry,
  SingleRecordResponse,
} from '../hooks/useForm.types';

import { apiClient } from '@/core/api/client';

function unwrap<T>(response: { data: ApiResponse<T> }): T {
  if (!response.data.success) {
    throw new Error(response.data.message ?? `API error: ${response.data.errorCode ?? 'UNKNOWN'}`);
  }
  return response.data.data;
}

// ---- Form Definition ----

/**
 * Fetches the cached form definition bundle.
 * GET /api/v1/runtime/forms/{formCode}/definition
 */
export async function fetchFormDefinition(formCode: string): Promise<FormDefinition> {
  const response = await apiClient.get<ApiResponse<FormDefinition>>(
    `/runtime/forms/${encodeURIComponent(formCode)}/definition`
  );
  return unwrap(response);
}

// ---- Records ----

/**
 * Lists accessible forms for the current user.
 * GET /api/v1/runtime/forms
 */
export async function fetchAccessibleForms(): Promise<FormDefinition[]> {
  const response = await apiClient.get<ApiResponse<FormDefinition[]>>('/runtime/forms');
  return unwrap(response);
}

/**
 * Fetches paginated records for a form.
 * GET /api/v1/runtime/forms/{formCode}/records
 */
export async function fetchRecords(
  formCode: string,
  page: number = 0,
  pageSize: number = 20,
  sortField?: string,
  sortDir?: string
): Promise<ListRecordsResponse> {
  const params: Record<string, string | number> = { page, size: pageSize };
  if (sortField) params.sortField = sortField;
  if (sortDir) params.sortDir = sortDir;

  const response = await apiClient.get<ApiResponse<ListRecordsResponse>>(
    `/runtime/forms/${encodeURIComponent(formCode)}/records`,
    { params }
  );
  return unwrap(response);
}

/**
 * Fetches a single record with sub-form children and breadcrumb context.
 * GET /api/v1/runtime/forms/{formCode}/records/{id}
 */
export async function fetchRecord(
  formCode: string,
  recordId: string
): Promise<SingleRecordResponse> {
  const response = await apiClient.get<ApiResponse<SingleRecordResponse>>(
    `/runtime/forms/${encodeURIComponent(formCode)}/records/${recordId}`
  );
  return unwrap(response);
}

/**
 * Creates a new record.
 * POST /api/v1/runtime/forms/{formCode}/records
 */
export async function createRecord(formCode: string, data: RecordData): Promise<RecordEntry> {
  const response = await apiClient.post<ApiResponse<RecordEntry>>(
    `/runtime/forms/${encodeURIComponent(formCode)}/records`,
    data
  );
  return unwrap(response);
}

/**
 * Updates an existing record.
 * PUT /api/v1/runtime/forms/{formCode}/records/{id}
 */
export async function updateRecord(
  formCode: string,
  recordId: string,
  data: RecordData
): Promise<RecordEntry> {
  const response = await apiClient.put<ApiResponse<RecordEntry>>(
    `/runtime/forms/${encodeURIComponent(formCode)}/records/${recordId}`,
    data
  );
  return unwrap(response);
}

/**
 * Soft-deletes a record.
 * DELETE /api/v1/runtime/forms/{formCode}/records/{id}
 */
export async function deleteRecord(formCode: string, recordId: string): Promise<void> {
  await apiClient.delete(`/runtime/forms/${encodeURIComponent(formCode)}/records/${recordId}`);
}

// ---- Menu API (PRD-004) ----

/** A single menu tree node returned from the menu API. */
export interface MenuTreeNode {
  id: string;
  name: string;
  type: 'group' | 'window';
  windowId?: string;
  windowName?: string;
  icon?: string;
  seqNo: number;
  children: MenuTreeNode[];
}

/**
 * Fetches the hierarchical menu tree for the current user.
 * GET /api/v1/runtime/menu
 */
export async function fetchMenu(): Promise<MenuTreeNode[]> {
  const response = await apiClient.get<ApiResponse<MenuTreeNode[]>>('/runtime/menu');
  return unwrap(response);
}
