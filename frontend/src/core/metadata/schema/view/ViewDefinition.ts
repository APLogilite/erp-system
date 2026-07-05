import { BaseMetadata } from '../BaseMetadata';
import { LayoutDefinition } from '../layout/LayoutDefinition';

export enum ViewType {
  FORM = 'FORM',
  GRID = 'GRID',
  KANBAN = 'KANBAN',
  DETAIL = 'DETAIL',
  DASHBOARD = 'DASHBOARD',
}

export interface ViewDefinition extends BaseMetadata {
  /** Unique view code, e.g., 'business_partner_form' */
  code: string;
  /** Code of the model this view renders */
  modelCode: string;
  /** Type of the view */
  viewType: ViewType;
  /** Title of the view */
  title: string;
  /** Layout configuration for this view */
  layout: LayoutDefinition;
}
