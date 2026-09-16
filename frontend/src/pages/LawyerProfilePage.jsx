import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { lawyerService } from '../services/lawyerService';
import { chatService } from '../services/chatService';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/Badge';
import { Modal } from '../components/Modal';
import {
  MapPin,
  Award,
  BookOpen,
  Building,
  Globe,
  Calendar,
  ShieldCheck,
  MessageSquare,
  Send,
  AlertCircle,
  CheckCircle2
} from 'lucide-react';

export const LawyerProfilePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, isClient } = useAuth();

  const [lawyer, setLawyer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Consultation Request Modal
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [caseType, setCaseType] = useState('Civil Dispute');
  const [message, setMessage] = useState('');
  const [courtName, setCourtName] = useState('');
  const [submittingRequest, setSubmittingRequest] = useState(false);
  const [requestSuccess, setRequestSuccess] = useState(false);

  useEffect(() => {
    lawyerService.getLawyerById(id).then((res) => {
      if (res?.success) {
        setLawyer(res.data);
      } else {
        setError('Lawyer profile not found');
      }
    }).catch(() => {
      setError('Lawyer profile not found');
    }).finally(() => setLoading(false));
  }, [id]);

  const handleSendRequest = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setSubmittingRequest(true);
    try {
      const res = await lawyerService.sendRequest({
        lawyerId: Number(id),
        caseType,
        message,
        courtName
      });
      if (res?.success) {
        setRequestSuccess(true);
        setTimeout(() => {
          setShowRequestModal(false);
          setRequestSuccess(false);
        }, 2000);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit consultation request');
    } finally {
      setSubmittingRequest(false);
    }
  };

  const handleStartChat = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    try {
      const res = await chatService.getOrCreateConversation(Number(id));
      if (res?.success) {
        navigate('/chat');
      }
    } catch (e) {
      console.error(e);
      navigate('/chat');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '5rem 0' }}>
        <div className="pulse-dot" style={{ width: '24px', height: '24px', margin: '0 auto 1rem', background: 'var(--primary)' }} />
        <p>Loading advocate profile...</p>
      </div>
    );
  }

  if (error || !lawyer) {
    return (
      <div className="container" style={{ padding: '4rem 0', textAlign: 'center' }}>
        <div className="glass-card" style={{ maxWidth: '500px', margin: '0 auto' }}>
          <h3 style={{ color: 'var(--danger)', marginBottom: '0.5rem' }}>Profile Unavailable</h3>
          <p style={{ marginBottom: '1.5rem' }}>{error || 'This lawyer profile does not exist.'}</p>
          <button onClick={() => navigate('/lawyers')} className="btn btn-primary">
            Back to Directory
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2.5rem 0' }}>
      {/* Top Banner Card */}
      <div className="glass-card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1.5rem' }}>
          <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
            <div
              style={{
                width: '80px',
                height: '80px',
                borderRadius: 'var(--radius-xl)',
                background: 'linear-gradient(135deg, var(--primary), var(--purple))',
                color: '#fff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 800,
                fontSize: '2rem'
              }}
            >
              {lawyer.name ? lawyer.name.charAt(0).toUpperCase() : 'A'}
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.25rem' }}>
                <h1 style={{ fontSize: '1.75rem' }}>{lawyer.name}</h1>
                {lawyer.verified && (
                  <Badge variant="VERIFIED">
                    <ShieldCheck size={14} /> Verified Counsel
                  </Badge>
                )}
              </div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '0.5rem' }}>
                Bar Council Enrollment: <strong style={{ color: 'var(--text-primary)' }}>{lawyer.barRegistrationNumber}</strong> ({lawyer.enrollmentYear || 'N/A'})
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  <MapPin size={14} color="var(--primary)" /> {lawyer.districtName}, {lawyer.stateName}
                </span>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  <Award size={14} color="var(--accent-gold)" /> {lawyer.experienceYears} Years Practice
                </span>
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            {isClient() && (
              <>
                <button onClick={handleStartChat} className="btn btn-secondary">
                  <MessageSquare size={16} />
                  <span>Send Message</span>
                </button>
                <button onClick={() => setShowRequestModal(true)} className="btn btn-primary">
                  <Calendar size={16} />
                  <span>Request Consultation</span>
                </button>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Main Details Grid */}
      <div className="grid-3" style={{ gap: '2rem' }}>
        {/* Left 2 cols: Bio, Practice Courts, Specializations */}
        <div style={{ gridColumn: 'span 2', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BookOpen size={18} color="var(--primary)" /> Professional Background &amp; Bio
            </h3>
            <p style={{ lineHeight: '1.7', fontSize: '0.95rem' }}>
              {lawyer.bio || 'Advocate practicing across trial and appellate courts, specializing in comprehensive case preparation and representation.'}
            </p>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Award size={18} color="var(--accent-gold)" /> Practice Areas &amp; Specializations
            </h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
              {lawyer.specializations && lawyer.specializations.length > 0 ? (
                lawyer.specializations.map((spec, i) => (
                  <span
                    key={i}
                    style={{
                      background: 'var(--primary-light)',
                      color: 'var(--primary)',
                      border: '1px solid rgba(59, 130, 246, 0.3)',
                      padding: '0.4rem 0.85rem',
                      borderRadius: 'var(--radius-full)',
                      fontSize: '0.85rem',
                      fontWeight: 600
                    }}
                  >
                    {spec}
                  </span>
                ))
              ) : (
                <p style={{ color: 'var(--text-muted)' }}>General Legal Practice</p>
              )}
            </div>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.15rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Building size={18} color="var(--purple)" /> Practicing Courts
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {lawyer.courts && lawyer.courts.length > 0 ? (
                lawyer.courts.map((c) => (
                  <div key={c.id} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.5rem', background: 'var(--bg-glass)', borderRadius: 'var(--radius-md)' }}>
                    <Building size={16} color="var(--text-muted)" />
                    <span style={{ fontSize: '0.9rem' }}>{c.name} ({c.districtName})</span>
                  </div>
                ))
              ) : (
                <p style={{ color: 'var(--text-muted)' }}>District &amp; Sessions Courts</p>
              )}
            </div>
          </div>
        </div>

        {/* Right 1 col: Availability & Contact Info */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Availability Status</h3>
            <div style={{ marginBottom: '1rem' }}>
              <Badge variant={lawyer.availabilityStatus || 'ACCEPTING_CLIENTS'} pulse={true}>
                {lawyer.availabilityStatus ? lawyer.availabilityStatus.replace('_', ' ') : 'Accepting Clients'}
              </Badge>
            </div>
            {lawyer.officeAddress && (
              <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                <strong style={{ color: 'var(--text-primary)', display: 'block' }}>Chambers / Office:</strong>
                {lawyer.officeAddress}
              </div>
            )}
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Globe size={18} color="var(--info)" /> Languages Spoken
            </h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
              {lawyer.languages && lawyer.languages.length > 0 ? (
                lawyer.languages.map((lang, i) => (
                  <span key={i} style={{ fontSize: '0.8rem', background: 'var(--bg-glass)', padding: '0.25rem 0.6rem', borderRadius: 'var(--radius-sm)' }}>
                    {lang}
                  </span>
                ))
              ) : (
                <span>English, Tamil</span>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Consultation Request Modal */}
      <Modal
        isOpen={showRequestModal}
        onClose={() => setShowRequestModal(false)}
        title={`Request Consultation with ${lawyer.name}`}
      >
        {requestSuccess ? (
          <div style={{ textAlign: 'center', padding: '2rem 0' }}>
            <CheckCircle2 size={48} color="var(--success)" style={{ margin: '0 auto 1rem' }} />
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>Consultation Request Sent!</h3>
            <p>Advocate {lawyer.name} will review your inquiry and respond shortly.</p>
          </div>
        ) : (
          <form onSubmit={handleSendRequest}>
            <div className="form-group">
              <label className="form-label">Matter / Case Nature</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. Property Partition Suit / Commercial Contract Dispute"
                value={caseType}
                onChange={(e) => setCaseType(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Jurisdiction Court (Optional)</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. City Civil Court, Chennai"
                value={courtName}
                onChange={(e) => setCourtName(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Brief Matter Description &amp; Questions</label>
              <textarea
                className="form-control"
                rows="4"
                placeholder="Briefly describe your legal assistance requirement..."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                required
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
              <button type="button" onClick={() => setShowRequestModal(false)} className="btn btn-secondary">
                Cancel
              </button>
              <button type="submit" disabled={submittingRequest} className="btn btn-primary">
                <Send size={16} />
                <span>{submittingRequest ? 'Sending...' : 'Submit Request'}</span>
              </button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};
