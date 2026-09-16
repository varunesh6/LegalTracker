import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Scale, Lock, Mail, AlertCircle, ArrowRight, UserCheck, Shield, Briefcase, User } from 'lucide-react';

export const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { login, isAdmin, isOfficer, isLawyer } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const loggedUser = await login({ email: email.trim(), password });
      const roles = loggedUser?.roles || [];

      if (from) {
        navigate(from, { replace: true });
      } else if (roles.includes('ADMIN') || roles.includes('ROLE_ADMIN')) {
        navigate('/admin/dashboard');
      } else if (roles.includes('LEGAL_AID_OFFICER') || roles.includes('ROLE_LEGAL_AID_OFFICER')) {
        navigate('/officer/dashboard');
      } else if (roles.includes('LAWYER') || roles.includes('ROLE_LAWYER')) {
        navigate('/lawyer/dashboard');
      } else {
        navigate('/client/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickFill = (demoEmail) => {
    setEmail(demoEmail);
    setPassword('password123');
    setError('');
  };

  return (
    <div className="container" style={{ padding: '3.5rem 0', minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ maxWidth: '950px', width: '100%', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '2rem' }}>
        
        {/* Login Form Card */}
        <div className="glass-card" style={{ padding: '2.5rem 2rem' }}>
          <div style={{ marginBottom: '1.75rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <Scale size={24} color="var(--primary)" />
              <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 800, fontSize: '1.25rem' }}>
                LEGALTRACK
              </span>
            </div>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>Account Sign In</h2>
            <p style={{ fontSize: '0.875rem' }}>Enter your credentials to access your legal workspace.</p>
          </div>

          {error && (
            <div style={{ background: 'var(--danger-light)', border: '1px solid var(--danger)', padding: '0.75rem', borderRadius: 'var(--radius-md)', color: 'var(--danger)', fontSize: '0.85rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <AlertCircle size={16} />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <div style={{ position: 'relative' }}>
                <input
                  type="email"
                  className="form-control"
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">
                <span>Password</span>
                <a href="#forgot" style={{ fontSize: '0.75rem' }}>Forgot password?</a>
              </label>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary btn-lg" style={{ width: '100%', marginTop: '0.5rem' }}>
              <Lock size={16} />
              <span>{loading ? 'Authenticating...' : 'Sign In'}</span>
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            Don't have an account? <Link to="/register" style={{ fontWeight: 600 }}>Create an account</Link>
          </div>
        </div>

        {/* Demo Credentials Quick Fill Panel */}
        <div className="glass-card" style={{ padding: '2rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem' }}>
              <Shield size={20} color="var(--accent-gold)" />
              <h3 style={{ fontSize: '1.15rem' }}>Demo Accounts (Viva / Placement Demo)</h3>
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
              Click any profile below to auto-fill the login form (all default passwords are <code>password123</code>).
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              <button
                type="button"
                onClick={() => handleQuickFill('client@example.com')}
                className="btn btn-glass"
                style={{ justifyContent: 'space-between', padding: '0.6rem 0.85rem', textAlign: 'left' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                  <User size={16} color="var(--primary)" />
                  <div>
                    <strong style={{ fontSize: '0.85rem', display: 'block' }}>Ramesh Babu (Client)</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>client@example.com</span>
                  </div>
                </div>
                <span className="badge badge-primary">Client</span>
              </button>

              <button
                type="button"
                onClick={() => handleQuickFill('lawyer1@example.com')}
                className="btn btn-glass"
                style={{ justifyContent: 'space-between', padding: '0.6rem 0.85rem', textAlign: 'left' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                  <Briefcase size={16} color="var(--accent-gold)" />
                  <div>
                    <strong style={{ fontSize: '0.85rem', display: 'block' }}>Adv. Kumar S. (Senior Lawyer)</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>lawyer1@example.com</span>
                  </div>
                </div>
                <span className="badge badge-gold">Lawyer</span>
              </button>

              <button
                type="button"
                onClick={() => handleQuickFill('lawyer2@example.com')}
                className="btn btn-glass"
                style={{ justifyContent: 'space-between', padding: '0.6rem 0.85rem', textAlign: 'left' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                  <Briefcase size={16} color="var(--info)" />
                  <div>
                    <strong style={{ fontSize: '0.85rem', display: 'block' }}>Adv. Priya Lakshmi (Civil Lawyer)</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>lawyer2@example.com</span>
                  </div>
                </div>
                <span className="badge badge-info">Lawyer</span>
              </button>

              <button
                type="button"
                onClick={() => handleQuickFill('officer@example.com')}
                className="btn btn-glass"
                style={{ justifyContent: 'space-between', padding: '0.6rem 0.85rem', textAlign: 'left' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                  <UserCheck size={16} color="var(--purple)" />
                  <div>
                    <strong style={{ fontSize: '0.85rem', display: 'block' }}>K. Sundaram (Legal Aid Officer)</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>officer@example.com</span>
                  </div>
                </div>
                <span className="badge badge-purple">Officer</span>
              </button>

              <button
                type="button"
                onClick={() => handleQuickFill('admin@example.com')}
                className="btn btn-glass"
                style={{ justifyContent: 'space-between', padding: '0.6rem 0.85rem', textAlign: 'left' }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                  <Shield size={16} color="var(--danger)" />
                  <div>
                    <strong style={{ fontSize: '0.85rem', display: 'block' }}>System Administrator</strong>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>admin@example.com</span>
                  </div>
                </div>
                <span className="badge badge-danger">Admin</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
