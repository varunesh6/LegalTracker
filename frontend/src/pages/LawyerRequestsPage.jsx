import React, { useState, useEffect } from 'react';
import { lawyerService } from '../services/lawyerService';
import { useAuth } from '../context/AuthContext';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { UserCheck, CheckCircle2, XCircle, Clock } from 'lucide-react';

export const LawyerRequestsPage = () => {
  const { isLawyer } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchRequests = async () => {
    try {
      const res = isLawyer()
        ? await lawyerService.getMyReceivedRequests(0, 50)
        : await lawyerService.getMySentRequests(0, 50);

      if (res?.success) {
        setRequests(res.data.content || []);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleRespond = async (id, status) => {
    try {
      const res = await lawyerService.respondToRequest(id, status, `Response from advocate: ${status}`);
      if (res?.success) {
        setRequests((prev) => prev.map((r) => (r.id === id ? { ...r, status } : r)));
      }
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          <div className="glass-card" style={{ marginBottom: '2rem' }}>
            <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>
              {isLawyer() ? 'Client Consultation Inquiries' : 'My Advocate Consultation Requests'}
            </h1>
            <p style={{ fontSize: '0.9rem' }}>Track the status of direct advocate inquiries and representation requests.</p>
          </div>

          <div className="glass-card">
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading inquiries...</p>
            ) : requests.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '3rem 0' }}>
                No consultation inquiries found.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {requests.map((r) => (
                  <div
                    key={r.id}
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
                        <strong style={{ fontSize: '1.05rem' }}>
                          {isLawyer() ? r.clientName : r.lawyerName}
                        </strong>
                        <Badge variant={r.status}>{r.status}</Badge>
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        <strong>Matter Type: </strong>{r.caseType} {r.courtName ? `(${r.courtName})` : ''}
                      </div>
                      <p style={{ fontSize: '0.85rem', marginTop: '0.35rem', fontStyle: 'italic', color: 'var(--text-primary)' }}>
                        "{r.message}"
                      </p>
                    </div>

                    {isLawyer() && r.status === 'PENDING' && (
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button onClick={() => handleRespond(r.id, 'ACCEPTED')} className="btn btn-success btn-sm">
                          <CheckCircle2 size={14} /> Accept
                        </button>
                        <button onClick={() => handleRespond(r.id, 'REJECTED')} className="btn btn-danger btn-sm">
                          <XCircle size={14} /> Decline
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};
