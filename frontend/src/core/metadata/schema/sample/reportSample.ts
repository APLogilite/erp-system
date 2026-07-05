import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const reportModel: ModelDefinition = {
  id: 'model_report_001',
  code: 'report_definition',
  name: 'Report Definition',
  description: 'Report definitions for analytics',
  version: 1,
  active: true,
  tableName: 'report_definitions',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_report_code',
      code: 'reportCode',
      name: 'Report Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_report_name',
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
      id: 'field_report_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_report_type',
      code: 'reportType',
      name: 'Report Type',
      type: 'TEXT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_report_model',
      code: 'modelCode',
      name: 'Model Code',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_report_format',
      code: 'outputFormat',
      name: 'Output Format',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'TABLE',
      properties: {
        options: [
          { label: 'Table', value: 'TABLE' },
          { label: 'CSV', value: 'CSV' },
          { label: 'Excel', value: 'EXCEL' },
          { label: 'PDF', value: 'PDF' },
        ],
      },
    },
  ],
};

export const reportFormView: ViewDefinition = {
  id: 'view_report_form',
  code: 'report_form',
  modelCode: 'report_definition',
  viewType: ViewType.FORM,
  name: 'Report Form',
  title: 'Report Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Report Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'reportCode' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'reportType' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'outputFormat' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'modelCode' } }],
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

export const reportGridView: ViewDefinition = {
  id: 'view_report_grid',
  code: 'report_list',
  modelCode: 'report_definition',
  viewType: ViewType.GRID,
  name: 'Report Grid',
  title: 'Report Definitions',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['reportCode', 'name', 'reportType', 'modelCode', 'outputFormat'],
    },
  },
};

export const reportActions: ActionDefinition[] = [
  {
    id: 'act_report_create',
    code: 'create_report',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const reportPermissions: PermissionDefinition[] = [
  {
    id: 'perm_report_read',
    code: 'read_report',
    name: 'Read Report',
    version: 1,
    active: true,
    resource: 'report_definition',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_report_admin',
    code: 'admin_report',
    name: 'Admin Report',
    version: 1,
    active: true,
    resource: 'report_definition',
    permissionType: PermissionType.MODULE,
  },
];

export const reportBundle: RuntimeMetadataBundle = {
  model: reportModel,
  views: [reportFormView, reportGridView],
  actions: reportActions,
  permissions: reportPermissions,
};
