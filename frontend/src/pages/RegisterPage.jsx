import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { courtDirectoryService } from '../services/courtDirectoryService';
import { Scale, User, Briefcase, Lock, Mail, Phone, MapPin, AlertCircle, CheckCircle2 } from 'lucide-react';

export const RegisterPage = () => {
  const [roleTab, setRoleTab] = useState('CLIENT'); // CLIENT or LAWYER
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [mobile, setMobile] = useState('');

  // Client specifics
  const [occupation, setOccupation] = useState('');
  const [address, setAddress] = useState('');
  const [pincode, setPincode] = useState('');

  // Lawyer specifics
  const [barRegistrationNumber, setBarRegistrationNumber] = useState('');
  const [enrollmentYear, setEnrollmentYear] = useState('2018');
  const [experienceYears, setExperienceYears] = useState('5');
  const [officeAddress, setOfficeAddress] = useState('');
  const [bio, setBio] = useState('');

  // Geography
  const [states, setStates] = useState([]);
  const [stateId, setStateId] = useState('');
  const [districts, setDistricts] = useState([]);
  const [districtId, setDistrictId] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { registerClient, registerLawyer } = useAuth();
  const navigate = useNavigate();

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

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (roleTab === 'CLIENT') {
        await registerClient({
          name: name.trim(),
          email: email.trim(),
          password,
          mobile: mobile.trim(),
          stateId: stateId ? Number(stateId) : null,
          districtId: districtId ? Number(districtId) : null,
          address,
          pincode,
          occupation
        });
        navigate('/client/dashboard');
      } else {
        await registerLawyer({
          name: name.trim(),
          email: email.trim(),
          password,
          mobile: mobile.trim(),
          barRegistrationNumber: barRegistrationNumber.trim(),
          enrollmentYear: Number(enrollmentYear),
          experienceYears: Number(experienceYears),
          stateId: Number(stateId),
          districtId: Number(districtId),
          officeAddress,
          bio,
          specializations: ['Civil Litigation', 'Property Law'],
          languages: ['English', 'Tamil']
        });
        navigate('/lawyer/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '3rem 0', display: 'flex', justifyContent: 'center' }}>
      <div className="glass-card" style={{ maxWidth: '680px', width: '100%', padding: '2.5rem' }}>
        
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Scale size={24} color="var(--primary)" />
            <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 800, fontSize: '1.25rem' }}>
              LEGALTRACK
            </span>
          </div>
          <h2 style={{ fontSize: '1.6rem', marginBottom: '0.25rem' }}>Create Your Account</h2>
          <p style={{ fontSize: '0.875rem' }}>Join the legal portal as a Citizen Client or Practicing Advocate.</p>
        </div>

        {/* Tab Selector */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '2rem', padding: '0.35rem', background: 'var(--bg-glass)', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border-color)' }}>
          <button
            type="button"
            className={`btn ${roleTab === 'CLIENT' ? 'btn-primary' : 'btn-glass'}`}
            onClick={() => { setRoleTab('CLIENT'); setError(''); }}
            style={{ borderRadius: 'var(--radius-md)' }}
          >
            <User size={16} />
            <span>Citizen / Client</span>
          </button>
          <button
            type="button"
            className={`btn ${roleTab === 'LAWYER' ? 'btn-primary' : 'btn-glass'}`}
            onClick={() => { setRoleTab('LAWYER'); setError(''); }}
            style={{ borderRadius: 'var(--radius-md)' }}
          >
            <Briefcase size={16} />
            <span>Advocate / Lawyer</span>
          </button>
        </div>

        {error && (
          <div style={{ background: 'var(--danger-light)', border: '1px solid var(--danger)', padding: '0.75rem', borderRadius: 'var(--radius-md)', color: 'var(--danger)', fontSize: '0.85rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleRegister}>
          {/* Common Fields */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Full Legal Name</label>
              <input
                type="text"
                className="form-control"
                placeholder={roleTab === 'CLIENT' ? 'e.g. Ramesh Babu' : 'e.g. Adv. Kumar S.'}
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Mobile Number</label>
              <input
                type="tel"
                className="form-control"
                placeholder="e.g. 9876543210"
                value={mobile}
                onChange={(e) => setMobile(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input
                type="email"
                className="form-control"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Create Password</label>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">State</label>
              <select
                className="form-control"
                value={stateId}
                onChange={(e) => setStateId(e.target.value)}
              >
                {states.map((s) => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </select>
            </div>

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
          </div>

          {/* Client Specifics */}
          {roleTab === 'CLIENT' && (
            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Occupation / Profession</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Business / Salaried"
                  value={occupation}
                  onChange={(e) => setOccupation(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Pincode</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 600001"
                  value={pincode}
                  onChange={(e) => setPincode(e.target.value)}
                />
              </div>
            </div>
          )}

          {/* Lawyer Specifics */}
          {roleTab === 'LAWYER' && (
            <>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Bar Registration No. (e.g. MS/1234/2018)</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="MS/1234/2018"
                    value={barRegistrationNumber}
                    onChange={(e) => setBarRegistrationNumber(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Years of Experience</label>
                  <input
                    type="number"
                    className="form-control"
                    value={experienceYears}
                    onChange={(e) => setExperienceYears(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Chambers / Office Address</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Chamber No. 42, High Court Complex, Chennai"
                  value={officeAddress}
                  onChange={(e) => setOfficeAddress(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Professional Summary &amp; Bio</label>
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Briefly describe your legal practice background..."
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                />
              </div>
            </>
          )}

          <button type="submit" disabled={loading} className="btn btn-primary btn-lg" style={{ width: '100%', marginTop: '1rem' }}>
            <span>{loading ? 'Creating Account...' : 'Complete Registration'}</span>
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Already have an account? <Link to="/login" style={{ fontWeight: 600 }}>Sign in</Link>
        </div>
      </div>
    </div>
  );
};
