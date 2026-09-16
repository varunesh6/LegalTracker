import api from './api';

export const chatService = {
  getMyConversations: async () => {
    const res = await api.get('/chat/conversations');
    return res.data;
  },

  getOrCreateConversation: async (lawyerId, caseId = null) => {
    const res = await api.post(`/chat/conversations?lawyerId=${lawyerId}${caseId ? `&caseId=${caseId}` : ''}`);
    return res.data;
  },

  getMessages: async (conversationId) => {
    const res = await api.get(`/chat/conversations/${conversationId}/messages`);
    return res.data;
  },

  sendMessage: async (conversationId, messageData) => {
    const res = await api.post(`/chat/conversations/${conversationId}/messages`, messageData);
    return res.data;
  },

  markAsRead: async (conversationId) => {
    const res = await api.put(`/chat/conversations/${conversationId}/read`);
    return res.data;
  }
};
