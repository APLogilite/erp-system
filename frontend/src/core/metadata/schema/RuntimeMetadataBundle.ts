import { ActionDefinition } from './action/ActionDefinition';
import { ModelDefinition } from './model/ModelDefinition';
import { PermissionDefinition } from './permission/PermissionDefinition';
import { ViewDefinition } from './view/ViewDefinition';
import { WorkflowDefinition } from './workflow/WorkflowDefinition';

export interface RuntimeMetadataBundle {
  /** The model definition representing the business object */
  model: ModelDefinition;
  /** Views defined for this model (e.g. List, Form) */
  views: ViewDefinition[];
  /** Optional lifecycle workflow definition */
  workflow?: WorkflowDefinition;
  /** Actions available for the model */
  actions?: ActionDefinition[];
  /** Permissions governing access to this model/fields/views */
  permissions?: PermissionDefinition[];
}
