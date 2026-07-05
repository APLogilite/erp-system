import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const taskModel: ModelDefinition = {
  id: 'model_task_001',
  code: 'project_task',
  name: 'Task',
  description: 'Project task',
  version: 1,
  active: true,
  tableName: 'project_tasks',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_task_no',
      code: 'taskNumber',
      name: 'Task Number',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_task_title',
      code: 'title',
      name: 'Title',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_task_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_task_pri',
      code: 'priority',
      name: 'Priority',
      type: 'SELECT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'MEDIUM',
      properties: {
        options: [
          { label: 'Low', value: 'LOW' },
          { label: 'Medium', value: 'MEDIUM' },
          { label: 'High', value: 'HIGH' },
          { label: 'Critical', value: 'CRITICAL' },
        ],
      },
    },
    {
      id: 'field_task_assign',
      code: 'assignedTo',
      name: 'Assigned To',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_task_project',
      code: 'projectId',
      name: 'Project',
      type: 'MANY_TO_ONE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_task_planned',
      code: 'plannedHours',
      name: 'Planned Hours',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_task_actual',
      code: 'actualHours',
      name: 'Actual Hours',
      type: 'DECIMAL',
      required: false,
      readonly: true,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_task_st',
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
          { label: 'Assigned', value: 'ASSIGNED' },
          { label: 'In Progress', value: 'IN_PROGRESS' },
          { label: 'Completed', value: 'COMPLETED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
  ],
};

export const taskFormView: ViewDefinition = {
  id: 'view_task_form',
  code: 'task_form',
  modelCode: 'project_task',
  viewType: ViewType.FORM,
  name: 'Task Form',
  title: 'Task Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Task Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'taskNumber' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'title' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'priority' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'projectId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'assignedTo' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'plannedHours' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'actualHours' } },
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

export const taskGridView: ViewDefinition = {
  id: 'view_task_grid',
  code: 'task_list',
  modelCode: 'project_task',
  viewType: ViewType.GRID,
  name: 'Task Grid',
  title: 'Tasks',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'taskNumber',
        'title',
        'priority',
        'assignedTo',
        'projectId',
        'plannedHours',
        'actualHours',
        'status',
      ],
    },
  },
};

export const taskActions: ActionDefinition[] = [
  {
    id: 'act_task_create',
    code: 'create_task',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_task_assign',
    code: 'assign',
    name: 'Assign',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'user',
  },
  {
    id: 'act_task_start',
    code: 'start',
    name: 'Start',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'play',
  },
  {
    id: 'act_task_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
];

export const taskPermissions: PermissionDefinition[] = [
  {
    id: 'perm_task_read',
    code: 'read_task',
    name: 'Read Task',
    version: 1,
    active: true,
    resource: 'project_task',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_task_write',
    code: 'write_task',
    name: 'Write Task',
    version: 1,
    active: true,
    resource: 'project_task',
    permissionType: PermissionType.MODULE,
  },
];

export const taskBundle: RuntimeMetadataBundle = {
  model: taskModel,
  views: [taskFormView, taskGridView],
  actions: taskActions,
  permissions: taskPermissions,
};
