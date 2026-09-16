import React, { useState, useEffect } from 'react';
import { supportService } from '../services/supportService';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/Badge';
import { Modal } from '../components/Modal';
import {
  HelpCircle,
  Plus,
  Send,
  ShieldAlert,
  AlertCircle,
  Clock,
  CheckCircle2
} from 'lucide-react';

export const SupportPage = () => {
  const { user, isSupport, isAdmin } = useAuth();
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [loading, setLoading] = useState(true);

  // Create Ticket Modal
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('TECHNICAL_SUPPORT');
  const [priority, setPriority] = useState('NORMAL');
  const [creating, setCreating] = useState(false);

  // Reply message
  const [replyMessage, setReplyMessage] = useState('');
  const [sendingReply, setSendingReply] = useState(false);

  const fetchTickets = async () => {
    setLoading(true);
    try {
      const res = isSupport() || isAdmin()
        ? await supportService.getAllTickets(null, 0, 20)
        : await supportService.getMyTickets(0, 20);

      if (res?.success) {
        setTickets(res.data.content || []);
        if (res.data.content?.length > 0 && !selectedTicket) {
          viewTicketDetails(res.data.content[0].id);
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTickets();
  }, []);

  const viewTicketDetails = async (id) => {
    try {
      const res = await supportService.getTicketById(id);
      if (res?.success) {
        setSelectedTicket(res.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleCreateTicket = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      const res = await supportService.createTicket({
        subject,
        description,
        category,
        priority
      });
      if (res?.success) {
        setShowCreateModal(false);
        setSubject('');
        setDescription('');
        fetchTickets();
      }
    } catch (e) {
      console.error(e);
    } finally {
      setCreating(false);
    }
  };

  const handleSendReply = async (e) => {
    e.preventDefault();
    if (!replyMessage.trim() || !selectedTicket) return;

    setSendingReply(true);
    try {
      const res = await supportService.addMessageToTicket(selectedTicket.id, {
        message: replyMessage.trim()
      });
      if (res?.success) {
        setReplyMessage('');
        viewTicketDetails(selectedTicket.id);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setSendingReply(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Help Desk &amp; Dispute Escalation</h1>
          <p style={{ fontSize: '0.9rem' }}>Submit technical inquiries or file disputes regarding lawyer engagements.</p>
        </div>
        <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
          <Plus size={16} /> Open Support Ticket
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '350px 1fr', gap: '1.5rem', minHeight: '600px' }}>
        {/* Ticket List */}
        <div className="glass-card" style={{ padding: '1rem', overflowY: 'auto' }}>
          <div style={{ paddingBottom: '0.75rem', borderBottom: '1px solid var(--border-color)', marginBottom: '0.75rem' }}>
            <strong style={{ fontSize: '0.95rem' }}>Support Tickets</strong>
          </div>

          {loading ? (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>Loading tickets...</p>
          ) : tickets.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', textAlign: 'center', padding: '2rem 0' }}>
              No support tickets found.
            </p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {tickets.map((t) => {
                const isActive = selectedTicket?.id === t.id;
                return (
                  <div
                    key={t.id}
                    onClick={() => viewTicketDetails(t.id)}
                    style={{
                      padding: '0.75rem',
                      borderRadius: 'var(--radius-md)',
                      background: isActive ? 'var(--bg-glass-hover)' : 'var(--bg-glass)',
                      border: '1px solid var(--border-color)',
                      borderLeft: isActive ? '3px solid var(--primary)' : '1px solid var(--border-color)',
                      cursor: 'pointer'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
                      <strong style={{ fontSize: '0.9rem' }}>{t.subject}</strong>
                      <Badge variant={t.status}>{t.status}</Badge>
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      #{t.ticketNumber} | {new Date(t.createdAt).toLocaleDateString()}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Selected Ticket Thread */}
        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          {selectedTicket ? (
            <>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', paddingBottom: '1rem', borderBottom: '1px solid var(--border-color)', marginBottom: '1rem' }}>
                  <div>
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>{selectedTicket.subject}</h2>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      Ticket #{selectedTicket.ticketNumber} | Category: {selectedTicket.category}
                    </span>
                  </div>
                  <Badge variant={selectedTicket.status}>{selectedTicket.status}</Badge>
                </div>

                <div style={{ padding: '1rem', background: 'var(--bg-glass)', borderRadius: 'var(--radius-md)', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
                  {selectedTicket.description}
                </div>

                {/* Message Thread */}
                <h4 style={{ fontSize: '0.95rem', marginBottom: '0.75rem' }}>Updates &amp; Replies</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', maxHeight: '320px', overflowY: 'auto', marginBottom: '1rem' }}>
                  {selectedTicket.messages?.map((msg) => (
                    <div
                      key={msg.id}
                      style={{
                        padding: '0.75rem 1rem',
                        borderRadius: 'var(--radius-md)',
                        background: msg.senderId === user?.id ? 'var(--bg-glass-hover)' : 'rgba(59, 130, 246, 0.1)',
                        border: '1px solid var(--border-color)'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.25rem' }}>
                        <strong>{msg.senderName}</strong>
                        <span>{new Date(msg.createdAt).toLocaleString()}</span>
                      </div>
                      <p style={{ fontSize: '0.875rem', margin: 0 }}>{msg.message}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* Reply Form */}
              <form onSubmit={handleSendReply} style={{ display: 'flex', gap: '0.75rem', borderTop: '1px solid var(--border-color)', paddingTop: '1rem' }}>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Type your reply message..."
                  value={replyMessage}
                  onChange={(e) => setReplyMessage(e.target.value)}
                />
                <button type="submit" disabled={sendingReply || !replyMessage.trim()} className="btn btn-primary" style={{ minWidth: '100px' }}>
                  <Send size={16} />
                  <span>Reply</span>
                </button>
              </form>
            </>
          ) : (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)', margin: 'auto' }}>
              Select a ticket to view conversation.
            </div>
          )}
        </div>
      </div>

      {/* Create Ticket Modal */}
      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} title="Open Support / Dispute Ticket">
        <form onSubmit={handleCreateTicket}>
          <div className="form-group">
            <label className="form-label">Subject</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Inquiry regarding lawyer assignment / Document upload issue"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Category</label>
              <select
                className="form-control"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                <option value="TECHNICAL_SUPPORT">Technical Platform Issue</option>
                <option value="LAWYER_DISPUTE">Advocate Service Dispute</option>
                <option value="LEGAL_AID_INQUIRY">Legal Aid Scheme Inquiry</option>
                <option value="ACCOUNT_BILLING">Account &amp; Security</option>
                <option value="OTHER">Other Query</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Priority</label>
              <select
                className="form-control"
                value={priority}
                onChange={(e) => setPriority(e.target.value)}
              >
                <option value="NORMAL">Normal</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Detailed Description</label>
            <textarea
              className="form-control"
              rows="4"
              placeholder="Describe the issue or dispute in detail..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" disabled={creating} className="btn btn-primary">
              <span>{creating ? 'Creating...' : 'Submit Ticket'}</span>
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
