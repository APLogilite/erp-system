import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const projectModel: ModelDefinition = {
  id: 'model_prj_001',
  code: 'project',
  name: 'Project',
  description: 'Project management',
  version: 1,
  active: true,
  tableName: 'projects',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_prj_code',
      code: 'projectCode',
      name: 'Project Code',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_cust',
      code: 'customerId',
      name: 'Customer',
      type: 'MANY_TO_ONE',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_mgr',
      code: 'managerId',
      name: 'Manager',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_start',
      code: 'startDate',
      name: 'Start Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_end',
      code: 'endDate',
      name: 'End Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_prj_st',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'OPEN',
      properties: {
        options: [
          { label: 'Open', value: 'OPEN' },
          { label: 'In Progress', value: 'IN_PROGRESS' },
          { label: 'Completed', value: 'COMPLETED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_prj_budget',
      code: 'budget',
      name: 'Budget',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
  ],
};

export const projectFormView: ViewDefinition = {
  id: 'view_prj_form',
  code: 'project_form',
  modelCode: 'project',
  viewType: ViewType.FORM,
  name: 'Project Form',
  title: 'Project Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Project Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'projectCode' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'customerId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'managerId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'budget' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'startDate' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'endDate' } },
            ],
          },
        ],
      },
    ],
  },
};

export const projectGridView: ViewDefinition = {
  id: 'view_prj_grid',
  code: 'project_list',
  modelCode: 'project',
  viewType: ViewType.GRID,
  name: 'Project Grid',
  title: 'Projects',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'projectCode',
        'name',
        'customerId',
        'managerId',
        'status',
        'startDate',
        'endDate',
        'budget',
      ],
    },
  },
};

export const projectActions: ActionDefinition[] = [
  {
    id: 'act_prj_create',
    code: 'create_project',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_prj_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
];

export const projectPermissions: PermissionDefinition[] = [
  {
    id: 'perm_prj_read',
    code: 'read_project',
    name: 'Read Project',
    version: 1,
    active: true,
    resource: 'project',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_prj_write',
    code: 'write_project',
    name: 'Write Project',
    version: 1,
    active: true,
    resource: 'project',
    permissionType: PermissionType.MODULE,
  },
];

export const projectBundle: RuntimeMetadataBundle = {
  model: projectModel,
  views: [projectFormView, projectGridView],
  actions: projectActions,
  permissions: projectPermissions,
};
