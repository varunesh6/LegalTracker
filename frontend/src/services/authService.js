import api from './api';

export const authService = {
  login: async (credentials) => {
    const res = await api.post('/auth/login', credentials);
    if (res.data?.success) {
      localStorage.setItem('token', res.data.data.accessToken);
      localStorage.setItem('refreshToken', res.data.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(res.data.data));
    }
    return res.data;
  },

  registerClient: async (data) => {
    const res = await api.post('/auth/register/client', data);
    if (res.data?.success) {
      localStorage.setItem('token', res.data.data.accessToken);
      localStorage.setItem('refreshToken', res.data.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(res.data.data));
    }
    return res.data;
  },

  registerLawyer: async (data) => {
    const res = await api.post('/auth/register/lawyer', data);
    if (res.data?.success) {
      localStorage.setItem('token', res.data.data.accessToken);
      localStorage.setItem('refreshToken', res.data.data.refreshToken);
      localStorage.setItem('user', JSON.stringify(res.data.data));
    }
    return res.data;
  },

  logout: async () => {
    try {
      await api.post('/auth/logout');
    } catch (e) {
      console.error(e);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }
  },

  getCurrentUser: async () => {
    const res = await api.get('/auth/me');
    return res.data;
  },

  changePassword: async (data) => {
    const res = await api.post('/auth/change-password', data);
    return res.data;
  }
};
