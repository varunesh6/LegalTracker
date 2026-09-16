import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { caseService } from '../services/caseService';
import { lawyerService } from '../services/lawyerService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import {
  Briefcase,
  Calendar,
  Clock,
  UserCheck,
  CheckCircle2,
  XCircle,
  Plus,
  ArrowRight,
  ToggleLeft,
  ToggleRight,
  FileText
} from 'lucide-react';

export const LawyerDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [cases, setCases] = useState([]);
  const [requests, setRequests] = useState([]);
  const [availability, setAvailability] = useState('ACCEPTING_CLIENTS');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadLawyerData = async () => {
      try {
        const [casesRes, reqRes] = await Promise.allSettled([
          caseService.getMyCases(0, 10),
          lawyerService.getMyReceivedRequests(0, 5)
        ]);

        if (casesRes.status === 'fulfilled' && casesRes.value?.success) {
          setCases(casesRes.value.data.content || []);
        }
        if (reqRes.status === 'fulfilled' && reqRes.value?.success) {
          setRequests(reqRes.value.data.content || []);
        }
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    loadLawyerData();
  }, []);

  const handleAvailabilityChange = async (newStatus) => {
    setAvailability(newStatus);
    try {
      await lawyerService.updateAvailability({
        status: newStatus,
        availableFrom: new Date().toISOString().split('T')[0],
        availableUntil: new Date(Date.now() + 365 * 86400000).toISOString().split('T')[0]
      });
    } catch (e) {
      console.error(e);
    }
  };

  const handleRespondRequest = async (requestId, status) => {
    try {
      const res = await lawyerService.respondToRequest(requestId, status, `Response from advocate: ${status}`);
      if (res?.success) {
        setRequests((prev) =>
          prev.map((r) => (r.id === requestId ? { ...r, status } : r))
        );
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
          {/* Header & Status Toggle */}
          <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Advocate Practice Desk</h1>
              <p style={{ fontSize: '0.9rem' }}>Welcome, {user?.name} | Active Chambers Workspace</p>
            </div>

            {/* Availability Status Switch */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', background: 'var(--bg-glass)', padding: '0.5rem 1rem', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border-color)' }}>
              <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>Practice Availability:</span>
              <select
                className="form-control"
                style={{ width: 'auto', padding: '0.35rem 0.75rem', fontSize: '0.85rem' }}
                value={availability}
                onChange={(e) => handleAvailabilityChange(e.target.value)}
              >
                <option value="ACCEPTING_CLIENTS">Accepting Clients</option>
                <option value="BUSY">Busy / Court Engaged</option>
                <option value="CONSULTATION_ONLY">Consultation Only</option>
                <option value="ON_LEAVE">On Leave</option>
              </select>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="grid-3" style={{ marginBottom: '2rem' }}>
            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Active Case Files</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--primary)', marginTop: '0.25rem' }}>
                {cases.length}
              </div>
            </div>

            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Pending Consultation Inquiries</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--accent-gold)', marginTop: '0.25rem' }}>
                {requests.filter((r) => r.status === 'PENDING').length}
              </div>
            </div>

            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Hearings This Week</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--warning)', marginTop: '0.25rem' }}>
                {cases.filter((c) => !!c.nextHearingDate).length}
              </div>
            </div>
          </div>

          {/* Pending Inquiries Section */}
          <div className="glass-card" style={{ marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <UserCheck size={18} color="var(--accent-gold)" /> Client Consultation Inquiries
            </h3>

            {requests.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No pending client requests.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {requests.map((r) => (
                  <div
                    key={r.id}
                    style={{
                      padding: '1rem',
                      borderRadius: 'var(--radius-md)',
                      background: 'var(--bg-glass)',
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
                        <strong style={{ fontSize: '0.95rem' }}>{r.clientName}</strong>
                        <Badge variant={r.status}>{r.status}</Badge>
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        <strong>Matter: </strong>{r.caseType} | <em>"{r.message}"</em>
                      </div>
                    </div>

                    {r.status === 'PENDING' && (
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button
                          onClick={() => handleRespondRequest(r.id, 'ACCEPTED')}
                          className="btn btn-success btn-sm"
                        >
                          <CheckCircle2 size={14} /> Accept
                        </button>
                        <button
                          onClick={() => handleRespondRequest(r.id, 'REJECTED')}
                          className="btn btn-danger btn-sm"
                        >
                          <XCircle size={14} /> Decline
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Active Case Files */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Briefcase size={18} color="var(--primary)" /> Active Client Matters
            </h3>

            {cases.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No case files assigned.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {cases.map((c) => (
                  <div
                    key={c.id}
                    onClick={() => navigate(`/cases/${c.id}`)}
                    className="glass-card glass-card-interactive"
                    style={{ padding: '1rem', background: 'var(--bg-glass)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.75rem' }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                        <strong style={{ fontSize: '0.95rem' }}>{c.title}</strong>
                        <Badge variant={c.status}>{c.status}</Badge>
                        <Badge variant={c.stage}>{c.stage}</Badge>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        Client: {c.clientName || 'N/A'} | CNR: {c.cnrNumber || 'Pending'}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      {c.nextHearingDate && (
                        <div style={{ fontSize: '0.8rem', color: 'var(--warning)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                          <Clock size={14} /> Next: {c.nextHearingDate}
                        </div>
                      )}
                      <ArrowRight size={16} color="var(--primary)" />
                    </div>
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
