import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Briefcase,
  BookmarkCheck,
  Calendar,
  MessageSquare,
  FileCheck2,
  Users,
  ShieldAlert,
  UserCheck,
  History,
  Settings,
  HelpCircle,
  FileText
} from 'lucide-react';

export const Sidebar = () => {
  const { isClient, isLawyer, isOfficer, isAdmin } = useAuth();

  return (
    <aside
      className="glass-card"
      style={{
        width: '260px',
        padding: '1.25rem 0.75rem',
        height: 'fit-content',
        position: 'sticky',
        top: '90px',
        display: 'flex',
        flexDirection: 'column',
        gap: '0.4rem'
      }}
    >
      <div style={{ padding: '0 0.75rem 0.75rem', borderBottom: '1px solid var(--border-color)', marginBottom: '0.5rem' }}>
        <span style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--text-muted)', letterSpacing: '0.5px' }}>
          Navigation
        </span>
      </div>

      {/* Client Links */}
      {isClient() && (
        <>
          <NavLink to="/client/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /> Overview
          </NavLink>
          <NavLink to="/client/cases" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Briefcase size={18} /> My Cases
          </NavLink>
          <NavLink to="/client/tracked" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <BookmarkCheck size={18} /> Tracked Cases
          </NavLink>
          <NavLink to="/client/requests" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <UserCheck size={18} /> Lawyer Consults
          </NavLink>
          <NavLink to="/client/legal-aid" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <ShieldAlert size={18} /> Legal Aid Status
          </NavLink>
          <NavLink to="/chat" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <MessageSquare size={18} /> Messages
          </NavLink>
        </>
      )}

      {/* Lawyer Links */}
      {isLawyer() && (
        <>
          <NavLink to="/lawyer/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /> Lawyer Desk
          </NavLink>
          <NavLink to="/lawyer/cases" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Briefcase size={18} /> Active Case Files
          </NavLink>
          <NavLink to="/lawyer/requests" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <UserCheck size={18} /> Client Inquiries
          </NavLink>
          <NavLink to="/lawyer/calendar" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Calendar size={18} /> Court Diary
          </NavLink>
          <NavLink to="/chat" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <MessageSquare size={18} /> Client Chat
          </NavLink>
        </>
      )}

      {/* Officer Links */}
      {isOfficer() && (
        <>
          <NavLink to="/officer/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /> Officer Desk
          </NavLink>
          <NavLink to="/officer/applications" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <FileCheck2 size={18} /> Legal Aid Applications
          </NavLink>
          <NavLink to="/officer/assignments" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Users size={18} /> Advocate Assignments
          </NavLink>
        </>
      )}

      {/* Admin Links */}
      {isAdmin() && (
        <>
          <NavLink to="/admin/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /> Admin Console
          </NavLink>
          <NavLink to="/admin/users" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Users size={18} /> User Accounts
          </NavLink>
          <NavLink to="/admin/verification" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <UserCheck size={18} /> Lawyer Verifications
          </NavLink>
          <NavLink to="/admin/sync-logs" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <History size={18} /> Court Sync Logs
          </NavLink>
          <NavLink to="/admin/audit-logs" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <FileText size={18} /> Audit Trail
          </NavLink>
        </>
      )}

      <div style={{ margin: '0.75rem 0', borderTop: '1px solid var(--border-color)' }} />

      <NavLink to="/support" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
        <HelpCircle size={18} /> Help & Support
      </NavLink>
      <NavLink to="/profile" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
        <Settings size={18} /> Settings
      </NavLink>
    </aside>
  );
};
