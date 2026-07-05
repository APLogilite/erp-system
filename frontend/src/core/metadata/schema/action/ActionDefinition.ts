import { BaseMetadata } from '../BaseMetadata';
import { ExpressionDefinition } from '../expression/ExpressionDefinition';

export enum ActionType {
  BUTTON = 'BUTTON',
  SERVER_ACTION = 'SERVER_ACTION',
  NAVIGATION = 'NAVIGATION',
  WORKFLOW = 'WORKFLOW',
  CUSTOM = 'CUSTOM',
}

export interface ActionDefinition extends BaseMetadata {
  /** The action execution trigger type */
  actionType: ActionType;
  /** UI Icon selector name */
  icon?: string;
  /** Visibility dynamic rule */
  visibleWhen?: ExpressionDefinition;
  /** Enablement dynamic rule */
  enabledWhen?: ExpressionDefinition;
}
