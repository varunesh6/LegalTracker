import React, { useState, useEffect } from 'react';
import { legalAidService } from '../services/legalAidService';
import { lawyerService } from '../services/lawyerService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { Modal } from '../components/Modal';
import {
  ShieldAlert,
  FileCheck2,
  Users,
  CheckCircle2,
  XCircle,
  UserCheck,
  Building,
  AlertCircle
} from 'lucide-react';

export const OfficerDashboard = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  // Review / Assign Modal
  const [selectedApp, setSelectedApp] = useState(null);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);

  // Review Form
  const [newStatus, setNewStatus] = useState('APPROVED');
  const [reviewRemarks, setReviewRemarks] = useState('');
  const [reviewing, setReviewing] = useState(false);

  // Assign Form
  const [lawyers, setLawyers] = useState([]);
  const [assignedLawyerId, setAssignedLawyerId] = useState('');
  const [assignInstructions, setAssignInstructions] = useState('');
  const [assigning, setAssigning] = useState(false);

  const fetchApplications = async () => {
    setLoading(true);
    try {
      const res = await legalAidService.searchApplications({});
      if (res?.success) {
        setApplications(res.data.content || []);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApplications();
    lawyerService.searchLawyers({}).then((res) => {
      if (res?.success && res.data.content?.length > 0) {
        setLawyers(res.data.content);
        setAssignedLawyerId(res.data.content[0].userId || res.data.content[0].id);
      }
    });
  }, []);

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!selectedApp) return;

    setReviewing(true);
    try {
      const res = await legalAidService.reviewApplication(selectedApp.id, {
        newStatus,
        remarks: reviewRemarks
      });
      if (res?.success) {
        setShowReviewModal(false);
        fetchApplications();
      }
    } catch (e) {
      console.error(e);
    } finally {
      setReviewing(false);
    }
  };

  const handleAssignSubmit = async (e) => {
    e.preventDefault();
    if (!selectedApp || !assignedLawyerId) return;

    setAssigning(true);
    try {
      const res = await legalAidService.assignLawyer(selectedApp.id, {
        lawyerId: Number(assignedLawyerId),
        instructions: assignInstructions
      });
      if (res?.success) {
        setShowAssignModal(false);
        fetchApplications();
      }
    } catch (e) {
      console.error(e);
    } finally {
      setAssigning(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          <div className="glass-card" style={{ marginBottom: '2rem' }}>
            <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Legal Services Authority Desk</h1>
            <p style={{ fontSize: '0.9rem' }}>Review citizen eligibility, certificate verifications, and advocate assignments under NALSA.</p>
          </div>

          {/* Applications Queue */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <FileCheck2 size={18} color="var(--purple)" /> Legal Aid Applications Queue
            </h3>

            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading applications...</p>
            ) : applications.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No legal aid applications in the queue.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {applications.map((app) => (
                  <div
                    key={app.id}
                    style={{
                      padding: '1.25rem',
                      background: 'var(--bg-glass)',
                      borderRadius: 'var(--radius-lg)',
                      border: '1px solid var(--border-color)',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      flexWrap: 'wrap',
                      gap: '1rem'
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                        <strong style={{ fontSize: '1rem' }}>{app.fullName}</strong>
                        <Badge variant={app.status}>{app.status}</Badge>
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                          Ref: {app.applicationNumber}
                        </span>
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        <strong>Category: </strong>{app.selectedCategory} | <strong>Income: </strong>₹{app.annualIncome?.toLocaleString() || 'N/A'}
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                        Matter: {app.caseType} ({app.districtName})
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                      <button
                        onClick={() => {
                          setSelectedApp(app);
                          setNewStatus('APPROVED');
                          setShowReviewModal(true);
                        }}
                        className="btn btn-secondary btn-sm"
                      >
                        Review Eligibility
                      </button>

                      {(app.status === 'APPROVED' || app.status === 'SUBMITTED') && (
                        <button
                          onClick={() => {
                            setSelectedApp(app);
                            setShowAssignModal(true);
                          }}
                          className="btn btn-primary btn-sm"
                        >
                          <UserCheck size={14} /> Assign Advocate
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </main>
      </div>

      {/* Review Modal */}
      <Modal
        isOpen={showReviewModal}
        onClose={() => setShowReviewModal(false)}
        title={`Review Application: ${selectedApp?.applicationNumber}`}
      >
        <form onSubmit={handleReviewSubmit}>
          <div className="form-group">
            <label className="form-label">Review Decision / Status</label>
            <select
              className="form-control"
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
            >
              <option value="APPROVED">APPROVE — Statutory Criteria Satisfied</option>
              <option value="REJECTED">REJECT — Does Not Meet Income / Category Threshold</option>
              <option value="ADDITIONAL_DOCUMENTS_REQUIRED">REQUEST ADDITIONAL DOCUMENTS</option>
              <option value="UNDER_REVIEW">UNDER DETAILED INQUIRY</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Officer Assessment Notes &amp; Remarks</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Enter official grounds for decision or specific missing certificates required..."
              value={reviewRemarks}
              onChange={(e) => setReviewRemarks(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowReviewModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={reviewing} className="btn btn-primary">
              <span>{reviewing ? 'Recording...' : 'Submit Decision'}</span>
            </button>
          </div>
        </form>
      </Modal>

      {/* Assign Lawyer Modal */}
      <Modal
        isOpen={showAssignModal}
        onClose={() => setShowAssignModal(false)}
        title={`Assign Panel Advocate to: ${selectedApp?.fullName}`}
      >
        <form onSubmit={handleAssignSubmit}>
          <div className="form-group">
            <label className="form-label">Select Panel Advocate</label>
            <select
              className="form-control"
              value={assignedLawyerId}
              onChange={(e) => setAssignedLawyerId(e.target.value)}
              required
            >
              {lawyers.map((lawyer) => (
                <option key={lawyer.id} value={lawyer.userId || lawyer.id}>
                  {lawyer.name} — Bar: {lawyer.barRegistrationNumber} ({lawyer.districtName})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Assignment Instructions for Counsel</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Instructions regarding pleadings, interim protection, and next court hearing..."
              value={assignInstructions}
              onChange={(e) => setAssignInstructions(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowAssignModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={assigning} className="btn btn-primary">
              <UserCheck size={16} />
              <span>{assigning ? 'Assigning...' : 'Assign & Create Case File'}</span>
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
