import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const notificationModel: ModelDefinition = {
  id: 'model_notification_001',
  code: 'notification',
  name: 'Notification',
  description: 'Platform notifications',
  version: 1,
  active: true,
  tableName: 'notifications',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_notif_title',
      code: 'title',
      name: 'Title',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_notif_message',
      code: 'message',
      name: 'Message',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_notif_type',
      code: 'type',
      name: 'Type',
      type: 'SELECT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'Info', value: 'INFO' },
          { label: 'Success', value: 'SUCCESS' },
          { label: 'Warning', value: 'WARNING' },
          { label: 'Error', value: 'ERROR' },
          { label: 'Action Required', value: 'ACTION_REQUIRED' },
        ],
      },
    },
    {
      id: 'field_notif_recipient',
      code: 'recipient',
      name: 'Recipient',
      type: 'TEXT',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_notif_module',
      code: 'module',
      name: 'Module',
      type: 'TEXT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_notif_status',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'Unread', value: 'UNREAD' },
          { label: 'Read', value: 'READ' },
          { label: 'Dismissed', value: 'DISMISSED' },
        ],
      },
    },
  ],
};

export const notificationFormView: ViewDefinition = {
  id: 'view_notification_form',
  code: 'notification_form',
  modelCode: 'notification',
  viewType: ViewType.FORM,
  name: 'Notification Form',
  title: 'Notification Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Notification Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'title' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'type' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'recipient' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'module' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'message' } }],
          },
        ],
      },
    ],
  },
};

export const notificationGridView: ViewDefinition = {
  id: 'view_notification_grid',
  code: 'notification_list',
  modelCode: 'notification',
  viewType: ViewType.GRID,
  name: 'Notification Grid',
  title: 'Notifications',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['title', 'type', 'recipient', 'module', 'status', 'createdAt'],
    },
  },
};

export const notificationActions: ActionDefinition[] = [
  {
    id: 'act_notif_send',
    code: 'send_notification',
    name: 'Send',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'send',
  },
];

export const notificationPermissions: PermissionDefinition[] = [
  {
    id: 'perm_notif_read',
    code: 'read_notification',
    name: 'Read Notification',
    version: 1,
    active: true,
    resource: 'notification',
    permissionType: PermissionType.MODULE,
  },
];

export const notificationBundle: RuntimeMetadataBundle = {
  model: notificationModel,
  views: [notificationFormView, notificationGridView],
  actions: notificationActions,
  permissions: notificationPermissions,
};
