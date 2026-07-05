import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const employeeModel: ModelDefinition = {
  id: 'model_emp_001',
  code: 'employee',
  name: 'Employee',
  description: 'Employee record',
  version: 1,
  active: true,
  tableName: 'employees',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_emp_code',
      code: 'employeeCode',
      name: 'Employee Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_fn',
      code: 'firstName',
      name: 'First Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_ln',
      code: 'lastName',
      name: 'Last Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_em',
      code: 'email',
      name: 'Email',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_ph',
      code: 'phone',
      name: 'Phone',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_dept',
      code: 'departmentId',
      name: 'Department',
      type: 'MANY_TO_ONE',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_desig',
      code: 'designation',
      name: 'Designation',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_mgr',
      code: 'managerId',
      name: 'Manager',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_jd',
      code: 'joiningDate',
      name: 'Joining Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_emp_st',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'ACTIVE',
      properties: {
        options: [
          { label: 'Active', value: 'ACTIVE' },
          { label: 'Inactive', value: 'INACTIVE' },
          { label: 'Terminated', value: 'TERMINATED' },
        ],
      },
    },
  ],
};

export const employeeFormView: ViewDefinition = {
  id: 'view_emp_form',
  code: 'employee_form',
  modelCode: 'employee',
  viewType: ViewType.FORM,
  name: 'Employee Form',
  title: 'Employee Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Personal Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'employeeCode' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'firstName' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'lastName' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'email' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'phone' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'designation' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'joiningDate' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'departmentId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'managerId' } },
            ],
          },
        ],
      },
    ],
  },
};

export const employeeGridView: ViewDefinition = {
  id: 'view_emp_grid',
  code: 'employee_list',
  modelCode: 'employee',
  viewType: ViewType.GRID,
  name: 'Employee Grid',
  title: 'Employees',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'employeeCode',
        'firstName',
        'lastName',
        'email',
        'departmentId',
        'designation',
        'status',
      ],
    },
  },
};

export const employeeActions: ActionDefinition[] = [
  {
    id: 'act_emp_create',
    code: 'create_employee',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const employeePermissions: PermissionDefinition[] = [
  {
    id: 'perm_emp_read',
    code: 'read_employee',
    name: 'Read Employee',
    version: 1,
    active: true,
    resource: 'employee',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_emp_write',
    code: 'write_employee',
    name: 'Write Employee',
    version: 1,
    active: true,
    resource: 'employee',
    permissionType: PermissionType.MODULE,
  },
];

export const employeeBundle: RuntimeMetadataBundle = {
  model: employeeModel,
  views: [employeeFormView, employeeGridView],
  actions: employeeActions,
  permissions: employeePermissions,
};
