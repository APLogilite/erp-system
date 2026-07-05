import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const workCenterModel: ModelDefinition = {
  id: 'model_wc_001',
  code: 'work_center',
  name: 'Work Center',
  description: 'Production resource definition',
  version: 1,
  active: true,
  tableName: 'work_centers',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_wc_code',
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
      id: 'field_wc_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_wc_capacity',
      code: 'capacity',
      name: 'Capacity',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 8,
    },
    {
      id: 'field_wc_cost',
      code: 'costPerHour',
      name: 'Cost/Hour',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_wc_eff',
      code: 'efficiency',
      name: 'Efficiency',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 100,
    },
    {
      id: 'field_wc_calendar',
      code: 'calendar',
      name: 'Calendar',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const workCenterFormView: ViewDefinition = {
  id: 'view_wc_form',
  code: 'work_center_form',
  modelCode: 'work_center',
  viewType: ViewType.FORM,
  name: 'Work Center Form',
  title: 'Work Center Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Work Center Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'code' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'calendar' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'capacity' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'costPerHour' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'efficiency' } },
            ],
          },
        ],
      },
    ],
  },
};

export const workCenterGridView: ViewDefinition = {
  id: 'view_wc_grid',
  code: 'work_center_list',
  modelCode: 'work_center',
  viewType: ViewType.GRID,
  name: 'Work Center Grid',
  title: 'Work Centers',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'capacity', 'costPerHour', 'efficiency', 'calendar'],
    },
  },
};

export const workCenterActions: ActionDefinition[] = [
  {
    id: 'act_wc_create',
    code: 'create_work_center',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const workCenterPermissions: PermissionDefinition[] = [
  {
    id: 'perm_wc_read',
    code: 'read_work_center',
    name: 'Read Work Center',
    version: 1,
    active: true,
    resource: 'work_center',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_wc_write',
    code: 'write_work_center',
    name: 'Write Work Center',
    version: 1,
    active: true,
    resource: 'work_center',
    permissionType: PermissionType.MODULE,
  },
];

export const workCenterBundle: RuntimeMetadataBundle = {
  model: workCenterModel,
  views: [workCenterFormView, workCenterGridView],
  actions: workCenterActions,
  permissions: workCenterPermissions,
};
