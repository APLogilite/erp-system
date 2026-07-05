import { BaseMetadata } from '../BaseMetadata';
import { ExpressionDefinition } from '../expression/ExpressionDefinition';

export enum PermissionType {
  MODULE = 'MODULE',
  MENU = 'MENU',
  VIEW = 'VIEW',
  FIELD = 'FIELD',
  ACTION = 'ACTION',
  ROW = 'ROW',
}

export interface PermissionDefinition extends BaseMetadata {
  /** The target resource path or identifier, e.g. 'sales_order.credit_limit' */
  resource: string;
  /** Level of permission enforcement */
  permissionType: PermissionType;
  /** Dynamic expression evaluating condition context */
  expression?: ExpressionDefinition;
}
