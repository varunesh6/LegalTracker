import api from './api';

export const caseService = {
  trackByCnr: async (cnrNumber) => {
    const res = await api.post('/cases/track/cnr', { cnrNumber });
    return res.data;
  },

  trackByCaseNumber: async (data) => {
    const res = await api.post('/cases/track/case-number', data);
    return res.data;
  },

  trackByFir: async (data) => {
    const res = await api.post('/cases/track/fir', data);
    return res.data;
  },

  getCaseDetails: async (caseId) => {
    const res = await api.get(`/cases/${caseId}/details`);
    return res.data;
  },

  getCaseTimeline: async (caseId) => {
    const res = await api.get(`/cases/${caseId}/timeline`);
    return res.data;
  },

  searchCases: async (criteria) => {
    const res = await api.post('/cases/search', criteria);
    return res.data;
  },

  getMyCases: async (page = 0, size = 10) => {
    const res = await api.get(`/cases/my?page=${page}&size=${size}`);
    return res.data;
  },

  createCase: async (caseData) => {
    const res = await api.post('/cases', caseData);
    return res.data;
  },

  updateCase: async (caseId, updateData) => {
    const res = await api.put(`/cases/${caseId}`, updateData);
    return res.data;
  },

  addDiaryEntry: async (caseId, entryData) => {
    const res = await api.post(`/cases/${caseId}/diary`, entryData);
    return res.data;
  },

  addHearing: async (caseId, hearingData) => {
    const res = await api.post(`/cases/${caseId}/hearings`, hearingData);
    return res.data;
  },

  addOrder: async (caseId, orderData) => {
    const res = await api.post(`/cases/${caseId}/orders`, orderData);
    return res.data;
  },

  addCaseNote: async (caseId, noteData) => {
    const res = await api.post(`/cases/${caseId}/notes`, noteData);
    return res.data;
  }
};
