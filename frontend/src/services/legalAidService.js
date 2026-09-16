import api from './api';

export const legalAidService = {
  preCheckEligibility: async (data) => {
    const res = await api.post('/legal-aid/pre-check', data);
    return res.data;
  },

  apply: async (data) => {
    const res = await api.post('/legal-aid/apply', data);
    return res.data;
  },

  uploadDocument: async (applicationId, file, documentName, documentType) => {
    const formData = new FormData();
    formData.append('file', file);
    if (documentName) formData.append('documentName', documentName);
    if (documentType) formData.append('documentType', documentType);

    const res = await api.post(`/legal-aid/applications/${applicationId}/documents`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
  },

  getMyApplications: async (page = 0, size = 10) => {
    const res = await api.get(`/legal-aid/my-applications?page=${page}&size=${size}`);
    return res.data;
  },

  getApplicationById: async (id) => {
    const res = await api.get(`/legal-aid/applications/${id}`);
    return res.data;
  },

  searchApplications: async (params) => {
    const res = await api.get('/legal-aid/applications', { params });
    return res.data;
  },

  reviewApplication: async (id, data) => {
    const res = await api.post(`/legal-aid/applications/${id}/review`, data);
    return res.data;
  },

  assignLawyer: async (id, data) => {
    const res = await api.post(`/legal-aid/applications/${id}/assign-lawyer`, data);
    return res.data;
  }
};
