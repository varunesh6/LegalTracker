import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { caseService } from '../services/caseService';
import { trackedCaseService } from '../services/trackedCaseService';
import { documentService } from '../services/documentService';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/Badge';
import { Timeline } from '../components/Timeline';
import { AttentionBanner } from '../components/AttentionBanner';
import { Modal } from '../components/Modal';
import { DocumentUploadModal } from '../components/DocumentUploadModal';
import {
  Briefcase,
  Calendar,
  Clock,
  BookOpen,
  FileText,
  Lock,
  Plus,
  BookmarkPlus,
  Check,
  Download,
  AlertCircle,
  Eye,
  Trash2,
  Share2
} from 'lucide-react';

export const CaseWorkspacePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, isLawyer, isClient, isAdmin } = useAuth();

  const [activeTab, setActiveTab] = useState('DIARY'); // OVERVIEW, DIARY, HEARINGS, DOCUMENTS, NOTES
  const [caseDetails, setCaseDetails] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modals
  const [showDocUpload, setShowDocUpload] = useState(false);
  const [showAddDiary, setShowAddDiary] = useState(false);
  const [showAddHearing, setShowAddHearing] = useState(false);
  const [showAddOrder, setShowAddOrder] = useState(false);
  const [showAddNote, setShowAddNote] = useState(false);

  // Form states
  const [diaryTitle, setDiaryTitle] = useState('');
  const [diaryDesc, setDiaryDesc] = useState('');
  const [diaryType, setDiaryType] = useState('COURT_PROCEEDING');
  const [diaryVis, setDiaryVis] = useState('SHARED');

  const [hearingDate, setHearingDate] = useState('');
  const [hearingPurpose, setHearingPurpose] = useState('Arguments');
  const [hearingJudge, setHearingJudge] = useState('');

  const [orderDate, setOrderDate] = useState(new Date().toISOString().split('T')[0]);
  const [orderTitle, setOrderTitle] = useState('');
  const [orderType, setOrderType] = useState('INTERIM_ORDER');
  const [orderSummary, setOrderSummary] = useState('');

  const [noteTitle, setNoteTitle] = useState('');
  const [noteContent, setNoteContent] = useState('');
  const [noteVis, setNoteVis] = useState('LAWYER_ONLY');

  const fetchWorkspace = async () => {
    try {
      const res = await caseService.getCaseDetails(id);
      if (res?.success) {
        setCaseDetails(res.data);
      }
      const timeRes = await caseService.getCaseTimeline(id);
      if (timeRes?.success) {
        setTimeline(timeRes.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load case workspace');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWorkspace();
  }, [id]);

  const handleAddDiaryEntry = async (e) => {
    e.preventDefault();
    try {
      const res = await caseService.addDiaryEntry(id, {
        title: diaryTitle,
        description: diaryDesc,
        entryType: diaryType,
        visibility: diaryVis,
        eventDate: new Date().toISOString()
      });
      if (res?.success) {
        setShowAddDiary(false);
        setDiaryTitle('');
        setDiaryDesc('');
        fetchWorkspace();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add diary entry');
    }
  };

  const handleAddHearing = async (e) => {
    e.preventDefault();
    try {
      const res = await caseService.addHearing(id, {
        hearingDate,
        purpose: hearingPurpose,
        judgeName: hearingJudge,
        status: 'SCHEDULED'
      });
      if (res?.success) {
        setShowAddHearing(false);
        setHearingDate('');
        setHearingPurpose('');
        fetchWorkspace();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to schedule hearing');
    }
  };

  const handleAddOrder = async (e) => {
    e.preventDefault();
    try {
      const res = await caseService.addOrder(id, {
        orderDate,
        title: orderTitle,
        orderType,
        summary: orderSummary
      });
      if (res?.success) {
        setShowAddOrder(false);
        setOrderTitle('');
        setOrderSummary('');
        fetchWorkspace();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add court order');
    }
  };

  const handleAddNote = async (e) => {
    e.preventDefault();
    try {
      const res = await caseService.addCaseNote(id, {
        title: noteTitle,
        content: noteContent,
        visibility: noteVis
      });
      if (res?.success) {
        setShowAddNote(false);
        setNoteTitle('');
        setNoteContent('');
        fetchWorkspace();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add case note');
    }
  };

  const handleDownloadDoc = async (docId, fileName) => {
    try {
      await documentService.downloadDocument(docId, fileName);
    } catch (e) {
      alert('Failed to download document');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '5rem 0' }}>
        <div className="pulse-dot" style={{ width: '24px', height: '24px', margin: '0 auto 1rem', background: 'var(--primary)' }} />
        <p>Loading Case Workspace...</p>
      </div>
    );
  }

  if (error || !caseDetails) {
    return (
      <div className="container" style={{ padding: '4rem 0', textAlign: 'center' }}>
        <div className="glass-card" style={{ maxWidth: '500px', margin: '0 auto' }}>
          <h3 style={{ color: 'var(--danger)', marginBottom: '0.5rem' }}>Workspace Inaccessible</h3>
          <p style={{ marginBottom: '1.5rem' }}>{error || 'Case file does not exist or you lack permission.'}</p>
          <button onClick={() => navigate(-1)} className="btn btn-primary">
            Go Back
          </button>
        </div>
      </div>
    );
  }

  const caseFile = caseDetails.caseFile;

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      {/* Rule-Engine Attention Alerts */}
      <AttentionBanner items={caseDetails.attentionItems} />

      {/* Case Header Card */}
      <div className="glass-card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.4rem' }}>
              <Badge variant={caseFile.status}>{caseFile.status}</Badge>
              <Badge variant={caseFile.stage}>{caseFile.stage}</Badge>
              {caseFile.engagementType && (
                <span className="badge badge-purple">{caseFile.engagementType.replace('_', ' ')}</span>
              )}
            </div>
            <h1 style={{ fontSize: '1.6rem', marginBottom: '0.25rem' }}>{caseFile.title}</h1>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.85rem', color: 'var(--primary)' }}>
              CNR: {caseFile.cnrNumber || 'N/A'} | Case No: {caseFile.caseNumber || 'N/A'}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
            {/* Quick Actions based on Role */}
            {(isLawyer() || isAdmin()) && (
              <>
                <button onClick={() => setShowAddDiary(true)} className="btn btn-secondary btn-sm">
                  <Plus size={14} /> Add Diary Log
                </button>
                <button onClick={() => setShowAddHearing(true)} className="btn btn-secondary btn-sm">
                  <Calendar size={14} /> Schedule Hearing
                </button>
              </>
            )}

            <button onClick={() => setShowDocUpload(true)} className="btn btn-primary btn-sm">
              <Plus size={14} /> Upload Document
            </button>
          </div>
        </div>

        {/* Quick Context Matrix */}
        <div className="grid-4" style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1.25rem' }}>
          <div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Court</span>
            <strong style={{ fontSize: '0.85rem' }}>{caseFile.courtName || 'District Court'}</strong>
          </div>
          <div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Representing Advocate</span>
            <strong style={{ fontSize: '0.85rem' }}>{caseFile.lawyerName || 'Unassigned'}</strong>
          </div>
          <div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Client / Petitioner</span>
            <strong style={{ fontSize: '0.85rem' }}>{caseFile.clientName || 'General'}</strong>
          </div>
          <div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Next Hearing Date</span>
            <strong style={{ fontSize: '0.85rem', color: caseFile.nextHearingDate ? 'var(--warning)' : 'var(--text-primary)' }}>
              {caseFile.nextHearingDate || 'Not Scheduled'}
            </strong>
          </div>
        </div>
      </div>

      {/* Tab Navigation Hub */}
      <div className="glass-card" style={{ padding: '0.5rem 1rem', marginBottom: '2rem' }}>
        <div style={{ display: 'flex', gap: '0.5rem', overflowX: 'auto' }}>
          <button
            onClick={() => setActiveTab('DIARY')}
            className={`search-tab-btn ${activeTab === 'DIARY' ? 'active' : ''}`}
          >
            <BookOpen size={16} /> Case Diary ({caseDetails.diaryEntries?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('OVERVIEW')}
            className={`search-tab-btn ${activeTab === 'OVERVIEW' ? 'active' : ''}`}
          >
            <Clock size={16} /> Timeline &amp; Events
          </button>
          <button
            onClick={() => setActiveTab('HEARINGS')}
            className={`search-tab-btn ${activeTab === 'HEARINGS' ? 'active' : ''}`}
          >
            <Calendar size={16} /> Hearings &amp; Orders ({caseDetails.hearings?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('DOCUMENTS')}
            className={`search-tab-btn ${activeTab === 'DOCUMENTS' ? 'active' : ''}`}
          >
            <FileText size={16} /> Case Documents ({caseDetails.documents?.length || 0})
          </button>
          <button
            onClick={() => setActiveTab('NOTES')}
            className={`search-tab-btn ${activeTab === 'NOTES' ? 'active' : ''}`}
          >
            <Lock size={16} /> Case Notes ({caseDetails.notes?.length || 0})
          </button>
        </div>
      </div>

      {/* Tab 1: Chronological Case Diary */}
      {activeTab === 'DIARY' && (
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem' }}>Chronological Case Diary</h3>
              <p style={{ fontSize: '0.85rem' }}>Daily proceeding records, consultation notes, and filing updates.</p>
            </div>
            {(isLawyer() || isAdmin()) && (
              <button onClick={() => setShowAddDiary(true)} className="btn btn-primary btn-sm">
                <Plus size={14} /> Add Diary Entry
              </button>
            )}
          </div>

          {caseDetails.diaryEntries?.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem 0' }}>
              No diary entries recorded for this case workspace yet.
            </p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {caseDetails.diaryEntries.map((entry) => (
                <div
                  key={entry.id}
                  style={{
                    padding: '1.25rem',
                    borderRadius: 'var(--radius-lg)',
                    background: 'var(--bg-glass)',
                    border: '1px solid var(--border-color)',
                    borderLeft: entry.visibility === 'SHARED' ? '4px solid var(--primary)' : '4px solid var(--accent-gold)'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '0.5rem' }}>
                    <div>
                      <strong style={{ fontSize: '1rem', color: 'var(--text-primary)' }}>{entry.title}</strong>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginLeft: '0.75rem' }}>
                        by {entry.createdByName} ({entry.createdByRole})
                      </span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <span className="badge badge-info">{entry.entryType}</span>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {new Date(entry.createdAt).toLocaleDateString([], { day: 'numeric', month: 'short', year: 'numeric' })}
                      </span>
                    </div>
                  </div>
                  <p style={{ fontSize: '0.9rem', lineHeight: '1.6', color: 'var(--text-secondary)' }}>
                    {entry.description}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab 2: Timeline & Events */}
      {activeTab === 'OVERVIEW' && (
        <div>
          {timeline && <Timeline timeline={timeline} />}
        </div>
      )}

      {/* Tab 3: Hearings & Orders */}
      {activeTab === 'HEARINGS' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          {/* Hearings Section */}
          <div className="glass-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Calendar size={18} color="var(--warning)" /> Scheduled Hearings &amp; Cause List
              </h3>
              {(isLawyer() || isAdmin()) && (
                <button onClick={() => setShowAddHearing(true)} className="btn btn-secondary btn-sm">
                  <Plus size={14} /> Schedule Next Hearing
                </button>
              )}
            </div>

            {caseDetails.hearings?.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No hearings scheduled yet.</p>
            ) : (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Hearing Date</th>
                      <th>Purpose / Stage</th>
                      <th>Presiding Judge</th>
                      <th>Status</th>
                      <th>Notes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {caseDetails.hearings.map((h) => (
                      <tr key={h.id}>
                        <td><strong>{h.hearingDate}</strong></td>
                        <td>{h.purpose}</td>
                        <td>{h.judgeName || 'Hon’ble Bench'}</td>
                        <td><Badge variant={h.status}>{h.status}</Badge></td>
                        <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{h.notes || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Court Orders Section */}
          <div className="glass-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FileText size={18} color="var(--accent-gold)" /> Pronounced Court Orders &amp; Judgments
              </h3>
              {(isLawyer() || isAdmin()) && (
                <button onClick={() => setShowAddOrder(true)} className="btn btn-secondary btn-sm">
                  <Plus size={14} /> Record Order
                </button>
              )}
            </div>

            {caseDetails.orders?.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No court orders recorded.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {caseDetails.orders.map((o) => (
                  <div key={o.id} style={{ padding: '1rem', background: 'var(--bg-glass)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                      <strong style={{ fontSize: '1rem' }}>{o.title}</strong>
                      <Badge variant="ORDERS_JUDGMENT">{o.orderType}</Badge>
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
                      Date: {o.orderDate}
                    </div>
                    <p style={{ fontSize: '0.875rem' }}>{o.summary}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Tab 4: Documents & Version History */}
      {activeTab === 'DOCUMENTS' && (
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem' }}>Case Document Repository</h3>
              <p style={{ fontSize: '0.85rem' }}>Version-controlled filings, affidavits, orders, and evidence.</p>
            </div>
            <button onClick={() => setShowDocUpload(true)} className="btn btn-primary btn-sm">
              <Plus size={14} /> Upload Document
            </button>
          </div>

          {caseDetails.documents?.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem 0' }}>
              No documents uploaded for this case file yet.
            </p>
          ) : (
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>File Name</th>
                    <th>Category</th>
                    <th>Version</th>
                    <th>Visibility</th>
                    <th>Uploaded By</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {caseDetails.documents.map((doc) => (
                    <tr key={doc.id}>
                      <td><strong>{doc.fileName}</strong></td>
                      <td><span className="badge badge-primary">{doc.category}</span></td>
                      <td><code>v{doc.versionNumber}</code></td>
                      <td><span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{doc.visibility}</span></td>
                      <td>{doc.uploadedByName || 'User'}</td>
                      <td>
                        <button
                          onClick={() => handleDownloadDoc(doc.id, doc.fileName)}
                          className="btn btn-glass btn-sm"
                          title="Download File"
                        >
                          <Download size={14} /> Download
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Tab 5: Private Notes */}
      {activeTab === 'NOTES' && (
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem' }}>Case Notes &amp; Strategy</h3>
              <p style={{ fontSize: '0.85rem' }}>Private research notes, client queries, and trial points.</p>
            </div>
            <button onClick={() => setShowAddNote(true)} className="btn btn-primary btn-sm">
              <Plus size={14} /> Add Note
            </button>
          </div>

          {caseDetails.notes?.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem 0' }}>
              No case notes created yet.
            </p>
          ) : (
            <div className="grid-2" style={{ gap: '1rem' }}>
              {caseDetails.notes.map((n) => (
                <div key={n.id} style={{ padding: '1rem', background: 'var(--bg-glass)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                    <strong style={{ fontSize: '0.95rem' }}>{n.title}</strong>
                    <span className="badge badge-warning">{n.visibility}</span>
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.5rem' }}>
                    by {n.userName} on {new Date(n.createdAt).toLocaleDateString()}
                  </div>
                  <p style={{ fontSize: '0.875rem', whiteSpace: 'pre-wrap' }}>{n.content}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Upload Modal */}
      <DocumentUploadModal
        isOpen={showDocUpload}
        onClose={() => setShowDocUpload(false)}
        caseId={id}
        onUploadSuccess={() => fetchWorkspace()}
      />

      {/* Add Diary Entry Modal */}
      <Modal
        isOpen={showAddDiary}
        onClose={() => setShowAddDiary(false)}
        title="Record Case Diary Entry"
      >
        <form onSubmit={handleAddDiaryEntry}>
          <div className="form-group">
            <label className="form-label">Diary Title / Subject</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Counter Affidavit Filed / Arguments Concluded"
              value={diaryTitle}
              onChange={(e) => setDiaryTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Entry Type</label>
              <select
                className="form-control"
                value={diaryType}
                onChange={(e) => setDiaryType(e.target.value)}
              >
                <option value="COURT_PROCEEDING">Court Proceeding</option>
                <option value="CLIENT_MEETING">Client Meeting</option>
                <option value="DOCUMENT_FILING">Document Filing</option>
                <option value="ORDER_SUMMARY">Order Summary</option>
                <option value="TASK_REMINDER">Task Reminder</option>
                <option value="OTHER">Other Note</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Visibility</label>
              <select
                className="form-control"
                value={diaryVis}
                onChange={(e) => setDiaryVis(e.target.value)}
              >
                <option value="SHARED">Shared (Visible to Client &amp; Lawyer)</option>
                <option value="LAWYER_ONLY">Lawyer Only (Confidential)</option>
                <option value="CLIENT_PRIVATE">Client Private</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Proceeding Description &amp; Details</label>
            <textarea
              className="form-control"
              rows="4"
              placeholder="Record details of what transpired in court or consultation..."
              value={diaryDesc}
              onChange={(e) => setDiaryDesc(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowAddDiary(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Entry
            </button>
          </div>
        </form>
      </Modal>

      {/* Schedule Hearing Modal */}
      <Modal
        isOpen={showAddHearing}
        onClose={() => setShowAddHearing(false)}
        title="Schedule Next Court Hearing"
      >
        <form onSubmit={handleAddHearing}>
          <div className="form-group">
            <label className="form-label">Hearing Date (YYYY-MM-DD)</label>
            <input
              type="date"
              className="form-control"
              value={hearingDate}
              onChange={(e) => setHearingDate(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Purpose of Hearing</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Cross Examination / Final Arguments / Framing of Issues"
              value={hearingPurpose}
              onChange={(e) => setHearingPurpose(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Judge / Bench (Optional)</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Principal District Judge"
              value={hearingJudge}
              onChange={(e) => setHearingJudge(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowAddHearing(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Schedule Hearing
            </button>
          </div>
        </form>
      </Modal>

      {/* Add Order Modal */}
      <Modal
        isOpen={showAddOrder}
        onClose={() => setShowAddOrder(false)}
        title="Record Court Order / Judgment"
      >
        <form onSubmit={handleAddOrder}>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Order Title</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. Interim Injunction Granted"
                value={orderTitle}
                onChange={(e) => setOrderTitle(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Order Type</label>
              <select
                className="form-control"
                value={orderType}
                onChange={(e) => setOrderType(e.target.value)}
              >
                <option value="INTERIM_ORDER">Interim Order (IA)</option>
                <option value="FINAL_JUDGMENT">Final Judgment / Decree</option>
                <option value="BAIL_ORDER">Bail Order</option>
                <option value="STAY_ORDER">Stay Order</option>
                <option value="NOTICE_ISSUANCE">Notice / Summons Order</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Date Pronounced</label>
            <input
              type="date"
              className="form-control"
              value={orderDate}
              onChange={(e) => setOrderDate(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Operative Summary &amp; Directions</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Brief summary of directions given by the Court..."
              value={orderSummary}
              onChange={(e) => setOrderSummary(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowAddOrder(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Court Order
            </button>
          </div>
        </form>
      </Modal>

      {/* Add Note Modal */}
      <Modal
        isOpen={showAddNote}
        onClose={() => setShowAddNote(false)}
        title="Create Case Note"
      >
        <form onSubmit={handleAddNote}>
          <div className="form-group">
            <label className="form-label">Note Title</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Legal Research on Limitation Period"
              value={noteTitle}
              onChange={(e) => setNoteTitle(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Visibility</label>
            <select
              className="form-control"
              value={noteVis}
              onChange={(e) => setNoteVis(e.target.value)}
            >
              <option value="LAWYER_ONLY">Lawyer Only (Confidential / Strategy)</option>
              <option value="SHARED">Shared with Client</option>
              <option value="CLIENT_PRIVATE">Client Private</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Content</label>
            <textarea
              className="form-control"
              rows="4"
              placeholder="Type your notes, citations, or client reminders here..."
              value={noteContent}
              onChange={(e) => setNoteContent(e.target.value)}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button type="button" onClick={() => setShowAddNote(false)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Note
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
