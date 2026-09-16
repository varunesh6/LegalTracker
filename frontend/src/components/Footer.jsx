import React from 'react';
import { Link } from 'react-router-dom';
import { Scale, ShieldCheck, ExternalLink, Heart } from 'lucide-react';

export const Footer = () => {
  return (
    <footer style={{ borderTop: '1px solid var(--border-color)', background: 'rgba(11, 15, 25, 0.9)', padding: '3.5rem 0 2rem', marginTop: 'auto' }}>
      <div className="container">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '2.5rem', marginBottom: '2.5rem' }}>
          
          {/* Brand Col */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <Scale size={24} color="var(--primary)" />
              <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 800, fontSize: '1.2rem', letterSpacing: '-0.5px' }}>
                LEGALTRACK
              </span>
            </div>
            <p style={{ fontSize: '0.85rem', lineHeight: '1.6', marginBottom: '1rem' }}>
              One place to discover legal assistance, manage case information and track cases with chronological case diary and hearing countdowns.
            </p>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', background: 'var(--bg-glass)', padding: '0.35rem 0.75rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--border-color)', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              <ShieldCheck size={14} color="var(--success)" />
              <span>Academic Capstone Architecture</span>
            </div>
          </div>

          {/* Quick Nav */}
          <div>
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem', color: 'var(--text-primary)' }}>Portals & Features</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.875rem' }}>
              <li><Link to="/track" style={{ color: 'var(--text-secondary)' }}>Universal Case Tracker</Link></li>
              <li><Link to="/lawyers" style={{ color: 'var(--text-secondary)' }}>Advocate Discovery Directory</Link></li>
              <li><Link to="/legal-aid" style={{ color: 'var(--text-secondary)' }}>Free Legal Aid Calculator</Link></li>
              <li><Link to="/login" style={{ color: 'var(--text-secondary)' }}>Client & Lawyer Portal</Link></li>
              <li><Link to="/about" style={{ color: 'var(--text-secondary)' }}>Viva Demonstration Guide</Link></li>
            </ul>
          </div>

          {/* External Legal Resources */}
          <div>
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem', color: 'var(--text-primary)' }}>Legal References</h4>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.875rem' }}>
              <li>
                <a href="https://ecourts.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--text-secondary)', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  eCourts Services <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://nalsa.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--text-secondary)', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  NALSA Legal Services <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://main.sci.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--text-secondary)', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  Supreme Court of India <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://hcmadras.tn.gov.in" target="_blank" rel="noreferrer" style={{ color: 'var(--text-secondary)', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  Madras High Court <ExternalLink size={12} />
                </a>
              </li>
            </ul>
          </div>

          {/* Academic Notice */}
          <div>
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '1rem', color: 'var(--text-primary)' }}>Project Notice</h4>
            <p style={{ fontSize: '0.8rem', lineHeight: '1.5', color: 'var(--text-muted)' }}>
              Built as an academic full-stack prototype demonstrating secure case diary event sourcing, role isolation, and real-time client-lawyer collaboration.
            </p>
          </div>
        </div>

        {/* Bottom Bar */}
        <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <div>
            &copy; {new Date().getFullYear()} LEGALTRACK. All rights reserved.
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            Engineered with Spring Boot 3 & React
          </div>
        </div>
      </div>
    </footer>
  );
};
