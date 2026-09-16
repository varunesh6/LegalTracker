import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { legalAidService } from '../services/legalAidService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { ShieldAlert, Plus, CheckCircle2, Clock } from 'lucide-react';

export const LegalAidApplicationsPage = () => {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    legalAidService.getMyApplications(0, 50).then((res) => {
      if (res?.success) {
        setApps(res.data.content || []);
      }
    }).catch(console.error).finally(() => setLoading(false));
  }, []);

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>My Legal Aid Applications</h1>
              <p style={{ fontSize: '0.9rem' }}>Status of free statutory legal assistance requests submitted under NALSA.</p>
            </div>
            <Link to="/legal-aid" className="btn btn-primary btn-sm">
              <Plus size={16} /> New Application
            </Link>
          </div>

          <div className="glass-card">
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading applications...</p>
            ) : apps.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '3rem 0' }}>
                You have not submitted any legal aid applications yet. <Link to="/legal-aid">Apply for Free Legal Aid</Link>.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {apps.map((app) => (
                  <div
                    key={app.id}
                    style={{
                      padding: '1.25rem',
                      background: 'var(--bg-glass)',
                      borderRadius: 'var(--radius-lg)',
                      border: '1px solid var(--border-color)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                      <div>
                        <strong style={{ fontSize: '1.05rem' }}>{app.caseType}</strong>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                          App Number: {app.applicationNumber}
                        </div>
                      </div>
                      <Badge variant={app.status}>{app.status}</Badge>
                    </div>

                    <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                      <strong>Category: </strong>{app.selectedCategory} | <strong>Assigned Advocate: </strong>{app.assignedLawyerName || 'Under DLSA Review'}
                    </div>

                    <p style={{ fontSize: '0.85rem', color: 'var(--text-primary)', background: 'rgba(0,0,0,0.2)', padding: '0.75rem', borderRadius: 'var(--radius-md)' }}>
                      {app.matterDescription}
                    </p>
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
