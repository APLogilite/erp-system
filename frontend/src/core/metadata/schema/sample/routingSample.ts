import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const routingModel: ModelDefinition = {
  id: 'model_routing_001',
  code: 'routing',
  name: 'Routing',
  description: 'Manufacturing process definition',
  version: 1,
  active: true,
  tableName: 'routings',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_routing_code',
      code: 'code',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_routing_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_routing_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const routingFormView: ViewDefinition = {
  id: 'view_routing_form',
  code: 'routing_form',
  modelCode: 'routing',
  viewType: ViewType.FORM,
  name: 'Routing Form',
  title: 'Routing Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Routing Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'code' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'name' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'description' } }],
          },
        ],
      },
    ],
  },
};

export const routingGridView: ViewDefinition = {
  id: 'view_routing_grid',
  code: 'routing_list',
  modelCode: 'routing',
  viewType: ViewType.GRID,
  name: 'Routing Grid',
  title: 'Routings',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'description'],
    },
  },
};

export const routingActions: ActionDefinition[] = [
  {
    id: 'act_routing_create',
    code: 'create_routing',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const routingPermissions: PermissionDefinition[] = [
  {
    id: 'perm_routing_read',
    code: 'read_routing',
    name: 'Read Routing',
    version: 1,
    active: true,
    resource: 'routing',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_routing_write',
    code: 'write_routing',
    name: 'Write Routing',
    version: 1,
    active: true,
    resource: 'routing',
    permissionType: PermissionType.MODULE,
  },
];

export const routingBundle: RuntimeMetadataBundle = {
  model: routingModel,
  views: [routingFormView, routingGridView],
  actions: routingActions,
  permissions: routingPermissions,
};
