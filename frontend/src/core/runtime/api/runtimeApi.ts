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

// ---- Window API (PRD-004) ----

/** Column type metadata within a window field. */
export interface ColumnInfo {
  code: string;
  label: string;
  type: string;
  required: boolean;
  maxLength?: number;
  precision?: number;
  scale?: number;
  relationTable?: string;
  enumOptions?: string;
}

/** A field definition within a window tab. */
export interface WindowFieldDefinition {
  id: string;
  seqNo: number;
  isSameLine: boolean;
  numLines: number;
  columnWidth: number;
  isDisplayed: boolean;
  isReadonly: boolean;
  isMandatory: boolean;
  displayLogic: string | null;
  readonlyLogic: string | null;
  defaultValue: string | null;
  labelOverride: string | null;
  label: string; // Pre-resolved: labelOverride ?? column.label
  column: ColumnInfo;
}

/** Table metadata within a tab. */
export interface TabTableInfo {
  id: string;
  name: string;
  label: string;
}

/** A tab definition within a window. */
export interface WindowTabDefinition {
  id: string;
  name: string;
  seqNo: number;
  isSingleRow: boolean;
  whereClause: string | null;
  parentColumn: string | null;
  table: TabTableInfo;
  fields: WindowFieldDefinition[];
}

/** Window metadata. */
export interface WindowInfo {
  id: string;
  name: string;
  tableId: string;
  description: string | null;
}

/** Full window definition bundle. */
export interface WindowDefinition {
  window: WindowInfo;
  tabs: WindowTabDefinition[];
}

/**
 * Fetches the window definition for a given window name.
 * GET /api/v1/runtime/windows/{windowName}/definition
 */
export async function fetchWindowDefinition(windowName: string): Promise<WindowDefinition> {
  const response = await apiClient.get<ApiResponse<WindowDefinition>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/definition`
  );
  return unwrap(response);
}

/**
 * Fetches paginated records for a window's main tab.
 * GET /api/v1/runtime/windows/{windowName}/records
 */
export async function fetchWindowRecords(
  windowName: string,
  page: number = 0,
  pageSize: number = 20,
  sortField?: string,
  sortDir?: string
): Promise<Record<string, unknown>> {
  const params: Record<string, string | number> = { page, size: pageSize };
  if (sortField) params.sortField = sortField;
  if (sortDir) params.sortDir = sortDir;

  const response = await apiClient.get<ApiResponse<Record<string, unknown>>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/records`,
    { params }
  );
  return unwrap(response);
}

/**
 * Fetches a single record with child tab records.
 * GET /api/v1/runtime/windows/{windowName}/records/{id}
 */
export async function fetchWindowRecord(
  windowName: string,
  recordId: string
): Promise<Record<string, unknown>> {
  const response = await apiClient.get<ApiResponse<Record<string, unknown>>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/records/${recordId}`
  );
  return unwrap(response);
}

/**
 * Creates a new record in a window.
 * Optionally specify tabId + parentRecordId to auto-set the parent FK
 * for child records created from drill-down context.
 * POST /api/v1/runtime/windows/{windowName}/records?tabId=...&parentRecordId=...
 */
export async function createWindowRecord(
  windowName: string,
  data: Record<string, unknown>,
  tabId?: string,
  parentRecordId?: string
): Promise<Record<string, unknown>> {
  const params: Record<string, string> = {};
  if (tabId) params.tabId = tabId;
  if (parentRecordId) params.parentRecordId = parentRecordId;
  const response = await apiClient.post<ApiResponse<Record<string, unknown>>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/records`,
    data,
    { params }
  );
  return unwrap(response);
}

/**
 * Updates an existing record in a window.
 * Optionally specify tabId to update a record in a child tab's table.
 * PUT /api/v1/runtime/windows/{windowName}/records/{id}?tabId={tabId}
 */
export async function updateWindowRecord(
  windowName: string,
  recordId: string,
  data: Record<string, unknown>,
  tabId?: string
): Promise<Record<string, unknown>> {
  const params: Record<string, string> = {};
  if (tabId) params.tabId = tabId;
  const response = await apiClient.put<ApiResponse<Record<string, unknown>>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/records/${recordId}`,
    data,
    { params }
  );
  return unwrap(response);
}

/**
 * Soft-deletes a record in a window.
 * DELETE /api/v1/runtime/windows/{windowName}/records/{id}
 */
export async function deleteWindowRecord(windowName: string, recordId: string): Promise<void> {
  await apiClient.delete(`/runtime/windows/${encodeURIComponent(windowName)}/records/${recordId}`);
}

// ---- Tab Record API (drill-down) ----

/**
 * Fetches a record from a specific tab's table (not just the main tab).
 * Used for drill-down navigation through the tab hierarchy.
 * GET /api/v1/runtime/windows/{windowName}/tabs/{tabId}/records/{recordId}?childTabs=...
 */
export async function fetchTabRecord(
  windowName: string,
  tabId: string,
  recordId: string,
  childTabIds?: string[]
): Promise<Record<string, unknown>> {
  const params: Record<string, string> = {};
  if (childTabIds && childTabIds.length > 0) {
    params.childTabs = childTabIds.join(',');
  }
  const response = await apiClient.get<ApiResponse<Record<string, unknown>>>(
    `/runtime/windows/${encodeURIComponent(windowName)}/tabs/${encodeURIComponent(tabId)}/records/${encodeURIComponent(recordId)}`,
    { params }
  );
  return unwrap(response);
}

// ---- Lookup API ----

/**
 * Fetches records from a table for use in dropdown/autocomplete.
 * Each record includes a _display field with the human-readable label.
 * Optionally pass parentRecordId to apply server-side filter_where_clause.
 * GET /api/v1/runtime/windows/lookup/{tableName}?parentRecordId=...
 */
export async function fetchLookupRecords(
  tableName: string,
  parentRecordId?: string
): Promise<Record<string, unknown>[]> {
  const params: Record<string, string> = {};
  if (parentRecordId) params.parentRecordId = parentRecordId;
  const response = await apiClient.get<ApiResponse<Record<string, unknown>[]>>(
    `/runtime/windows/lookup/${encodeURIComponent(tableName)}`,
    { params }
  );
  return unwrap(response);
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
