import React, { useState, useEffect, useRef } from 'react';
import { chatService } from '../services/chatService';
import { useAuth } from '../context/AuthContext';
import { MessageSquare, Send, User, Clock, AlertCircle } from 'lucide-react';

export const ChatPage = () => {
  const { user } = useAuth();
  const [conversations, setConversations] = useState([]);
  const [activeConv, setActiveConv] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);

  const fetchConversations = async () => {
    try {
      const res = await chatService.getMyConversations();
      if (res?.success) {
        setConversations(res.data || []);
        if (res.data?.length > 0 && !activeConv) {
          selectConversation(res.data[0]);
        }
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConversations();
    const interval = setInterval(fetchConversations, 10000);
    return () => clearInterval(interval);
  }, []);

  const selectConversation = async (conv) => {
    setActiveConv(conv);
    try {
      const res = await chatService.getMessages(conv.id);
      if (res?.success) {
        setMessages(res.data || []);
        chatService.markAsRead(conv.id);
      }
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || !activeConv) return;

    setSending(true);
    try {
      const res = await chatService.sendMessage(activeConv.id, {
        content: newMessage.trim(),
        messageType: 'TEXT'
      });
      if (res?.success) {
        setMessages((prev) => [...prev, res.data]);
        setNewMessage('');
      }
    } catch (e) {
      console.error(e);
    } finally {
      setSending(false);
    }
  };

  const getOtherParticipant = (conv) => {
    if (!conv) return { name: 'User' };
    return user?.id === conv.clientId
      ? { name: conv.lawyerName || 'Advocate', role: 'Advocate' }
      : { name: conv.clientName || 'Client', role: 'Client' };
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Client-Lawyer Messaging</h1>
        <p style={{ fontSize: '0.9rem' }}>Secure real-time communication for active case matters.</p>
      </div>

      <div className="chat-container">
        {/* Conversations Sidebar */}
        <div className="chat-sidebar">
          <div style={{ padding: '1rem', borderBottom: '1px solid var(--border-color)' }}>
            <strong style={{ fontSize: '0.95rem' }}>Active Conversations</strong>
          </div>

          <div style={{ flex: 1, overflowY: 'auto' }}>
            {loading ? (
              <p style={{ padding: '1rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>Loading threads...</p>
            ) : conversations.length === 0 ? (
              <p style={{ padding: '1.5rem 1rem', color: 'var(--text-muted)', fontSize: '0.85rem', textAlign: 'center' }}>
                No active conversations yet. Reach out to an advocate from their profile.
              </p>
            ) : (
              conversations.map((conv) => {
                const other = getOtherParticipant(conv);
                const isActive = activeConv?.id === conv.id;
                return (
                  <div
                    key={conv.id}
                    onClick={() => selectConversation(conv)}
                    style={{
                      padding: '0.85rem 1rem',
                      borderBottom: '1px solid var(--border-color)',
                      background: isActive ? 'var(--bg-glass-hover)' : 'transparent',
                      cursor: 'pointer',
                      borderLeft: isActive ? '3px solid var(--primary)' : 'none',
                      transition: 'var(--transition)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
                      <strong style={{ fontSize: '0.9rem', color: 'var(--text-primary)' }}>{other.name}</strong>
                      {conv.unreadCount > 0 && (
                        <span className="badge badge-danger" style={{ fontSize: '0.65rem' }}>{conv.unreadCount}</span>
                      )}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {conv.lastMessage || 'Start conversation...'}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Message Thread */}
        <div className="chat-thread">
          {activeConv ? (
            <>
              {/* Active Header */}
              <div style={{ padding: '1rem 1.25rem', borderBottom: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '0.75rem', background: 'rgba(17, 24, 39, 0.4)' }}>
                <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'var(--primary-light)', color: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700 }}>
                  <User size={18} />
                </div>
                <div>
                  <strong style={{ fontSize: '0.95rem' }}>{getOtherParticipant(activeConv).name}</strong>
                  {activeConv.caseTitle && (
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                      Regarding Case: {activeConv.caseTitle}
                    </div>
                  )}
                </div>
              </div>

              {/* Messages Body */}
              <div style={{ flex: 1, padding: '1.25rem', overflowY: 'auto', display: 'flex', flexDirection: 'column' }}>
                {messages.length === 0 ? (
                  <p style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: 'auto', marginBottom: 'auto', fontSize: '0.85rem' }}>
                    Send a message to begin your consultation.
                  </p>
                ) : (
                  messages.map((msg) => {
                    const isMe = msg.senderId === user?.id;
                    return (
                      <div
                        key={msg.id}
                        className={`message-bubble ${isMe ? 'message-outgoing' : 'message-incoming'}`}
                      >
                        <p style={{ margin: 0, fontSize: '0.9rem' }}>{msg.content}</p>
                        <span style={{ fontSize: '0.65rem', opacity: 0.7, display: 'block', textAlign: 'right', marginTop: '0.25rem' }}>
                          {new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                    );
                  })
                )}
                <div ref={messagesEndRef} />
              </div>

              {/* Message Input Box */}
              <form onSubmit={handleSendMessage} style={{ padding: '1rem', borderTop: '1px solid var(--border-color)', display: 'flex', gap: '0.75rem', background: 'rgba(17, 24, 39, 0.6)' }}>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Type your message..."
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                />
                <button type="submit" disabled={sending || !newMessage.trim()} className="btn btn-primary" style={{ minWidth: '100px' }}>
                  <Send size={16} />
                  <span>Send</span>
                </button>
              </form>
            </>
          ) : (
            <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Select a conversation to start messaging.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
