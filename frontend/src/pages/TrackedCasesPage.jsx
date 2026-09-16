import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { trackedCaseService } from '../services/trackedCaseService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { BookmarkCheck, Trash2, Bell, BellOff, ArrowRight, Search } from 'lucide-react';

export const TrackedCasesPage = () => {
  const [trackedCases, setTrackedCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchTracked = async () => {
    try {
      const res = await trackedCaseService.getMyTrackedCases('', 0, 50);
      if (res?.success) {
        setTrackedCases(res.data.content || []);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTracked();
  }, []);

  const handleUntrack = async (id, e) => {
    e.stopPropagation();
    if (!window.confirm('Stop tracking this case?')) return;

    try {
      await trackedCaseService.stopTracking(id);
      setTrackedCases((prev) => prev.filter((tc) => tc.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  const handleToggleNotifications = async (id, currentVal, e) => {
    e.stopPropagation();
    try {
      await trackedCaseService.toggleNotifications(id, !currentVal);
      setTrackedCases((prev) =>
        prev.map((tc) => (tc.id === id ? { ...tc, notificationsEnabled: !currentVal } : tc))
      );
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Tracked Cases</h1>
              <p style={{ fontSize: '0.9rem' }}>Monitored court matters with automatic hearing countdown alerts.</p>
            </div>
            <Link to="/track" className="btn btn-primary btn-sm">
              <Search size={16} /> Track Another Case
            </Link>
          </div>

          <div className="glass-card">
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading tracked cases...</p>
            ) : trackedCases.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '3rem 0' }}>
                You are not tracking any cases yet. <Link to="/track">Track a case by CNR</Link>.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {trackedCases.map((tc) => (
                  <div
                    key={tc.id}
                    onClick={() => tc.caseFile?.id && navigate(`/cases/${tc.caseFile.id}`)}
                    className="glass-card glass-card-interactive"
                    style={{ padding: '1.25rem', background: 'var(--bg-glass)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
                        <strong style={{ fontSize: '1.05rem' }}>{tc.nickname || tc.caseFile?.title}</strong>
                        <Badge variant={tc.caseFile?.status || 'PENDING'}>{tc.caseFile?.status || 'ACTIVE'}</Badge>
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        CNR: {tc.caseFile?.cnrNumber} | Court: {tc.caseFile?.courtName || 'District Court'}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <button
                        onClick={(e) => handleToggleNotifications(tc.id, tc.notificationsEnabled, e)}
                        className={`btn btn-icon ${tc.notificationsEnabled ? 'btn-glass' : 'btn-secondary'}`}
                        title={tc.notificationsEnabled ? 'Notifications Enabled' : 'Notifications Muted'}
                      >
                        {tc.notificationsEnabled ? <Bell size={16} color="var(--primary)" /> : <BellOff size={16} />}
                      </button>

                      <button
                        onClick={(e) => handleUntrack(tc.id, e)}
                        className="btn btn-glass btn-icon"
                        style={{ color: 'var(--danger)' }}
                        title="Untrack Case"
                      >
                        <Trash2 size={16} />
                      </button>

                      <button className="btn btn-secondary btn-sm">
                        <span>Details</span>
                        <ArrowRight size={14} />
                      </button>
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
