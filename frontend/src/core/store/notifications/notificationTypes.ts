export type NotificationType = 'success' | 'error' | 'warning' | 'info';

export interface NotificationItem {
  id: string;
  type: NotificationType;
  message: string;
  duration?: number;
}

export interface NotificationState {
  notifications: NotificationItem[];
}

export interface NotificationActions {
  enqueueNotification: (notification: Omit<NotificationItem, 'id'>) => string;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

export type NotificationStore = NotificationState & NotificationActions;
