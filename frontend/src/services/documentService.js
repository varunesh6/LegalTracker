import api from './api';

export const documentService = {
  uploadCaseDocument: async (caseId, file, category, visibility = 'LAWYER_AND_CLIENT', parentDocumentId = null) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', category);
    formData.append('visibility', visibility);
    if (parentDocumentId) formData.append('parentDocumentId', parentDocumentId);

    const res = await api.post(`/documents/cases/${caseId}/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
  },

  getCaseDocuments: async (caseId) => {
    const res = await api.get(`/documents/cases/${caseId}`);
    return res.data;
  },

  downloadDocument: async (documentId, filename) => {
    const response = await api.get(`/documents/${documentId}/download`, {
      responseType: 'blob'
    });
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename || `document_${documentId}`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },

  getDocumentVersions: async (documentId) => {
    const res = await api.get(`/documents/${documentId}/versions`);
    return res.data;
  },

  deleteDocument: async (documentId) => {
    const res = await api.delete(`/documents/${documentId}`);
    return res.data;
  }
};
