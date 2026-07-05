import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const dashboardModel: ModelDefinition = {
  id: 'model_dashboard_001',
  code: 'dashboard',
  name: 'Dashboard',
  description: 'Analytics dashboards',
  version: 1,
  active: true,
  tableName: 'dashboards',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_dash_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_dash_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_dash_layout',
      code: 'layout',
      name: 'Layout',
      type: 'JSON',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_dash_default',
      code: 'isDefault',
      name: 'Is Default',
      type: 'BOOLEAN',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const dashboardFormView: ViewDefinition = {
  id: 'view_dashboard_form',
  code: 'dashboard_form',
  modelCode: 'dashboard',
  viewType: ViewType.FORM,
  name: 'Dashboard Form',
  title: 'Dashboard Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Dashboard Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'isDefault' } },
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

export const dashboardGridView: ViewDefinition = {
  id: 'view_dashboard_grid',
  code: 'dashboard_list',
  modelCode: 'dashboard',
  viewType: ViewType.GRID,
  name: 'Dashboard Grid',
  title: 'Dashboards',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['name', 'description', 'isDefault'],
    },
  },
};

export const dashboardActions: ActionDefinition[] = [
  {
    id: 'act_dash_create',
    code: 'create_dashboard',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const dashboardPermissions: PermissionDefinition[] = [
  {
    id: 'perm_dash_read',
    code: 'read_dashboard',
    name: 'Read Dashboard',
    version: 1,
    active: true,
    resource: 'dashboard',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_dash_admin',
    code: 'admin_dashboard',
    name: 'Admin Dashboard',
    version: 1,
    active: true,
    resource: 'dashboard',
    permissionType: PermissionType.MODULE,
  },
];

export const dashboardBundle: RuntimeMetadataBundle = {
  model: dashboardModel,
  views: [dashboardFormView, dashboardGridView],
  actions: dashboardActions,
  permissions: dashboardPermissions,
};
