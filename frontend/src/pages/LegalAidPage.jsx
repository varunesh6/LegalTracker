import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { legalAidService } from '../services/legalAidService';
import { courtDirectoryService } from '../services/courtDirectoryService';
import { useAuth } from '../context/AuthContext';
import {
  ShieldAlert,
  Calculator,
  CheckCircle2,
  XCircle,
  FileText,
  Building,
  User,
  ArrowRight,
  AlertCircle,
  UploadCloud
} from 'lucide-react';

export const LegalAidPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();

  const [step, setStep] = useState(1); // 1: Eligibility Check, 2: Result, 3: Full Application

  // Pre-check fields
  const [category, setCategory] = useState('WOMAN_OR_CHILD');
  const [annualIncome, setAnnualIncome] = useState('200000');
  const [caseType, setCaseType] = useState('Civil / Property Suit');
  const [reason, setReason] = useState('');
  const [evaluating, setEvaluating] = useState(false);
  const [evalResult, setEvalResult] = useState(null);
  const [error, setError] = useState('');

  // Full Application Fields
  const [fullName, setFullName] = useState(user?.name || '');
  const [dob, setDob] = useState('1995-01-01');
  const [gender, setGender] = useState('FEMALE');
  const [phone, setPhone] = useState(user?.mobile || '');
  const [email, setEmail] = useState(user?.email || '');
  const [address, setAddress] = useState('');
  const [stateId, setStateId] = useState('');
  const [districtId, setDistrictId] = useState('');
  const [opponentInfo, setOpponentInfo] = useState('');
  const [matterDescription, setMatterDescription] = useState('');
  const [employmentStatus, setEmploymentStatus] = useState('Employed / Self-Employed');

  // Master data
  const [states, setStates] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [submittingApp, setSubmittingApp] = useState(false);
  const [appSubmitted, setAppSubmitted] = useState(null);

  useEffect(() => {
    courtDirectoryService.getStates().then((res) => {
      if (res?.success && res.data.length > 0) {
        setStates(res.data);
        setStateId(res.data[0].id);
        courtDirectoryService.getDistricts(res.data[0].id).then((dRes) => {
          if (dRes?.success && dRes.data.length > 0) {
            setDistricts(dRes.data);
            setDistrictId(dRes.data[0].id);
          }
        });
      }
    });
  }, []);

  const handlePreCheck = async (e) => {
    e.preventDefault();
    setEvaluating(true);
    setError('');

    try {
      const res = await legalAidService.preCheckEligibility({
        category,
        annualIncome: Number(annualIncome),
        caseType,
        reasonForAssistance: reason,
        stateId: stateId ? Number(stateId) : null,
        districtId: districtId ? Number(districtId) : null
      });

      if (res?.success) {
        setEvalResult(res.data);
        setStep(2);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Eligibility check failed');
    } finally {
      setEvaluating(false);
    }
  };

  const handleFullApplicationSubmit = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setSubmittingApp(true);
    setError('');

    try {
      const payload = {
        fullName,
        dateOfBirth: dob,
        gender,
        phone,
        email,
        address,
        stateId: Number(stateId),
        districtId: Number(districtId),
        caseType,
        matterDescription,
        opponentInformation: opponentInfo,
        annualIncome: Number(annualIncome),
        employmentStatus,
        selectedCategory: category,
        supportingInformation: reason
      };

      const res = await legalAidService.apply(payload);
      if (res?.success) {
        setAppSubmitted(res.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Application submission failed');
    } finally {
      setSubmittingApp(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
        <h1 style={{ fontSize: '2.25rem', marginBottom: '0.5rem' }}>Free Legal Aid Assistance</h1>
        <p>Statutory legal representation under Legal Services Authorities Act (NALSA / TNSLSA).</p>
      </div>

      {/* Step Stepper Header */}
      <div style={{ display: 'flex', justifyContent: 'center', gap: '2rem', marginBottom: '2.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: step >= 1 ? 'var(--primary)' : 'var(--text-muted)' }}>
          <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: step >= 1 ? 'var(--primary)' : 'var(--bg-tertiary)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: '0.85rem' }}>1</div>
          <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>Eligibility Check</span>
        </div>
        <div style={{ width: '40px', height: '2px', background: 'var(--border-color)', alignSelf: 'center' }} />
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: step >= 2 ? 'var(--primary)' : 'var(--text-muted)' }}>
          <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: step >= 2 ? 'var(--primary)' : 'var(--bg-tertiary)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: '0.85rem' }}>2</div>
          <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>Evaluation Result</span>
        </div>
        <div style={{ width: '40px', height: '2px', background: 'var(--border-color)', alignSelf: 'center' }} />
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: step >= 3 ? 'var(--primary)' : 'var(--text-muted)' }}>
          <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: step >= 3 ? 'var(--primary)' : 'var(--bg-tertiary)', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 700, fontSize: '0.85rem' }}>3</div>
          <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>Apply for Legal Aid</span>
        </div>
      </div>

      {error && (
        <div style={{ background: 'var(--danger-light)', border: '1px solid var(--danger)', padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)', color: 'var(--danger)', fontSize: '0.85rem', marginBottom: '1.5rem', maxWidth: '750px', margin: '0 auto 1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <AlertCircle size={16} />
          <span>{error}</span>
        </div>
      )}

      {/* Step 1: Pre-check Wizard Form */}
      {step === 1 && (
        <div className="glass-card" style={{ maxWidth: '750px', margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border-color)' }}>
            <Calculator size={24} color="var(--primary)" />
            <div>
              <h3 style={{ fontSize: '1.2rem' }}>Eligibility Pre-Check Calculator</h3>
              <p style={{ fontSize: '0.85rem' }}>Evaluate if your case qualifies under Section 12 criteria.</p>
            </div>
          </div>

          <form onSubmit={handlePreCheck}>
            <div className="form-group">
              <label className="form-label">Qualifying Statutory Category</label>
              <select
                className="form-control"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                <option value="WOMAN_OR_CHILD">Woman or Child (Categorically Eligible)</option>
                <option value="SCHEDULED_CASTE_OR_TRIBE">Member of Scheduled Caste / Scheduled Tribe (SC/ST)</option>
                <option value="PERSON_WITH_DISABILITY">Person with Disability (PwD)</option>
                <option value="INDUSTRIAL_WORKMAN">Industrial Workman</option>
                <option value="PERSON_IN_CUSTODY">Person in Custody / Under-Trial</option>
                <option value="VICTIM_OF_TRAFFICKING_OR_BEGAR">Victim of Human Trafficking / Begar</option>
                <option value="VICTIM_OF_DISASTER">Victim of Natural Disaster / Caste Violence</option>
                <option value="LOW_INCOME_GENERAL">Low Income Citizen (Income &le; ₹3,00,000 / year)</option>
                <option value="OTHER_ELIGIBLE_CATEGORY">Other Eligible Category</option>
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Annual Family Income (INR ₹)</label>
                <input
                  type="number"
                  className="form-control"
                  value={annualIncome}
                  onChange={(e) => setAnnualIncome(e.target.value)}
                  placeholder="e.g. 180000"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Matter / Case Nature</label>
                <input
                  type="text"
                  className="form-control"
                  value={caseType}
                  onChange={(e) => setCaseType(e.target.value)}
                  placeholder="e.g. Domestic Violence / Land Dispute"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Brief Reason for Assistance (Optional)</label>
              <textarea
                className="form-control"
                rows="3"
                placeholder="Describe your current circumstance and why free legal representation is required..."
                value={reason}
                onChange={(e) => setReason(e.target.value)}
              />
            </div>

            <button type="submit" disabled={evaluating} className="btn btn-primary btn-lg" style={{ width: '100%', marginTop: '1rem' }}>
              <Calculator size={18} />
              <span>{evaluating ? 'Evaluating Statutory Criteria...' : 'Evaluate Eligibility'}</span>
            </button>
          </form>
        </div>
      )}

      {/* Step 2: Evaluation Result */}
      {step === 2 && evalResult && (
        <div className="glass-card" style={{ maxWidth: '750px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', padding: '1rem 0 1.5rem', borderBottom: '1px solid var(--border-color)', marginBottom: '1.5rem' }}>
            {evalResult.eligible ? (
              <>
                <CheckCircle2 size={56} color="var(--success)" style={{ margin: '0 auto 1rem' }} />
                <h2 style={{ fontSize: '1.5rem', color: 'var(--success)', marginBottom: '0.5rem' }}>Potentially Eligible for Free Legal Aid</h2>
                <p style={{ maxWidth: '600px', margin: '0 auto' }}>{evalResult.message}</p>
              </>
            ) : (
              <>
                <AlertCircle size={56} color="var(--warning)" style={{ margin: '0 auto 1rem' }} />
                <h2 style={{ fontSize: '1.5rem', color: 'var(--warning)', marginBottom: '0.5rem' }}>Special Review Required</h2>
                <p style={{ maxWidth: '600px', margin: '0 auto' }}>{evalResult.message}</p>
              </>
            )}
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem' }}>Mandatory Documents Required for Submission</h4>
            <ul style={{ paddingLeft: '1.25rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.4rem', fontSize: '0.9rem' }}>
              {evalResult.requiredDocuments?.map((doc, idx) => (
                <li key={idx}>{doc}</li>
              ))}
            </ul>
          </div>

          <div style={{ background: 'var(--bg-glass)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', marginBottom: '1.5rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            <strong>Legal Disclaimer: </strong>{evalResult.disclaimer}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', gap: '1rem' }}>
            <button onClick={() => setStep(1)} className="btn btn-secondary">
              Recalculate
            </button>
            <button onClick={() => setStep(3)} className="btn btn-primary">
              <span>Proceed to Application Form</span>
              <ArrowRight size={16} />
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Full Application Form or Submission Success */}
      {step === 3 && (
        appSubmitted ? (
          <div className="glass-card" style={{ maxWidth: '750px', margin: '0 auto', textAlign: 'center', padding: '3rem 2rem' }}>
            <CheckCircle2 size={64} color="var(--success)" style={{ margin: '0 auto 1.25rem' }} />
            <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Application Submitted Successfully!</h2>
            <p style={{ fontSize: '1rem', marginBottom: '1.5rem' }}>
              Your application tracking reference is: <strong style={{ color: 'var(--primary)', fontFamily: 'var(--font-mono)' }}>{appSubmitted.applicationNumber}</strong>
            </p>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '2rem' }}>
              The District Legal Services Authority (DLSA) officer will review your income certificates and assign a panel advocate.
            </p>
            <button onClick={() => navigate('/client/legal-aid')} className="btn btn-primary">
              View Application Status
            </button>
          </div>
        ) : (
          <div className="glass-card" style={{ maxWidth: '750px', margin: '0 auto' }}>
            <div style={{ marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border-color)' }}>
              <h3 style={{ fontSize: '1.25rem', marginBottom: '0.25rem' }}>Formal Legal Aid Application Form</h3>
              <p style={{ fontSize: '0.85rem' }}>Provide applicant details for verification by the Legal Services Authority.</p>
            </div>

            <form onSubmit={handleFullApplicationSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Full Name of Applicant</label>
                  <input
                    type="text"
                    className="form-control"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Gender</label>
                  <select
                    className="form-control"
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                  >
                    <option value="FEMALE">Female</option>
                    <option value="MALE">Male</option>
                    <option value="TRANSGENDER">Transgender</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Mobile Number</label>
                  <input
                    type="tel"
                    className="form-control"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">District</label>
                  <select
                    className="form-control"
                    value={districtId}
                    onChange={(e) => setDistrictId(e.target.value)}
                  >
                    {districts.map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Employment Status</label>
                  <input
                    type="text"
                    className="form-control"
                    value={employmentStatus}
                    onChange={(e) => setEmploymentStatus(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Residential Address</label>
                <textarea
                  className="form-control"
                  rows="2"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  placeholder="Full door no, street name, and pincode..."
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Opponent / Opposite Party Information</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Landlord / Former Employer / Spouse / Organization"
                  value={opponentInfo}
                  onChange={(e) => setOpponentInfo(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Detailed Matter &amp; Dispute Description</label>
                <textarea
                  className="form-control"
                  rows="4"
                  placeholder="Explain the background facts, litigation status, and specific relief sought..."
                  value={matterDescription}
                  onChange={(e) => setMatterDescription(e.target.value)}
                  required
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '1.5rem' }}>
                <button type="button" onClick={() => setStep(2)} className="btn btn-secondary">
                  Back
                </button>
                <button type="submit" disabled={submittingApp} className="btn btn-primary">
                  <span>{submittingApp ? 'Submitting Application...' : 'Submit Application'}</span>
                </button>
              </div>
            </form>
          </div>
        )
      )}
    </div>
  );
};
