import api from './api';

export const courtDirectoryService = {
  getStates: async () => {
    const res = await api.get('/directory/states');
    return res.data;
  },

  getDistricts: async (stateId) => {
    const res = await api.get(`/directory/states/${stateId}/districts`);
    return res.data;
  },

  getCourtComplexes: async (districtId) => {
    const res = await api.get(`/directory/districts/${districtId}/court-complexes`);
    return res.data;
  },

  getCourts: async (complexId) => {
    const res = await api.get(`/directory/court-complexes/${complexId}/courts`);
    return res.data;
  },

  getCourtsByDistrict: async (districtId) => {
    const res = await api.get(`/directory/districts/${districtId}/courts`);
    return res.data;
  },

  getPoliceStations: async (districtId) => {
    const res = await api.get(`/directory/districts/${districtId}/police-stations`);
    return res.data;
  },

  getCaseTypes: async () => {
    const res = await api.get('/directory/case-types');
    return res.data;
  }
};
