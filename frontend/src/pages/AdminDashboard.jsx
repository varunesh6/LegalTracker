import React, { useState, useEffect } from 'react';
import { adminService } from '../services/adminService';
import { Sidebar } from '../components/Sidebar';
import { Badge } from '../components/Badge';
import {
  Shield,
  Users,
  Briefcase,
  Database,
  History,
  CheckCircle2,
  XCircle,
  Clock,
  RefreshCw,
  FileText
} from 'lucide-react';

export const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [syncLogs, setSyncLogs] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [activeTab, setActiveTab] = useState('USERS'); // USERS, SYNC_LOGS, AUDIT_TRAIL
  const [loading, setLoading] = useState(true);

  const fetchAdminData = async () => {
    setLoading(true);
    try {
      const [statsRes, usersRes, logsRes, auditRes] = await Promise.allSettled([
        adminService.getSystemStats(),
        adminService.getUsers(null, 0, 15),
        adminService.getSyncLogs(0, 10),
        adminService.getAuditLogs(0, 15)
      ]);

      if (statsRes.status === 'fulfilled' && statsRes.value?.success) setStats(statsRes.value.data);
      if (usersRes.status === 'fulfilled' && usersRes.value?.success) setUsers(usersRes.value.data.content || []);
      if (logsRes.status === 'fulfilled' && logsRes.value?.success) setSyncLogs(logsRes.value.data.content || []);
      if (auditRes.status === 'fulfilled' && auditRes.value?.success) setAuditLogs(auditRes.value.data.content || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, []);

  const handleUpdateStatus = async (userId, newStatus) => {
    try {
      await adminService.updateUserStatus(userId, newStatus);
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, status: newStatus } : u))
      );
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        <Sidebar />

        <main style={{ flex: 1 }}>
          <div className="glass-card" style={{ marginBottom: '2rem' }}>
            <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Platform Administration Console</h1>
            <p style={{ fontSize: '0.9rem' }}>Real-time metrics, user role governance, court sync logs, and compliance audit trail.</p>
          </div>

          {/* High-Level Metrics */}
          {stats && (
            <div className="grid-4" style={{ marginBottom: '2rem' }}>
              <div className="glass-card">
                <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total Users</span>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--primary)', marginTop: '0.25rem' }}>
                  {stats.totalUsers}
                </div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                  {stats.activeLawyers} Lawyers | {stats.activeClients} Clients
                </span>
              </div>

              <div className="glass-card">
                <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Case Files</span>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--accent-gold)', marginTop: '0.25rem' }}>
                  {stats.totalCases}
                </div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                  {stats.pendingCases} Pending Matters
                </span>
              </div>

              <div className="glass-card">
                <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Legal Aid Grants</span>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--purple)', marginTop: '0.25rem' }}>
                  {stats.totalLegalAidApplications}
                </div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                  {stats.approvedLegalAid} Approved Applications
                </span>
              </div>

              <div className="glass-card">
                <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Case Tracking Users</span>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--info)', marginTop: '0.25rem' }}>
                  {stats.totalTrackedCases}
                </div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                  {stats.activeAttentionItems} Attention Alerts
                </span>
              </div>
            </div>
          )}

          {/* Tabbed Console Views */}
          <div className="glass-card">
            <div style={{ display: 'flex', gap: '0.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', marginBottom: '1.5rem' }}>
              <button
                onClick={() => setActiveTab('USERS')}
                className={`search-tab-btn ${activeTab === 'USERS' ? 'active' : ''}`}
              >
                <Users size={16} /> User Management
              </button>
              <button
                onClick={() => setActiveTab('SYNC_LOGS')}
                className={`search-tab-btn ${activeTab === 'SYNC_LOGS' ? 'active' : ''}`}
              >
                <RefreshCw size={16} /> Court Sync Logs
              </button>
              <button
                onClick={() => setActiveTab('AUDIT_TRAIL')}
                className={`search-tab-btn ${activeTab === 'AUDIT_TRAIL' ? 'active' : ''}`}
              >
                <History size={16} /> Audit Trail &amp; Events
              </button>
            </div>

            {/* View 1: Users */}
            {activeTab === 'USERS' && (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>User</th>
                      <th>Email</th>
                      <th>Roles</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((u) => (
                      <tr key={u.id}>
                        <td><strong>{u.name}</strong></td>
                        <td><code>{u.email}</code></td>
                        <td>
                          {u.roles?.map((r, i) => (
                            <span key={i} className="badge badge-primary" style={{ marginRight: '0.25rem' }}>
                              {r.replace('ROLE_', '')}
                            </span>
                          ))}
                        </td>
                        <td><Badge variant={u.status}>{u.status}</Badge></td>
                        <td>
                          {u.status === 'ACTIVE' ? (
                            <button
                              onClick={() => handleUpdateStatus(u.id, 'SUSPENDED')}
                              className="btn btn-glass btn-sm"
                              style={{ color: 'var(--danger)', fontSize: '0.75rem' }}
                            >
                              Suspend
                            </button>
                          ) : (
                            <button
                              onClick={() => handleUpdateStatus(u.id, 'ACTIVE')}
                              className="btn btn-glass btn-sm"
                              style={{ color: 'var(--success)', fontSize: '0.75rem' }}
                            >
                              Activate
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* View 2: Sync Logs */}
            {activeTab === 'SYNC_LOGS' && (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Sync Job ID</th>
                      <th>Status</th>
                      <th>Cases Evaluated</th>
                      <th>Updates Found</th>
                      <th>Started At</th>
                    </tr>
                  </thead>
                  <tbody>
                    {syncLogs.length === 0 ? (
                      <tr>
                        <td colSpan={5} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                          No court synchronization records logged.
                        </td>
                      </tr>
                    ) : (
                      syncLogs.map((log) => (
                        <tr key={log.id}>
                          <td><code>JOB-{log.id}</code></td>
                          <td><Badge variant={log.status}>{log.status}</Badge></td>
                          <td>{log.casesChecked || 0} Cases</td>
                          <td>{log.updatesFound || 0} Updated</td>
                          <td>{new Date(log.startedAt).toLocaleString()}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {/* View 3: Audit Trail */}
            {activeTab === 'AUDIT_TRAIL' && (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Actor</th>
                      <th>Action</th>
                      <th>Entity</th>
                      <th>Description</th>
                      <th>Timestamp</th>
                    </tr>
                  </thead>
                  <tbody>
                    {auditLogs.length === 0 ? (
                      <tr>
                        <td colSpan={5} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                          No audit events recorded.
                        </td>
                      </tr>
                    ) : (
                      auditLogs.map((a) => (
                        <tr key={a.id}>
                          <td><strong>{a.actorName || 'SYSTEM'}</strong></td>
                          <td><code>{a.action}</code></td>
                          <td>{a.entityType} #{a.entityId}</td>
                          <td style={{ fontSize: '0.85rem' }}>{a.newValue || a.oldValue || 'Action performed'}</td>
                          <td style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            {new Date(a.timestamp).toLocaleString()}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};
