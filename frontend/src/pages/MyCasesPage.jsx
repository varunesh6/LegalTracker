import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { caseService } from '../services/caseService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import { Briefcase, Calendar, Clock, ArrowRight, Search, Plus } from 'lucide-react';

export const MyCasesPage = () => {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    caseService.getMyCases(0, 50).then((res) => {
      if (res?.success) {
        setCases(res.data.content || []);
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
              <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Active Case Files</h1>
              <p style={{ fontSize: '0.9rem' }}>All active and disposed legal matters associated with your account.</p>
            </div>
          </div>

          <div className="glass-card">
            {loading ? (
              <p style={{ color: 'var(--text-muted)' }}>Loading cases...</p>
            ) : cases.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '3rem 0' }}>
                No active case files found.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {cases.map((c) => (
                  <div
                    key={c.id}
                    onClick={() => navigate(`/cases/${c.id}`)}
                    className="glass-card glass-card-interactive"
                    style={{ padding: '1.25rem', background: 'var(--bg-glass)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
                        <strong style={{ fontSize: '1.05rem' }}>{c.title}</strong>
                        <Badge variant={c.status}>{c.status}</Badge>
                        <Badge variant={c.stage}>{c.stage}</Badge>
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        Court: <strong>{c.courtName}</strong> | Case No: {c.caseNumber || 'N/A'}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', marginTop: '0.25rem' }}>
                        CNR: {c.cnrNumber || 'N/A'}
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                      {c.nextHearingDate && (
                        <div style={{ fontSize: '0.85rem', color: 'var(--warning)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                          <Calendar size={16} />
                          <span>Hearing: {c.nextHearingDate}</span>
                        </div>
                      )}
                      <button className="btn btn-secondary btn-sm">
                        <span>Workspace</span>
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
