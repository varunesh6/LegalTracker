import React from 'react';
import { AlertTriangle, Clock, FileWarning, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const AttentionBanner = ({ items = [] }) => {
  if (!items || items.length === 0) return null;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.5rem' }}>
      {items.map((item) => {
        const isUrgent = item.severity === 'HIGH' || item.severity === 'CRITICAL';
        return (
          <div
            key={item.id}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '1rem 1.25rem',
              borderRadius: 'var(--radius-lg)',
              background: isUrgent ? 'rgba(239, 68, 68, 0.1)' : 'rgba(249, 115, 22, 0.1)',
              border: `1px solid ${isUrgent ? 'rgba(239, 68, 68, 0.3)' : 'rgba(249, 115, 22, 0.3)'}`,
              gap: '1rem',
              flexWrap: 'wrap'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <div
                style={{
                  padding: '0.5rem',
                  borderRadius: 'var(--radius-md)',
                  background: isUrgent ? 'rgba(239, 68, 68, 0.2)' : 'rgba(249, 115, 22, 0.2)',
                  color: isUrgent ? 'var(--danger)' : 'var(--warning)'
                }}
              >
                {item.type === 'HEARING_OVERDUE' ? <Clock size={20} /> : <AlertTriangle size={20} />}
              </div>
              <div>
                <strong style={{ color: 'var(--text-primary)', fontSize: '0.9rem', display: 'block' }}>
                  {item.title}
                </strong>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  {item.description}
                </span>
              </div>
            </div>

            {item.actionUrl && (
              <Link
                to={item.actionUrl}
                className={`btn btn-sm ${isUrgent ? 'btn-danger' : 'btn-gold'}`}
                style={{ fontSize: '0.8rem', padding: '0.35rem 0.75rem' }}
              >
                <span>Take Action</span>
                <ArrowRight size={14} />
              </Link>
            )}
          </div>
        );
      })}
    </div>
  );
};
