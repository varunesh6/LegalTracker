import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { caseService } from '../services/caseService';
import { trackedCaseService } from '../services/trackedCaseService';
import { courtDirectoryService } from '../services/courtDirectoryService';
import { useAuth } from '../context/AuthContext';
import { Timeline } from '../components/Timeline';
import { Badge } from '../components/Badge';
import {
  Search,
  BookmarkPlus,
  Check,
  Building,
  Calendar,
  User,
  FileText,
  AlertCircle,
  ExternalLink,
  ShieldCheck,
  RefreshCw
} from 'lucide-react';

export const TrackCasePage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [activeTab, setActiveTab] = useState('CNR'); // CNR, CASE_NO, FIR
  const [cnr, setCnr] = useState(searchParams.get('cnr') || '');

  // Case Number fields
  const [caseNumber, setCaseNumber] = useState('');
  const [courtId, setCourtId] = useState('');
  const [filingYear, setFilingYear] = useState(new Date().getFullYear().toString());

  // FIR fields
  const [firNumber, setFirNumber] = useState('');
  const [firYear, setFirYear] = useState(new Date().getFullYear().toString());
  const [policeStationId, setPoliceStationId] = useState('');

  // Master Data
  const [states, setStates] = useState([]);
  const [selectedStateId, setSelectedStateId] = useState('');
  const [districts, setDistricts] = useState([]);
  const [selectedDistrictId, setSelectedDistrictId] = useState('');
  const [courts, setCourts] = useState([]);
  const [policeStations, setPoliceStations] = useState([]);

  // Results & States
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [caseDetails, setCaseDetails] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [isTracking, setIsTracking] = useState(false);
  const [trackingSuccess, setTrackingSuccess] = useState(false);

  // Load States on mount
  useEffect(() => {
    courtDirectoryService.getStates().then((res) => {
      if (res?.success) {
        setStates(res.data);
        if (res.data.length > 0) setSelectedStateId(res.data[0].id);
      }
    }).catch(console.error);
  }, []);

  // Load Districts when state changes
  useEffect(() => {
    if (selectedStateId) {
      courtDirectoryService.getDistricts(selectedStateId).then((res) => {
        if (res?.success) {
          setDistricts(res.data);
          if (res.data.length > 0) setSelectedDistrictId(res.data[0].id);
        }
      }).catch(console.error);
    }
  }, [selectedStateId]);

  // Load Courts and Police Stations when district changes
  useEffect(() => {
    if (selectedDistrictId) {
      courtDirectoryService.getCourtsByDistrict(selectedDistrictId).then((res) => {
        if (res?.success && res.data.length > 0) {
          setCourts(res.data);
          setCourtId(res.data[0].id);
        }
      }).catch(console.error);

      courtDirectoryService.getPoliceStations(selectedDistrictId).then((res) => {
        if (res?.success && res.data.length > 0) {
          setPoliceStations(res.data);
          setPoliceStationId(res.data[0].id);
        }
      }).catch(console.error);
    }
  }, [selectedDistrictId]);

  // Auto-search if CNR in query params
  useEffect(() => {
    const cnrQuery = searchParams.get('cnr');
    if (cnrQuery) {
      setCnr(cnrQuery);
      handleSearchCNR(cnrQuery);
    }
  }, [searchParams]);

  const handleSearchCNR = async (searchCnr) => {
    const targetCnr = searchCnr || cnr;
    if (!targetCnr.trim()) {
      setError('Please provide a CNR number');
      return;
    }

    setLoading(true);
    setError('');
    setCaseDetails(null);
    setTimeline(null);
    setTrackingSuccess(false);

    try {
      const res = await caseService.trackByCnr(targetCnr.trim());
      if (res?.success && res.data) {
        setCaseDetails(res.data);
        if (res.data.caseFile?.id) {
          fetchTimeline(res.data.caseFile.id);
        }
      } else {
        setError(res?.message || 'Case not found');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Case not found in court records. Verify the CNR number.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchCaseNo = async (e) => {
    e.preventDefault();
    if (!caseNumber.trim() || !courtId) {
      setError('Please provide both Case Number and Court');
      return;
    }

    setLoading(true);
    setError('');
    setCaseDetails(null);
    setTimeline(null);

    try {
      const res = await caseService.trackByCaseNumber({
        caseNumber: caseNumber.trim(),
        courtId: Number(courtId),
        filingYear: Number(filingYear)
      });
      if (res?.success && res.data) {
        setCaseDetails(res.data);
        if (res.data.caseFile?.id) fetchTimeline(res.data.caseFile.id);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Case not found for the given details');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchFir = async (e) => {
    e.preventDefault();
    if (!firNumber.trim() || !policeStationId) {
      setError('Please provide FIR Number and Police Station');
      return;
    }

    setLoading(true);
    setError('');
    setCaseDetails(null);
    setTimeline(null);

    try {
      const res = await caseService.trackByFir({
        firNumber: firNumber.trim(),
        policeStationId: Number(policeStationId),
        firYear: Number(firYear)
      });
      if (res?.success && res.data) {
        setCaseDetails(res.data);
        if (res.data.caseFile?.id) fetchTimeline(res.data.caseFile.id);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Case not found for the given FIR number');
    } finally {
      setLoading(false);
    }
  };

  const fetchTimeline = async (caseId) => {
    try {
      const res = await caseService.getCaseTimeline(caseId);
      if (res?.success) {
        setTimeline(res.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleTrackCase = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (!caseDetails?.caseFile?.id) return;

    setIsTracking(true);
    try {
      const res = await trackedCaseService.trackCase({
        caseId: caseDetails.caseFile.id,
        nickname: caseDetails.caseFile.title,
        notificationsEnabled: true
      });
      if (res?.success) {
        setTrackingSuccess(true);
        setCaseDetails((prev) => ({ ...prev, isTrackedByCurrentUser: true }));
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Could not track case');
    } finally {
      setIsTracking(false);
    }
  };

  const caseInfo = caseDetails?.caseFile;

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
        <h1 style={{ fontSize: '2.25rem', marginBottom: '0.5rem' }}>Universal Case Tracker</h1>
        <p>Track live case status, procedural stage, upcoming hearings, and court orders.</p>
      </div>

      {/* Multi-mode Search Hub */}
      <div className="search-hub" style={{ marginBottom: '3rem' }}>
        <div className="search-tabs">
          <button
            type="button"
            className={`search-tab-btn ${activeTab === 'CNR' ? 'active' : ''}`}
            onClick={() => { setActiveTab('CNR'); setError(''); }}
          >
            <Search size={16} /> By 16-Digit CNR
          </button>
          <button
            type="button"
            className={`search-tab-btn ${activeTab === 'CASE_NO' ? 'active' : ''}`}
            onClick={() => { setActiveTab('CASE_NO'); setError(''); }}
          >
            <Building size={16} /> By Case Number &amp; Court
          </button>
          <button
            type="button"
            className={`search-tab-btn ${activeTab === 'FIR' ? 'active' : ''}`}
            onClick={() => { setActiveTab('FIR'); setError(''); }}
          >
            <FileText size={16} /> By Police Station &amp; FIR
          </button>
        </div>

        {error && (
          <div style={{ background: 'var(--danger-light)', border: '1px solid var(--danger)', padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)', color: 'var(--danger)', fontSize: '0.85rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        {/* Tab 1: CNR Search */}
        {activeTab === 'CNR' && (
          <form onSubmit={(e) => { e.preventDefault(); handleSearchCNR(); }}>
            <div className="form-group">
              <label className="form-label">
                CNR Number (Case Record Number)
                <span className="form-label-optional">Format: 16 alphanumeric characters</span>
              </label>
              <div style={{ display: 'flex', gap: '0.75rem' }}>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. TNCH010012342024"
                  value={cnr}
                  onChange={(e) => setCnr(e.target.value)}
                  style={{ fontFamily: 'var(--font-mono)', letterSpacing: '1px' }}
                  required
                />
                <button type="submit" disabled={loading} className="btn btn-primary" style={{ minWidth: '130px' }}>
                  {loading ? <RefreshCw className="pulse-dot" size={16} /> : <Search size={16} />}
                  <span>{loading ? 'Searching...' : 'Search'}</span>
                </button>
              </div>
            </div>
          </form>
        )}

        {/* Tab 2: Case Number Search */}
        {activeTab === 'CASE_NO' && (
          <form onSubmit={handleSearchCaseNo}>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">District</label>
                <select
                  className="form-control"
                  value={selectedDistrictId}
                  onChange={(e) => setSelectedDistrictId(e.target.value)}
                >
                  {districts.map((d) => (
                    <option key={d.id} value={d.id}>{d.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Court Establishment</label>
                <select
                  className="form-control"
                  value={courtId}
                  onChange={(e) => setCourtId(e.target.value)}
                  required
                >
                  {courts.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Case Number (e.g. OS/102/2024)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. OS/102/2024"
                  value={caseNumber}
                  onChange={(e) => setCaseNumber(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Filing Year</label>
                <input
                  type="number"
                  className="form-control"
                  value={filingYear}
                  onChange={(e) => setFilingYear(e.target.value)}
                  required
                />
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ width: '100%', marginTop: '0.5rem' }}>
              {loading ? 'Searching...' : 'Search Case by Number'}
            </button>
          </form>
        )}

        {/* Tab 3: FIR Search */}
        {activeTab === 'FIR' && (
          <form onSubmit={handleSearchFir}>
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">District</label>
                <select
                  className="form-control"
                  value={selectedDistrictId}
                  onChange={(e) => setSelectedDistrictId(e.target.value)}
                >
                  {districts.map((d) => (
                    <option key={d.id} value={d.id}>{d.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Police Station</label>
                <select
                  className="form-control"
                  value={policeStationId}
                  onChange={(e) => setPoliceStationId(e.target.value)}
                  required
                >
                  {policeStations.map((ps) => (
                    <option key={ps.id} value={ps.id}>{ps.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">FIR / Crime Number</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 104/2024"
                  value={firNumber}
                  onChange={(e) => setFirNumber(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">FIR Year</label>
                <input
                  type="number"
                  className="form-control"
                  value={firYear}
                  onChange={(e) => setFirYear(e.target.value)}
                  required
                />
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ width: '100%', marginTop: '0.5rem' }}>
              {loading ? 'Searching...' : 'Search Case by FIR'}
            </button>
          </form>
        )}
      </div>

      {/* Case Details Display */}
      {caseInfo && (
        <div style={{ animation: 'fadeIn 0.3s ease-in' }}>
          {/* Header Card */}
          <div className="glass-card" style={{ marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
                  <Badge variant={caseInfo.status}>{caseInfo.status}</Badge>
                  <Badge variant={caseInfo.stage}>{caseInfo.stage}</Badge>
                  {caseInfo.caseCategory && <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{caseInfo.caseCategory}</span>}
                </div>
                <h2 style={{ fontSize: '1.5rem', marginBottom: '0.25rem' }}>{caseInfo.title}</h2>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.85rem', color: 'var(--primary)' }}>
                  CNR: {caseInfo.cnrNumber} | Case No: {caseInfo.caseNumber || 'N/A'}
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.75rem' }}>
                {caseDetails.isTrackedByCurrentUser || trackingSuccess ? (
                  <button className="btn btn-glass" disabled>
                    <Check size={16} color="var(--success)" />
                    <span>Tracked</span>
                  </button>
                ) : (
                  <button onClick={handleTrackCase} disabled={isTracking} className="btn btn-primary">
                    <BookmarkPlus size={16} />
                    <span>{isTracking ? 'Tracking...' : 'Track This Case'}</span>
                  </button>
                )}

                {isAuthenticated && caseInfo.id && (
                  <button onClick={() => navigate(`/cases/${caseInfo.id}`)} className="btn btn-secondary">
                    <span>Open Workspace</span>
                    <ExternalLink size={14} />
                  </button>
                )}
              </div>
            </div>

            {/* Quick Metadata Matrix */}
            <div className="grid-4" style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1.25rem' }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Court Establishment</span>
                <strong style={{ fontSize: '0.85rem' }}>{caseInfo.courtName || 'District Court'}</strong>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Filing Date</span>
                <strong style={{ fontSize: '0.85rem' }}>{caseInfo.filingDate || 'N/A'}</strong>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Next Hearing Date</span>
                <strong style={{ fontSize: '0.85rem', color: caseInfo.nextHearingDate ? 'var(--warning)' : 'var(--text-primary)' }}>
                  {caseInfo.nextHearingDate || 'Awaiting Schedule'}
                </strong>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Case Act / Section</span>
                <strong style={{ fontSize: '0.85rem' }}>{caseInfo.act ? `${caseInfo.act} §${caseInfo.section || ''}` : 'General Civil'}</strong>
              </div>
            </div>
          </div>

          {/* Timeline & Stages */}
          {timeline && <Timeline timeline={timeline} />}

          {/* Parties & Advocates Grid */}
          <div className="grid-2" style={{ marginTop: '1.5rem' }}>
            <div className="glass-card">
              <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <User size={18} color="var(--primary)" /> Parties
              </h4>
              {caseDetails.parties?.length === 0 ? (
                <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No party details listed.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {caseDetails.parties.map((p) => (
                    <div key={p.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem 0', borderBottom: '1px solid var(--border-color)' }}>
                      <div>
                        <strong style={{ fontSize: '0.875rem' }}>{p.name}</strong>
                        {p.isPrimary && <span style={{ fontSize: '0.7rem', marginLeft: '0.5rem', color: 'var(--primary)' }}>(Primary)</span>}
                      </div>
                      <Badge variant="primary">{p.partyType}</Badge>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="glass-card">
              <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldCheck size={18} color="var(--accent-gold)" /> Advocates
              </h4>
              {caseDetails.advocates?.length === 0 ? (
                <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No advocate details recorded.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {caseDetails.advocates.map((a) => (
                    <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem 0', borderBottom: '1px solid var(--border-color)' }}>
                      <div>
                        <strong style={{ fontSize: '0.875rem' }}>{a.advocateName}</strong>
                        {a.registrationNumber && <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{a.registrationNumber}</div>}
                      </div>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Rep: {a.partyRepresented || 'Party'}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
