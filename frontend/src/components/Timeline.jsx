import React from 'react';
import { Check, Clock, AlertCircle } from 'lucide-react';

export const Timeline = ({ timeline }) => {
  if (!timeline) return null;

  const { stageProgression = [], events = [] } = timeline;

  return (
    <div>
      {/* Visual Stepper */}
      <div className="glass-card" style={{ marginBottom: '1.5rem', overflowX: 'auto' }}>
        <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '0.5rem' }}>Court Procedural Stage</h4>
        <div className="timeline-stepper">
          {stageProgression.map((stage, idx) => (
            <div
              key={idx}
              className={`timeline-step ${stage.completed ? 'completed' : ''} ${stage.current ? 'current' : ''}`}
            >
              <div className="timeline-circle">
                {stage.completed && !stage.current ? <Check size={18} /> : idx + 1}
              </div>
              <span className="timeline-step-label">{stage.stageName}</span>
              {stage.date && <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>{stage.date}</span>}
            </div>
          ))}
        </div>
      </div>

      {/* Chronological Event History */}
      <div className="glass-card">
        <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '1.25rem' }}>Case Chronology & History</h4>
        {events.length === 0 ? (
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No events recorded for this case yet.</p>
        ) : (
          <div className="event-feed">
            {events.map((event) => (
              <div key={event.id} className="event-item">
                <div className="event-item-bullet" />
                <div className="glass-card" style={{ padding: '1rem', background: 'rgba(255, 255, 255, 0.02)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.5rem' }}>
                    <strong style={{ color: 'var(--text-primary)', fontSize: '0.9rem' }}>{event.eventTitle}</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                      <Clock size={12} />
                      {new Date(event.eventDate).toLocaleDateString([], { day: 'numeric', month: 'short', year: 'numeric' })}
                    </span>
                  </div>
                  <p style={{ fontSize: '0.85rem', marginTop: '0.35rem' }}>{event.eventDescription}</p>
                  {event.createdByName && (
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
                      Recorded by: {event.createdByName} ({event.source})
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
