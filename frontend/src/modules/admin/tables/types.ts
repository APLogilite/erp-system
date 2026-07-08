export interface TableColumn {
  id: string;
  tableId: string;
  code: string;
  label: string;
  type: ColumnType;
  required: boolean;
  defaultValue?: string;
  maxLength?: number;
  precision?: number;
  scale?: number;
  relationTable?: string;
  enumOptions?: Record<string, string>;
  position: number;
  isActive: boolean;
}

export type ColumnType =
  | 'string'
  | 'text'
  | 'integer'
  | 'decimal'
  | 'boolean'
  | 'date'
  | 'datetime'
  | 'many2one'
  | 'enum';

export interface TableDefinition {
  id: string;
  code: string;
  label: string;
  pluralLabel: string;
  description?: string;
  tableName: string;
  tableType: string;
  isActive: boolean;
  columns: TableColumn[];
}

export interface CreateTablePayload {
  code: string;
  label: string;
  pluralLabel: string;
  description?: string;
  tableName: string;
  columns?: CreateColumnPayload[];
}

export interface UpdateTablePayload {
  label?: string;
  pluralLabel?: string;
  description?: string;
}

export interface CreateColumnPayload {
  code: string;
  label: string;
  type: ColumnType;
  required?: boolean;
  defaultValue?: string;
  maxLength?: number;
  precision?: number;
  scale?: number;
  relationTable?: string;
  enumOptions?: Record<string, string>;
  position?: number;
}

export interface UpdateColumnPayload {
  label?: string;
  type?: ColumnType;
  required?: boolean;
  defaultValue?: string;
  maxLength?: number;
  precision?: number;
  scale?: number;
  relationTable?: string;
  enumOptions?: Record<string, string>;
  position?: number;
}

export interface ColumnReorderPayload {
  columnIds: string[];
}

export interface VersionHistoryEntry {
  id: string;
  version: number;
  tableId: string;
  description: string;
  changedBy?: string;
  createdAt: string;
}

export const COLUMN_TYPE_LABELS: Record<ColumnType, string> = {
  string: 'String (VARCHAR)',
  text: 'Text (TEXT)',
  integer: 'Integer',
  decimal: 'Decimal',
  boolean: 'Boolean',
  date: 'Date',
  datetime: 'Date & Time',
  many2one: 'Relation (Many-to-One)',
  enum: 'Enum',
};

export const SNAKE_CASE_REGEX = /^[a-z][a-z0-9_]*$/;
