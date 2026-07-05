import { BaseMetadata } from '../BaseMetadata';
import { FieldDefinition } from '../field/FieldDefinition';

/**
 * Represents an ERP business object.
 */
export interface ModelDefinition extends BaseMetadata {
  /** Unique model code, e.g., 'business_partner' */
  code: string;
  /** Human‑readable name of the model */
  name: string;
  /** Optional description */
  description?: string;
  /** Underlying database table name */
  tableName: string;
  /** Indicates if entity should be audited (created/updated/deleted) */
  auditable: boolean;
  /** Enable workflow engine for this model */
  workflowEnabled: boolean;
  /** Multi‑tenant aware flag */
  tenantAware: boolean;
  /** Array of field definitions belonging to this model */
  fields: FieldDefinition[];
}
