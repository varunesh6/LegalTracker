import api from './api';

export const supportService = {
  createTicket: async (data) => {
    const res = await api.post('/support/tickets', data);
    return res.data;
  },

  getTicketById: async (id) => {
    const res = await api.get(`/support/tickets/${id}`);
    return res.data;
  },

  getMyTickets: async (page = 0, size = 10) => {
    const res = await api.get(`/support/tickets/my?page=${page}&size=${size}`);
    return res.data;
  },

  getAllTickets: async (status, page = 0, size = 10) => {
    const res = await api.get(`/support/tickets?${status ? `status=${status}&` : ''}page=${page}&size=${size}`);
    return res.data;
  },

  addMessageToTicket: async (ticketId, messageData) => {
    const res = await api.post(`/support/tickets/${ticketId}/messages`, messageData);
    return res.data;
  },

  updateTicketStatus: async (ticketId, statusData) => {
    const res = await api.patch(`/support/tickets/${ticketId}/status`, statusData);
    return res.data;
  }
};
