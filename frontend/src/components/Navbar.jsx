import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useNotifications } from '../context/NotificationContext';
import {
  Scale,
  Search,
  Users,
  FileText,
  ShieldAlert,
  Bell,
  MessageSquare,
  LogOut,
  User as UserIcon,
  ChevronDown,
  Menu,
  X,
  CheckCheck
} from 'lucide-react';

export const Navbar = () => {
  const { user, isAuthenticated, logout, isClient, isLawyer, isOfficer, isAdmin } = useAuth();
  const { unreadCount, notifications, fetchNotifications, markAsRead, markAllAsRead } = useNotifications();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const notifRef = useRef(null);
  const userRef = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();

  // Close popovers on click outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
      if (userRef.current && !userRef.current.contains(event.target)) {
        setShowUserMenu(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleNotificationClick = () => {
    if (!showNotifications) {
      fetchNotifications();
    }
    setShowNotifications(!showNotifications);
  };

  const getDashboardLink = () => {
    if (isAdmin()) return '/admin/dashboard';
    if (isOfficer()) return '/officer/dashboard';
    if (isLawyer()) return '/lawyer/dashboard';
    return '/client/dashboard';
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        {/* Brand */}
        <Link to="/" className="brand-logo">
          <Scale size={28} />
          <span>LEGALTRACK</span>
          <span className="brand-badge">Academic</span>
        </Link>

        {/* Desktop Nav Links */}
        <ul className="nav-links">
          <li>
            <Link to="/track" className={`nav-link ${location.pathname === '/track' ? 'active' : ''}`}>
              <Search size={16} />
              <span>Track Case</span>
            </Link>
          </li>
          <li>
            <Link to="/lawyers" className={`nav-link ${location.pathname.startsWith('/lawyers') ? 'active' : ''}`}>
              <Users size={16} />
              <span>Find Lawyer</span>
            </Link>
          </li>
          <li>
            <Link to="/legal-aid" className={`nav-link ${location.pathname.startsWith('/legal-aid') ? 'active' : ''}`}>
              <ShieldAlert size={16} />
              <span>Legal Aid</span>
            </Link>
          </li>
          <li>
            <Link to="/about" className={`nav-link ${location.pathname === '/about' ? 'active' : ''}`}>
              <FileText size={16} />
              <span>About / Viva</span>
            </Link>
          </li>
        </ul>

        {/* Actions / Auth */}
        <div className="nav-actions">
          {isAuthenticated ? (
            <>
              {/* Dashboard Link */}
              <Link to={getDashboardLink()} className="btn btn-secondary btn-sm">
                Dashboard
              </Link>

              {/* Chat Icon (For Clients & Lawyers) */}
              {(isClient() || isLawyer()) && (
                <Link to="/chat" className="btn btn-glass btn-icon" title="Messages">
                  <MessageSquare size={18} />
                </Link>
              )}

              {/* Notification Bell */}
              <div style={{ position: 'relative' }} ref={notifRef}>
                <button
                  className="btn btn-glass btn-icon"
                  onClick={handleNotificationClick}
                  style={{ position: 'relative' }}
                  title="Notifications"
                >
                  <Bell size={18} />
                  {unreadCount > 0 && (
                    <span
                      style={{
                        position: 'absolute',
                        top: '-4px',
                        right: '-4px',
                        background: 'var(--danger)',
                        color: '#fff',
                        fontSize: '0.65rem',
                        fontWeight: 700,
                        borderRadius: '50%',
                        width: '18px',
                        height: '18px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 0 8px rgba(239, 68, 68, 0.6)'
                      }}
                    >
                      {unreadCount > 9 ? '9+' : unreadCount}
                    </span>
                  )}
                </button>

                {/* Notification Dropdown */}
                {showNotifications && (
                  <div
                    className="glass-card"
                    style={{
                      position: 'absolute',
                      right: 0,
                      top: '48px',
                      width: '360px',
                      maxHeight: '450px',
                      overflowY: 'auto',
                      padding: '1rem',
                      zIndex: 100,
                      boxShadow: 'var(--shadow-xl)',
                      border: '1px solid var(--border-glass)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>
                      <h4 style={{ fontSize: '0.95rem' }}>Notifications</h4>
                      {unreadCount > 0 && (
                        <button
                          onClick={markAllAsRead}
                          className="btn btn-glass btn-sm"
                          style={{ fontSize: '0.75rem', padding: '0.2rem 0.5rem' }}
                        >
                          <CheckCheck size={12} /> Mark all read
                        </button>
                      )}
                    </div>

                    {notifications.length === 0 ? (
                      <p style={{ textAlign: 'center', padding: '1.5rem 0', fontSize: '0.85rem' }}>
                        No notifications yet.
                      </p>
                    ) : (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          onClick={() => {
                            if (!n.isRead) markAsRead(n.id);
                            if (n.actionUrl) {
                              setShowNotifications(false);
                              navigate(n.actionUrl);
                            }
                          }}
                          style={{
                            padding: '0.75rem',
                            borderRadius: 'var(--radius-md)',
                            marginBottom: '0.5rem',
                            background: n.isRead ? 'transparent' : 'var(--bg-glass-hover)',
                            borderLeft: n.isRead ? 'none' : '3px solid var(--primary)',
                            cursor: 'pointer',
                            transition: 'var(--transition)'
                          }}
                        >
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                            <strong style={{ fontSize: '0.85rem', color: 'var(--text-primary)' }}>{n.title}</strong>
                            <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                              {new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </span>
                          </div>
                          <p style={{ fontSize: '0.8rem', marginTop: '0.25rem', lineHeight: '1.4' }}>{n.message}</p>
                        </div>
                      ))
                    )}
                  </div>
                )}
              </div>

              {/* User Dropdown */}
              <div style={{ position: 'relative' }} ref={userRef}>
                <button
                  className="btn btn-glass"
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.4rem 0.75rem' }}
                >
                  <div
                    style={{
                      width: '28px',
                      height: '28px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, var(--primary), var(--purple))',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 700,
                      fontSize: '0.8rem',
                      color: '#fff'
                    }}
                  >
                    {user.name ? user.name.charAt(0).toUpperCase() : 'U'}
                  </div>
                  <span style={{ fontSize: '0.85rem', fontWeight: 600 }}>{user.name?.split(' ')[0]}</span>
                  <ChevronDown size={14} />
                </button>

                {showUserMenu && (
                  <div
                    className="glass-card"
                    style={{
                      position: 'absolute',
                      right: 0,
                      top: '48px',
                      width: '220px',
                      padding: '0.5rem',
                      zIndex: 100,
                      boxShadow: 'var(--shadow-xl)'
                    }}
                  >
                    <div style={{ padding: '0.5rem 0.75rem', borderBottom: '1px solid var(--border-color)', marginBottom: '0.5rem' }}>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem', color: 'var(--text-primary)' }}>{user.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user.email}</div>
                    </div>
                    <Link
                      to="/profile"
                      onClick={() => setShowUserMenu(false)}
                      className="nav-link"
                      style={{ fontSize: '0.85rem', padding: '0.5rem 0.75rem' }}
                    >
                      <UserIcon size={14} /> My Profile
                    </Link>
                    <Link
                      to="/support"
                      onClick={() => setShowUserMenu(false)}
                      className="nav-link"
                      style={{ fontSize: '0.85rem', padding: '0.5rem 0.75rem' }}
                    >
                      <ShieldAlert size={14} /> Help & Disputes
                    </Link>
                    <button
                      onClick={() => {
                        setShowUserMenu(false);
                        logout();
                        navigate('/');
                      }}
                      className="nav-link"
                      style={{
                        width: '100%',
                        textAlign: 'left',
                        border: 'none',
                        background: 'transparent',
                        color: 'var(--danger)',
                        fontSize: '0.85rem',
                        padding: '0.5rem 0.75rem',
                        cursor: 'pointer'
                      }}
                    >
                      <LogOut size={14} /> Sign Out
                    </button>
                  </div>
                )}
              </div>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-glass btn-sm">
                Log In
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Get Started
              </Link>
            </>
          )}

          {/* Mobile Menu Toggle */}
          <button
            className="btn btn-glass btn-icon"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            style={{ display: 'none' }} // Can be made responsive
          >
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </div>
    </nav>
  );
};
