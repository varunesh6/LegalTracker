import api from './api';

export const lawyerService = {
  searchLawyers: async (params) => {
    const res = await api.get('/lawyers/search', { params });
    return res.data;
  },

  getLawyerById: async (id) => {
    const res = await api.get(`/lawyers/${id}`);
    return res.data;
  },

  updateAvailability: async (data) => {
    const res = await api.put('/lawyers/availability', data);
    return res.data;
  },

  // Lawyer Requests
  sendRequest: async (data) => {
    const res = await api.post('/lawyer-requests', data);
    return res.data;
  },

  getMySentRequests: async (page = 0, size = 10) => {
    const res = await api.get(`/lawyer-requests/my-sent?page=${page}&size=${size}`);
    return res.data;
  },

  getMyReceivedRequests: async (page = 0, size = 10) => {
    const res = await api.get(`/lawyer-requests/my-received?page=${page}&size=${size}`);
    return res.data;
  },

  respondToRequest: async (id, status, responseMessage) => {
    const res = await api.put(`/lawyer-requests/${id}/respond`, { status, responseMessage });
    return res.data;
  }
};
