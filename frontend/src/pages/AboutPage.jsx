import React from 'react';
import { Scale, CheckCircle2, ShieldCheck, Database, Server, Layout, Lock, Code2, Users, FileText, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const AboutPage = () => {
  return (
    <div className="container" style={{ padding: '3rem 0' }}>
      {/* Title */}
      <div style={{ textAlign: 'center', maxWidth: '800px', margin: '0 auto 3.5rem' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'var(--primary-light)', color: 'var(--primary)', padding: '0.35rem 0.85rem', borderRadius: 'var(--radius-full)', fontSize: '0.8rem', fontWeight: 700, textTransform: 'uppercase', marginBottom: '1rem' }}>
          Academic Project Guide &amp; Viva Blueprint
        </div>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>LEGALTRACK Platform Architecture</h1>
        <p style={{ fontSize: '1.05rem', lineHeight: '1.7' }}>
          An integrated full-stack case management, advocate discovery, and statutory legal-aid portal engineered using Spring Boot 3, React 18, and MySQL 8.
        </p>
      </div>

      {/* Core Technical Highlights */}
      <div className="grid-3" style={{ gap: '1.5rem', marginBottom: '3.5rem' }}>
        <div className="glass-card">
          <div style={{ padding: '0.75rem', width: 'fit-content', background: 'var(--primary-light)', color: 'var(--primary)', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>
            <Server size={24} />
          </div>
          <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Backend Layer</h3>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
            <li>• Java 17 + Spring Boot 3.2.4</li>
            <li>• Spring Security 6 with JWT Bearer &amp; RBAC</li>
            <li>• 41 JPA Entities &amp; 40 Spring Data Repositories</li>
            <li>• WebSocket STOMP for Real-Time 1-to-1 Chat</li>
            <li>• Automated 7/3/1 Day Hearing Schedulers</li>
          </ul>
        </div>

        <div className="glass-card">
          <div style={{ padding: '0.75rem', width: 'fit-content', background: 'var(--accent-gold-light)', color: 'var(--accent-gold)', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>
            <Layout size={24} />
          </div>
          <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Frontend Layer</h3>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
            <li>• React 18 SPA + Vite build tool</li>
            <li>• Pure Vanilla CSS Glassmorphism Design System</li>
            <li>• React Router v6 with Role-Guarded ProtectedRoutes</li>
            <li>• Interactive Procedural Timeline &amp; Event Feed</li>
            <li>• Instant Demo Credential Switcher for Viva</li>
          </ul>
        </div>

        <div className="glass-card">
          <div style={{ padding: '0.75rem', width: 'fit-content', background: 'var(--purple-light)', color: 'var(--purple)', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>
            <Database size={24} />
          </div>
          <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Domain &amp; Persistence</h3>
          <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
            <li>• Programmatic <code>DataInitializer</code> Seed Engine</li>
            <li>• Strict DTO &amp; Entity Isolation (No separate folder)</li>
            <li>• MD5 Hash File Storage &amp; Category Versioning</li>
            <li>• Case Attention Engine with Rule Evaluation</li>
            <li>• Full Audit Trail Logging for Compliance</li>
          </ul>
        </div>
      </div>

      {/* Problem Statement & Solution */}
      <div className="glass-card" style={{ marginBottom: '3.5rem', padding: '2.5rem' }}>
        <h2 style={{ fontSize: '1.6rem', marginBottom: '1.25rem' }}>Problem Statement &amp; Solution</h2>
        <div className="grid-2" style={{ gap: '2rem' }}>
          <div>
            <h4 style={{ color: 'var(--danger)', fontSize: '1rem', marginBottom: '0.75rem' }}>Traditional Challenges in Case Tracking</h4>
            <ul style={{ paddingLeft: '1.25rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9rem' }}>
              <li>Clients struggle to track upcoming hearings and case progression across disparate court systems.</li>
              <li>Difficulty discovering verified advocates practicing in specific court complexes.</li>
              <li>Underprivileged citizens are unaware of statutory legal-aid schemes and eligibility.</li>
              <li>Lack of a centralized chronological case diary and secure document repository.</li>
            </ul>
          </div>

          <div>
            <h4 style={{ color: 'var(--success)', fontSize: '1rem', marginBottom: '0.75rem' }}>LEGALTRACK Engineered Solution</h4>
            <ul style={{ paddingLeft: '1.25rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9rem' }}>
              <li>Universal tracking via 16-digit CNR, Case Number, and Police Station FIR numbers.</li>
              <li>Advocate discovery filtered by Tamil Nadu districts, courts, and real-time availability.</li>
              <li>Automated 3-step legal aid calculator evaluating statutory Section 12 criteria.</li>
              <li>Integrated workspace featuring Case Attention flags, versioned documents, and chat.</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Demo Credentials Table */}
      <div className="glass-card" style={{ padding: '2rem' }}>
        <h3 style={{ fontSize: '1.3rem', marginBottom: '1rem' }}>Placement &amp; Viva Demo Credentials</h3>
        <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
          Use these pre-seeded accounts to demonstrate role-based workflows during placement interviews and viva examinations. All passwords are <code>password123</code>.
        </p>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Role</th>
                <th>Email</th>
                <th>Name / Description</th>
                <th>Key Workflow to Showcase</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><span className="badge badge-primary">Client</span></td>
                <td><code>client@example.com</code></td>
                <td>Ramesh Babu (Citizen)</td>
                <td>Track cases by CNR, request consultations, view case diary &amp; hearings.</td>
              </tr>
              <tr>
                <td><span className="badge badge-gold">Lawyer 1</span></td>
                <td><code>lawyer1@example.com</code></td>
                <td>Adv. Kumar S. (Senior Advocate)</td>
                <td>Manage active cases, add diary entries, schedule hearings, upload orders.</td>
              </tr>
              <tr>
                <td><span className="badge badge-info">Lawyer 2</span></td>
                <td><code>lawyer2@example.com</code></td>
                <td>Adv. Priya Lakshmi</td>
                <td>Manage legal-aid assignments, toggle availability, client chat.</td>
              </tr>
              <tr>
                <td><span className="badge badge-purple">Officer</span></td>
                <td><code>officer@example.com</code></td>
                <td>K. Sundaram (Legal Aid Officer)</td>
                <td>Review income certificates, approve legal aid applications, assign panel lawyers.</td>
              </tr>
              <tr>
                <td><span className="badge badge-danger">Admin</span></td>
                <td><code>admin@example.com</code></td>
                <td>System Administrator</td>
                <td>Monitor platform statistics, verify bar credentials, view court sync logs &amp; audit trails.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
