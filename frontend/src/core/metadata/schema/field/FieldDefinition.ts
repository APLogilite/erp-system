import { BaseMetadata } from '../BaseMetadata';
import { RelationDefinition } from '../relation/RelationDefinition';

/**
 * Definition of a single field within a model.
 */
export interface FieldDefinition extends BaseMetadata {
  /** Unique field code, e.g., 'product_name' */
  code: string;
  /** Human‑readable label */
  name: string;
  /** Data type of the field */
  type:
    | 'TEXT'
    | 'TEXTAREA'
    | 'NUMBER'
    | 'DECIMAL'
    | 'BOOLEAN'
    | 'DATE'
    | 'DATETIME'
    | 'EMAIL'
    | 'PHONE'
    | 'SELECT'
    | 'MULTI_SELECT'
    | 'MANY_TO_ONE'
    | 'ONE_TO_MANY'
    | 'MANY_TO_MANY'
    | 'TREE'
    | 'JSON';
  /** Validation flags */
  required?: boolean;
  readonly?: boolean;
  hidden?: boolean;
  /** Default value when creating a new entity */
  defaultValue?: unknown;
  /** Searchable flag */
  searchable?: boolean;
  /** Filterable flag */
  filterable?: boolean;
  /** Sortable flag */
  sortable?: boolean;
  /** Validation constraints */
  minLength?: number;
  maxLength?: number;
  minValue?: number;
  maxValue?: number;
  pattern?: string;
  /** UI hints */
  placeholder?: string;
  helperText?: string;
  /** Dynamic expression rules (JSON Logic or boolean) */
  visibleWhen?: unknown;
  readonlyWhen?: unknown;
  requiredWhen?: unknown;
  /** Relation settings, if applicable */
  relation?: RelationDefinition;
}
