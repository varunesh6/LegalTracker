import api from './api';

export const adminService = {
  getSystemStats: async () => {
    const res = await api.get('/admin/stats');
    return res.data;
  },

  getUsers: async (role, page = 0, size = 15) => {
    const res = await api.get(`/admin/users?${role ? `role=${role}&` : ''}page=${page}&size=${size}`);
    return res.data;
  },

  updateUserStatus: async (userId, status) => {
    const res = await api.patch(`/admin/users/${userId}/status?status=${status}`);
    return res.data;
  },

  getSyncLogs: async (page = 0, size = 20) => {
    const res = await api.get(`/admin/sync-logs?page=${page}&size=${size}`);
    return res.data;
  },

  getAuditLogs: async (page = 0, size = 20) => {
    const res = await api.get(`/audit-logs?page=${page}&size=${size}`);
    return res.data;
  }
};
