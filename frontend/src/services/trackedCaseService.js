import api from './api';

export const trackedCaseService = {
  getMyTrackedCases: async (query = '', page = 0, size = 10) => {
    const res = await api.get(`/tracked-cases?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    return res.data;
  },

  getAllTrackedCases: async () => {
    const res = await api.get('/tracked-cases/all');
    return res.data;
  },

  trackCase: async (data) => {
    const res = await api.post('/tracked-cases', data);
    return res.data;
  },

  updateTrackedCase: async (id, data) => {
    const res = await api.put(`/tracked-cases/${id}`, data);
    return res.data;
  },

  stopTracking: async (id) => {
    const res = await api.delete(`/tracked-cases/${id}`);
    return res.data;
  },

  toggleNotifications: async (id, enabled) => {
    const res = await api.patch(`/tracked-cases/${id}/notifications?enabled=${enabled}`);
    return res.data;
  }
};
