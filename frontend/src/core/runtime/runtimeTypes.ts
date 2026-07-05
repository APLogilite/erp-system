export type RuntimeEventType =
  | 'FORM_CHANGED'
  | 'WORKFLOW_TRANSITIONED'
  | 'METADATA_UPDATED'
  | 'RELATION_SELECTED';

export interface RuntimeEvent<T = unknown> {
  type: RuntimeEventType;
  payload: T;
  timestamp: number;
}

export type RuntimeEventListener<T = unknown> = (event: RuntimeEvent<T>) => void;
