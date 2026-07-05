import { create } from 'zustand';

import { NotificationStore } from './notificationTypes';

export const useNotificationStore = create<NotificationStore>((set) => ({
  notifications: [],

  enqueueNotification: (notification) => {
    const id = Math.random().toString(36).substring(2, 9);
    set((state) => ({
      notifications: [...state.notifications, { ...notification, id }],
    }));
    return id;
  },

  removeNotification: (id) =>
    set((state) => ({
      notifications: state.notifications.filter((n) => n.id !== id),
    })),

  clearNotifications: () => set({ notifications: [] }),
}));

export const notifyActions = {
  success: (message: string, duration?: number) =>
    useNotificationStore.getState().enqueueNotification({ type: 'success', message, duration }),
  error: (message: string, duration?: number) =>
    useNotificationStore.getState().enqueueNotification({ type: 'error', message, duration }),
  warning: (message: string, duration?: number) =>
    useNotificationStore.getState().enqueueNotification({ type: 'warning', message, duration }),
  info: (message: string, duration?: number) =>
    useNotificationStore.getState().enqueueNotification({ type: 'info', message, duration }),
};
