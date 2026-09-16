import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { lawyerService } from '../services/lawyerService';
import { courtDirectoryService } from '../services/courtDirectoryService';
import { Badge } from '../components/Badge';
import {
  Search,
  MapPin,
  Briefcase,
  Award,
  Globe,
  CheckCircle2,
  Calendar,
  MessageSquare,
  ArrowRight,
  Filter
} from 'lucide-react';

export const FindLawyer = () => {
  const navigate = useNavigate();

  // Filters
  const [query, setQuery] = useState('');
  const [districtId, setDistrictId] = useState('');
  const [specialization, setSpecialization] = useState('');
  const [language, setLanguage] = useState('');
  const [minExp, setMinExp] = useState('');
  const [status, setStatus] = useState('');

  // Master Data
  const [states, setStates] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [lawyers, setLawyers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    courtDirectoryService.getStates().then((res) => {
      if (res?.success && res.data.length > 0) {
        setStates(res.data);
        courtDirectoryService.getDistricts(res.data[0].id).then((dRes) => {
          if (dRes?.success) setDistricts(dRes.data);
        });
      }
    });

    fetchLawyers();
  }, []);

  const fetchLawyers = async (customParams = {}) => {
    setLoading(true);
    try {
      const params = {
        query: customParams.query !== undefined ? customParams.query : query,
        districtId: customParams.districtId !== undefined ? customParams.districtId : districtId,
        specialization: customParams.specialization !== undefined ? customParams.specialization : specialization,
        language: customParams.language !== undefined ? customParams.language : language,
        minExperience: customParams.minExp !== undefined ? customParams.minExp : minExp,
        status: customParams.status !== undefined ? customParams.status : status,
        page: 0,
        size: 20
      };

      // Strip empty params
      Object.keys(params).forEach((key) => {
        if (!params[key]) delete params[key];
      });

      const res = await lawyerService.searchLawyers(params);
      if (res?.success) {
        setLawyers(res.data.content || []);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchLawyers();
  };

  const resetFilters = () => {
    setQuery('');
    setDistrictId('');
    setSpecialization('');
    setLanguage('');
    setMinExp('');
    setStatus('');
    fetchLawyers({ query: '', districtId: '', specialization: '', language: '', minExp: '', status: '' });
  };

  return (
    <div className="container" style={{ padding: '2rem 0' }}>
      <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
        <h1 style={{ fontSize: '2.25rem', marginBottom: '0.5rem' }}>Find Verified Legal Counsel</h1>
        <p>Discover experienced advocates across Tamil Nadu district courts and high courts.</p>
      </div>

      {/* Filter Matrix */}
      <div className="glass-card" style={{ marginBottom: '2.5rem' }}>
        <form onSubmit={handleSearch}>
          <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.25rem' }}>
            <div style={{ flex: 1, position: 'relative' }}>
              <input
                type="text"
                className="form-control"
                placeholder="Search by advocate name, bar council registration, or keywords..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            <button type="submit" disabled={loading} className="btn btn-primary" style={{ minWidth: '120px' }}>
              <Search size={16} /> Search
            </button>
            <button type="button" onClick={resetFilters} className="btn btn-secondary">
              Reset
            </button>
          </div>

          <div className="grid-4" style={{ gap: '1rem' }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">District</label>
              <select
                className="form-control"
                value={districtId}
                onChange={(e) => setDistrictId(e.target.value)}
              >
                <option value="">All Districts</option>
                {districts.map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Specialization</label>
              <select
                className="form-control"
                value={specialization}
                onChange={(e) => setSpecialization(e.target.value)}
              >
                <option value="">All Practice Areas</option>
                <option value="Civil Litigation">Civil Litigation</option>
                <option value="Criminal Defense">Criminal Defense</option>
                <option value="Family Law & Matrimonial">Family & Matrimonial</option>
                <option value="Property & Real Estate">Property & Real Estate</option>
                <option value="Corporate & Commercial">Corporate & Commercial</option>
                <option value="Constitutional Law">Constitutional Law</option>
                <option value="Labour & Service Law">Labour & Service</option>
              </select>
            </div>

            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Min Experience</label>
              <select
                className="form-control"
                value={minExp}
                onChange={(e) => setMinExp(e.target.value)}
              >
                <option value="">Any Experience</option>
                <option value="3">3+ Years</option>
                <option value="5">5+ Years</option>
                <option value="10">10+ Years</option>
                <option value="15">15+ Years</option>
              </select>
            </div>

            <div className="form-group" style={{ margin: 0 }}>
              <label className="form-label">Availability</label>
              <select
                className="form-control"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="">Any Status</option>
                <option value="ACCEPTING_CLIENTS">Accepting Clients</option>
                <option value="BUSY">Busy</option>
                <option value="CONSULTATION_ONLY">Consultation Only</option>
              </select>
            </div>
          </div>
        </form>
      </div>

      {/* Lawyers Grid */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem 0' }}>
          <div className="pulse-dot" style={{ width: '20px', height: '20px', margin: '0 auto 1rem', background: 'var(--primary)' }} />
          <p>Searching advocate directory...</p>
        </div>
      ) : lawyers.length === 0 ? (
        <div className="glass-card" style={{ textAlign: 'center', padding: '3rem 1.5rem' }}>
          <p style={{ color: 'var(--text-muted)' }}>No advocates matched the selected criteria.</p>
        </div>
      ) : (
        <div className="grid-3" style={{ gap: '1.5rem' }}>
          {lawyers.map((lawyer) => (
            <div
              key={lawyer.id}
              className="glass-card glass-card-interactive"
              onClick={() => navigate(`/lawyers/${lawyer.id}`)}
              style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <div
                      style={{
                        width: '48px',
                        height: '48px',
                        borderRadius: 'var(--radius-md)',
                        background: 'linear-gradient(135deg, var(--primary), var(--purple))',
                        color: '#fff',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 700,
                        fontSize: '1.2rem'
                      }}
                    >
                      {lawyer.name ? lawyer.name.charAt(0).toUpperCase() : 'L'}
                    </div>
                    <div>
                      <h3 style={{ fontSize: '1.1rem', marginBottom: '0.15rem' }}>{lawyer.name}</h3>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Bar No: {lawyer.barRegistrationNumber}
                      </span>
                    </div>
                  </div>

                  {lawyer.verified && (
                    <Badge variant="VERIFIED">
                      <CheckCircle2 size={12} /> Verified
                    </Badge>
                  )}
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.75rem' }}>
                  <MapPin size={14} color="var(--primary)" />
                  <span>{lawyer.districtName}, {lawyer.stateName}</span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '1rem' }}>
                  <Award size={14} color="var(--accent-gold)" />
                  <span>{lawyer.experienceYears} Years Practice Experience</span>
                </div>

                {/* Specialization Tags */}
                {lawyer.specializations && lawyer.specializations.length > 0 && (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem', marginBottom: '1rem' }}>
                    {lawyer.specializations.slice(0, 3).map((spec, i) => (
                      <span
                        key={i}
                        style={{
                          fontSize: '0.75rem',
                          background: 'var(--bg-glass)',
                          border: '1px solid var(--border-color)',
                          padding: '0.2rem 0.5rem',
                          borderRadius: 'var(--radius-sm)',
                          color: 'var(--text-secondary)'
                        }}
                      >
                        {spec}
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Badge variant={lawyer.availabilityStatus || 'ACCEPTING_CLIENTS'}>
                  {lawyer.availabilityStatus ? lawyer.availabilityStatus.replace('_', ' ') : 'Available'}
                </Badge>
                <span style={{ color: 'var(--primary)', fontSize: '0.85rem', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                  View Profile <ArrowRight size={14} />
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
