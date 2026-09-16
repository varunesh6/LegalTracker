import api from './api';

export const notificationService = {
  getMyNotifications: async (page = 0, size = 15) => {
    const res = await api.get(`/notifications?page=${page}&size=${size}`);
    return res.data;
  },

  getUnreadNotifications: async () => {
    const res = await api.get('/notifications/unread');
    return res.data;
  },

  getUnreadCount: async () => {
    const res = await api.get('/notifications/unread-count');
    return res.data;
  },

  markAsRead: async (notificationId) => {
    const res = await api.put(`/notifications/${notificationId}/read`);
    return res.data;
  },

  markAllAsRead: async () => {
    const res = await api.put('/notifications/read-all');
    return res.data;
  }
};
