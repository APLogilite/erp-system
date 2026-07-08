/**
 * TypeScript types for the useForm() hook and runtime form system.
 *
 * Aligns with the backend FormDefinitionBundleResponse, record endpoints,
 * and the PRD-001 two-request loading pattern.
 */

// ---- Form Definition (from GET .../definition) ----

export interface FieldRule {
  ruleId: string;
  conditionField: string;
  conditionOperator: string;
  conditionValue: string;
  action: string;
  logicGroup: number | null;
}

export interface FieldValidation {
  validationId: string;
  type: string;
  value: string;
  message: string;
}

export interface FieldDefinition {
  fieldId: string;
  columnCode: string;
  label: string;
  type: string;
  visible: boolean;
  readOnly: boolean;
  required: boolean;
  position: number;
  defaultValue: unknown;
  placeholder: string;
  relationTable: string | null;
  enumOptions: string[] | null;
  rules: FieldRule[];
  validations: FieldValidation[];
}

export interface LayoutSection {
  sectionId: string;
  code: string;
  label: string;
  collapsible: boolean;
  columns: number;
  position: number;
  fieldIds: string[];
}

export interface SubFormDefinition {
  id: string;
  relationCode: string;
  childFormCode: string;
  label: string;
  displayAs: string;
  position: number;
  childFormId: string | null;
  childFormLabel: string | null;
  childFormModelName: string | null;
  childFormTableName: string | null;
}

export interface FormDefinition {
  formId: string;
  formCode: string;
  formLabel: string;
  modelName: string;
  modelLabel: string | null;
  tableName: string;
  whereClauseField: string | null;
  whereClauseOperator: string | null;
  whereClauseValue: string | null;
  fields: FieldDefinition[];
  sections: LayoutSection[];
  subForms: SubFormDefinition[];
}

// ---- Record Data ----

export type RecordData = Record<string, unknown>;

export interface RecordEntry {
  id: string;
  [key: string]: unknown;
}

export interface BreadcrumbEntry {
  formCode: string;
  label: string;
  recordId: string;
}

export interface ParentContext {
  parentFormCode: string;
  parentRecordId: string;
  parentLabel: string;
}

export interface SingleRecordResponse {
  record: RecordEntry;
  subFormRecords: Record<string, RecordEntry[]>;
  breadcrumb: BreadcrumbEntry[];
  parent?: ParentContext;
}

export interface ListRecordsResponse {
  records: RecordEntry[];
  total: number;
  page: number;
  pageSize: number;
}

// ---- Hook Types ----

export interface UseFormOptions {
  recordId?: string;
  page?: number;
  pageSize?: number;
  sortField?: string;
  sortDir?: 'asc' | 'desc';
}

export interface UseFormResult {
  // Form definition (cached)
  formDefinition: FormDefinition | undefined;
  isLoadingDefinition: boolean;
  definitionError: Error | null;

  // Record data (fresh)
  records: RecordEntry[] | undefined;
  record: RecordEntry | undefined;
  subFormRecords: Record<string, RecordEntry[]> | undefined;
  breadcrumb: BreadcrumbEntry[] | undefined;
  parent: ParentContext | undefined;
  isLoadingData: boolean;
  dataError: Error | null;

  // Combined state
  isLoading: boolean;
  error: Error | null;

  // Actions
  createRecord: (data: RecordData) => Promise<RecordEntry>;
  updateRecord: (id: string, data: RecordData) => Promise<RecordEntry>;
  deleteRecord: (id: string) => Promise<void>;
  refreshData: () => void;
  invalidateDefinition: () => void;

  // Pagination
  totalRecords: number;
  currentPage: number;
  setPage: (page: number) => void;
}

// ---- API Response Envelope ----

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
  errorCode: string | null;
  details: unknown[] | null;
}
