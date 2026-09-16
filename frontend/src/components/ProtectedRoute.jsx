import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute = ({ allowedRoles = [] }) => {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) {
    return (
      <div style={{ minHeight: '60vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div className="glass-card" style={{ padding: '2rem', textAlign: 'center' }}>
          <div className="pulse-dot" style={{ width: '24px', height: '24px', margin: '0 auto 1rem', background: 'var(--primary)' }} />
          <p>Verifying secure session...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0) {
    const userRoles = user?.roles || [];
    const hasPermission = allowedRoles.some((role) =>
      userRoles.includes(role) || userRoles.includes(`ROLE_${role}`)
    );

    if (!hasPermission) {
      return (
        <div className="container" style={{ padding: '4rem 0', textAlign: 'center' }}>
          <div className="glass-card" style={{ maxWidth: '500px', margin: '0 auto', padding: '2.5rem' }}>
            <h2 style={{ color: 'var(--danger)', marginBottom: '1rem' }}>Access Restricted</h2>
            <p style={{ marginBottom: '1.5rem' }}>
              Your account does not have authorization to access this area.
            </p>
            <button onClick={() => window.history.back()} className="btn btn-secondary">
              Go Back
            </button>
          </div>
        </div>
      );
    }
  }

  return <Outlet />;
};
