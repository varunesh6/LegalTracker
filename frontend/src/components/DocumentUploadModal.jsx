import React, { useState } from 'react';
import { Modal } from './Modal';
import { documentService } from '../services/documentService';
import { UploadCloud, File, AlertCircle } from 'lucide-react';

export const DocumentUploadModal = ({ isOpen, onClose, caseId, onUploadSuccess }) => {
  const [file, setFile] = useState(null);
  const [category, setCategory] = useState('PLAINT_OR_PETITION');
  const [visibility, setVisibility] = useState('LAWYER_AND_CLIENT');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please choose a file to upload');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const res = await documentService.uploadCaseDocument(caseId, file, category, visibility);
      if (res?.success) {
        onUploadSuccess(res.data);
        onClose();
      } else {
        setError(res?.message || 'Failed to upload document');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Upload failed. File type or size may be restricted.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Upload Case Document">
      <form onSubmit={handleSubmit}>
        {error && (
          <div style={{ background: 'var(--danger-light)', border: '1px solid var(--danger)', padding: '0.75rem', borderRadius: 'var(--radius-md)', color: 'var(--danger)', fontSize: '0.85rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <div className="form-group">
          <label className="form-label">Document File (PDF, DOCX, JPG, PNG &lt; 15MB)</label>
          <input
            type="file"
            onChange={handleFileChange}
            className="form-control"
            accept=".pdf,.docx,.doc,.jpg,.jpeg,.png"
            required
          />
          {file && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.5rem', fontSize: '0.85rem', color: 'var(--primary)' }}>
              <File size={16} />
              <span>{file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)</span>
            </div>
          )}
        </div>

        <div className="form-group">
          <label className="form-label">Document Category</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="form-control"
          >
            <option value="PLAINT_OR_PETITION">Plaint / Petition</option>
            <option value="WRITTEN_STATEMENT">Written Statement / Counter</option>
            <option value="AFFIDAVIT">Affidavit</option>
            <option value="EVIDENCE">Evidence / Exhibits</option>
            <option value="INTERIM_APPLICATION">Interim Application (IA)</option>
            <option value="VAKALATNAMA">Vakalatnama / Authorization</option>
            <option value="COURT_ORDER">Court Order Copy</option>
            <option value="LEGAL_NOTICE">Legal Notice</option>
            <option value="IDENTITY_PROOF">Identity / Address Proof</option>
            <option value="OTHER">Other Reference Material</option>
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Access & Visibility</label>
          <select
            value={visibility}
            onChange={(e) => setVisibility(e.target.value)}
            className="form-control"
          >
            <option value="LAWYER_AND_CLIENT">Shared with Lawyer & Client</option>
            <option value="LAWYER_ONLY">Lawyer Only (Confidential / Draft)</option>
            <option value="CLIENT_PRIVATE">Client Private</option>
            <option value="PUBLIC">Public</option>
          </select>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
          <button type="button" onClick={onClose} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" disabled={loading} className="btn btn-primary">
            <UploadCloud size={16} />
            <span>{loading ? 'Uploading...' : 'Upload Document'}</span>
          </button>
        </div>
      </form>
    </Modal>
  );
};
