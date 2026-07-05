import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const departmentModel: ModelDefinition = {
  id: 'model_dept_001',
  code: 'department',
  name: 'Department',
  description: 'Organizational unit',
  version: 1,
  active: true,
  tableName: 'departments',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_dept_code',
      code: 'departmentCode',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_dept_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_dept_parent',
      code: 'parentDepartmentId',
      name: 'Parent Department',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_dept_mgr',
      code: 'managerId',
      name: 'Manager',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const departmentFormView: ViewDefinition = {
  id: 'view_dept_form',
  code: 'department_form',
  modelCode: 'department',
  viewType: ViewType.FORM,
  name: 'Department Form',
  title: 'Department Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Department Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'departmentCode' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'name' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'parentDepartmentId' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'managerId' } },
            ],
          },
        ],
      },
    ],
  },
};

export const departmentGridView: ViewDefinition = {
  id: 'view_dept_grid',
  code: 'department_list',
  modelCode: 'department',
  viewType: ViewType.GRID,
  name: 'Department Grid',
  title: 'Departments',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: { columns: ['departmentCode', 'name', 'parentDepartmentId', 'managerId'] },
  },
};

export const departmentActions: ActionDefinition[] = [
  {
    id: 'act_dept_create',
    code: 'create_department',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const departmentPermissions: PermissionDefinition[] = [
  {
    id: 'perm_dept_read',
    code: 'read_department',
    name: 'Read Department',
    version: 1,
    active: true,
    resource: 'department',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_dept_write',
    code: 'write_department',
    name: 'Write Department',
    version: 1,
    active: true,
    resource: 'department',
    permissionType: PermissionType.MODULE,
  },
];

export const departmentBundle: RuntimeMetadataBundle = {
  model: departmentModel,
  views: [departmentFormView, departmentGridView],
  actions: departmentActions,
  permissions: departmentPermissions,
};
