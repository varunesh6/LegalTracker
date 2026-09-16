import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Scale,
  Search,
  Users,
  ShieldCheck,
  Calendar,
  FileText,
  Clock,
  ArrowRight,
  Sparkles,
  BookOpen,
  CheckCircle2,
  Lock
} from 'lucide-react';

export const Home = () => {
  const [cnrSearch, setCnrSearch] = useState('');
  const navigate = useNavigate();

  const handleQuickTrack = (e) => {
    e.preventDefault();
    if (cnrSearch.trim()) {
      navigate(`/track?cnr=${encodeURIComponent(cnrSearch.trim())}`);
    }
  };

  return (
    <div>
      {/* Hero Section */}
      <section className="hero-section">
        <div className="container">
          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
              background: 'var(--bg-glass)',
              border: '1px solid var(--border-glass)',
              padding: '0.4rem 1rem',
              borderRadius: 'var(--radius-full)',
              marginBottom: '1.5rem',
              fontSize: '0.85rem'
            }}
          >
            <Sparkles size={16} color="var(--accent-gold)" />
            <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>
              Academic Full-Stack Case Tracking & Legal Assistance Architecture
            </span>
          </div>

          <h1 className="hero-title">
            One Place to Track Cases,<br />Discover Lawyers &amp; Seek Legal Aid.
          </h1>

          <p className="hero-subtitle">
            A centralized legal workspace built for citizens, advocates, and authorities featuring universal CNR case tracking, chronological case diary event sourcing, hearing countdowns, and automated legal-aid eligibility rule evaluation.
          </p>

          {/* Live Quick Tracking Bar */}
          <form onSubmit={handleQuickTrack} style={{ maxWidth: '650px', margin: '0 auto 2.5rem' }}>
            <div
              style={{
                display: 'flex',
                background: 'rgba(17, 24, 39, 0.85)',
                backdropFilter: 'blur(16px)',
                border: '1px solid var(--border-glass)',
                borderRadius: 'var(--radius-full)',
                padding: '0.4rem 0.5rem 0.4rem 1.25rem',
                boxShadow: 'var(--shadow-xl), 0 0 25px rgba(59, 130, 246, 0.2)'
              }}
            >
              <input
                type="text"
                value={cnrSearch}
                onChange={(e) => setCnrSearch(e.target.value)}
                placeholder="Enter 16-character CNR number (e.g. TNCH010012342024)..."
                style={{
                  flex: 1,
                  background: 'transparent',
                  border: 'none',
                  outline: 'none',
                  color: 'var(--text-primary)',
                  fontSize: '0.95rem'
                }}
              />
              <button type="submit" className="btn btn-primary" style={{ borderRadius: 'var(--radius-full)', padding: '0.65rem 1.5rem' }}>
                <Search size={16} />
                <span>Track Now</span>
              </button>
            </div>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '0.75rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              <span>Try Demo CNRs:</span>
              <button
                type="button"
                onClick={() => { setCnrSearch('TNCH010012342024'); navigate('/track?cnr=TNCH010012342024'); }}
                style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', textDecoration: 'underline' }}
              >
                TNCH010012342024
              </button>
              <button
                type="button"
                onClick={() => { setCnrSearch('TNMDU010098762024'); navigate('/track?cnr=TNMDU010098762024'); }}
                style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', textDecoration: 'underline' }}
              >
                TNMDU010098762024
              </button>
            </div>
          </form>

          {/* Quick Pillar Action Cards */}
          <div className="grid-3" style={{ marginTop: '3.5rem' }}>
            <div className="glass-card glass-card-interactive" onClick={() => navigate('/track')}>
              <div style={{ width: '48px', height: '48px', borderRadius: 'var(--radius-md)', background: 'var(--primary-light)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--primary)', marginBottom: '1.25rem' }}>
                <Search size={24} />
              </div>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Universal Case Tracker</h3>
              <p style={{ fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                Track cases via 16-character CNR, Case Number & Court, or Police Station & FIR number with automatic timeline progression.
              </p>
              <span style={{ color: 'var(--primary)', fontWeight: 600, fontSize: '0.875rem', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                Explore Case Tracker <ArrowRight size={14} />
              </span>
            </div>

            <div className="glass-card glass-card-interactive" onClick={() => navigate('/lawyers')}>
              <div style={{ width: '48px', height: '48px', borderRadius: 'var(--radius-md)', background: 'var(--accent-gold-light)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--accent-gold)', marginBottom: '1.25rem' }}>
                <Users size={24} />
              </div>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Advocate Discovery</h3>
              <p style={{ fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                Filter verified advocates by district, court complex, specialization, languages, and real-time consultation availability.
              </p>
              <span style={{ color: 'var(--accent-gold)', fontWeight: 600, fontSize: '0.875rem', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                Find Advocates <ArrowRight size={14} />
              </span>
            </div>

            <div className="glass-card glass-card-interactive" onClick={() => navigate('/legal-aid')}>
              <div style={{ width: '48px', height: '48px', borderRadius: 'var(--radius-md)', background: 'var(--purple-light)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--purple)', marginBottom: '1.25rem' }}>
                <ShieldCheck size={24} />
              </div>
              <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Free Legal Aid Portal</h3>
              <p style={{ fontSize: '0.9rem', marginBottom: '1.25rem' }}>
                Instant eligibility evaluation under NALSA / TNSLSA criteria for women, SC/ST, low-income citizens with automated officer review workflows.
              </p>
              <span style={{ color: 'var(--purple)', fontWeight: 600, fontSize: '0.875rem', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                Calculate Eligibility <ArrowRight size={14} />
              </span>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Counter Bar */}
      <section style={{ padding: '2rem 0', borderTop: '1px solid var(--border-color)', borderBottom: '1px solid var(--border-color)', background: 'rgba(17, 24, 39, 0.4)' }}>
        <div className="container">
          <div className="hero-stats" style={{ margin: 0 }}>
            <div className="stat-card">
              <div className="stat-value">16</div>
              <div className="stat-label">Digit Standard CNR Support</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">38+</div>
              <div className="stat-label">Tamil Nadu Districts &amp; Courts</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">100%</div>
              <div className="stat-label">Statutory Legal Aid Coverage</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">24/7</div>
              <div className="stat-label">Case Diary &amp; Attention Monitoring</div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Showcase Grid */}
      <section style={{ padding: '5rem 0' }}>
        <div className="container">
          <div style={{ textAlign: 'center', maxWidth: '700px', margin: '0 auto 3.5rem' }}>
            <h2 style={{ fontSize: '2.2rem', marginBottom: '1rem' }}>Built for the Indian Legal Workflow</h2>
            <p>LEGALTRACK bridges the gap between citizens, advocates, and legal authorities through structured digital case workspaces.</p>
          </div>

          <div className="grid-2" style={{ gap: '2rem' }}>
            <div className="glass-card" style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start' }}>
              <div style={{ padding: '0.75rem', background: 'var(--primary-light)', borderRadius: 'var(--radius-md)', color: 'var(--primary)' }}>
                <BookOpen size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Chronological Case Diary</h4>
                <p style={{ fontSize: '0.875rem' }}>
                  Every hearing, order, client note, and procedural milestone is immutably timestamped in a shared or private case diary.
                </p>
              </div>
            </div>

            <div className="glass-card" style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start' }}>
              <div style={{ padding: '0.75rem', background: 'var(--danger-light)', borderRadius: 'var(--radius-md)', color: 'var(--danger)' }}>
                <Clock size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Rule Engine Attention Alerts</h4>
                <p style={{ fontSize: '0.875rem' }}>
                  Automated background checks flag upcoming hearings within 7/3/1 days, past hearings lacking orders, and pending submissions.
                </p>
              </div>
            </div>

            <div className="glass-card" style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start' }}>
              <div style={{ padding: '0.75rem', background: 'var(--success-light)', borderRadius: 'var(--radius-md)', color: 'var(--success)' }}>
                <Lock size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Granular Document Versioning</h4>
                <p style={{ fontSize: '0.875rem' }}>
                  Secure plaining, affidavit, and vakalatnama uploads with MD5 validation and category-specific lawyer/client visibility controls.
                </p>
              </div>
            </div>

            <div className="glass-card" style={{ display: 'flex', gap: '1.25rem', alignItems: 'flex-start' }}>
              <div style={{ padding: '0.75rem', background: 'var(--purple-light)', borderRadius: 'var(--radius-md)', color: 'var(--purple)' }}>
                <Users size={24} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.1rem', marginBottom: '0.4rem' }}>Direct 1-to-1 Client-Lawyer Chat</h4>
                <p style={{ fontSize: '0.875rem' }}>
                  Integrated messaging thread with unread count badges and case context linkage for seamless client communication.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
