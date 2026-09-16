import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { caseService } from '../services/caseService';
import { trackedCaseService } from '../services/trackedCaseService';
import { lawyerService } from '../services/lawyerService';
import { legalAidService } from '../services/legalAidService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { AttentionBanner } from '../components/AttentionBanner';
import {
  Briefcase,
  BookmarkCheck,
  Calendar,
  Clock,
  ArrowRight,
  Search,
  UserCheck,
  ShieldAlert,
  Plus
} from 'lucide-react';

export const ClientDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [myCases, setMyCases] = useState([]);
  const [trackedCases, setTrackedCases] = useState([]);
  const [requests, setRequests] = useState([]);
  const [legalAidApps, setLegalAidApps] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const [casesRes, trackedRes, reqRes, aidRes] = await Promise.allSettled([
          caseService.getMyCases(0, 5),
          trackedCaseService.getMyTrackedCases('', 0, 5),
          lawyerService.getMySentRequests(0, 5),
          legalAidService.getMyApplications(0, 5)
        ]);

        if (casesRes.status === 'fulfilled' && casesRes.value?.success) {
          setMyCases(casesRes.value.data.content || []);
        }
        if (trackedRes.status === 'fulfilled' && trackedRes.value?.success) {
          setTrackedCases(trackedRes.value.data.content || []);
        }
        if (reqRes.status === 'fulfilled' && reqRes.value?.success) {
          setRequests(reqRes.value.data.content || []);
        }
        if (aidRes.status === 'fulfilled' && aidRes.value?.success) {
          setLegalAidApps(aidRes.value.data.content || []);
        }
      } catch (e) {
        console.error(e);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          {/* Welcome Header */}
          <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Welcome back, {user?.name}!</h1>
              <p style={{ fontSize: '0.9rem' }}>Citizen Legal Workspace &amp; Case Tracking Hub</p>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <Link to="/track" className="btn btn-primary btn-sm">
                <Search size={16} /> Track New Case
              </Link>
              <Link to="/lawyers" className="btn btn-secondary btn-sm">
                <UserCheck size={16} /> Find Advocate
              </Link>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="grid-4" style={{ marginBottom: '2rem' }}>
            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>My Legal Cases</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--primary)', marginTop: '0.25rem' }}>
                {myCases.length}
              </div>
            </div>

            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Tracked Cases</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--accent-gold)', marginTop: '0.25rem' }}>
                {trackedCases.length}
              </div>
            </div>

            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Consultation Requests</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--info)', marginTop: '0.25rem' }}>
                {requests.length}
              </div>
            </div>

            <div className="glass-card">
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Legal Aid Applications</span>
              <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--purple)', marginTop: '0.25rem' }}>
                {legalAidApps.length}
              </div>
            </div>
          </div>

          {/* Active Cases Section */}
          <div className="glass-card" style={{ marginBottom: '2rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <h3 style={{ fontSize: '1.15rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Briefcase size={18} color="var(--primary)" /> My Active Case Files
              </h3>
            </div>

            {loading ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Loading case files...</p>
            ) : myCases.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                You have no active case representations yet. You can track a case or consult an advocate.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {myCases.map((c) => (
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
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        CNR: {c.cnrNumber || 'N/A'} | Court: {c.courtName || 'District Court'}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      {c.nextHearingDate && (
                        <div style={{ fontSize: '0.8rem', color: 'var(--warning)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                          <Calendar size={14} />
                          <span>Hearing: {c.nextHearingDate}</span>
                        </div>
                      )}
                      <ArrowRight size={16} color="var(--primary)" />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Tracked Cases Feed */}
          <div className="glass-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <h3 style={{ fontSize: '1.15rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <BookmarkCheck size={18} color="var(--accent-gold)" /> Tracked Case Feed
              </h3>
            </div>

            {trackedCases.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                No tracked cases. Use the <Link to="/track">Universal Tracker</Link> to monitor any court case with automatic hearing alerts.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {trackedCases.map((tc) => (
                  <div
                    key={tc.id}
                    onClick={() => tc.caseFile?.id && navigate(`/cases/${tc.caseFile.id}`)}
                    className="glass-card glass-card-interactive"
                    style={{ padding: '1rem', background: 'var(--bg-glass)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                  >
                    <div>
                      <strong style={{ fontSize: '0.95rem', display: 'block' }}>{tc.nickname || tc.caseFile?.title}</strong>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        CNR: {tc.caseFile?.cnrNumber}
                      </span>
                    </div>
                    <Badge variant={tc.caseFile?.status || 'PENDING'}>{tc.caseFile?.status || 'ACTIVE'}</Badge>
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
