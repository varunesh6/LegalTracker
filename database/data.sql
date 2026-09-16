-- ===================================================================
-- LEGALTRACK SEED DATA (MySQL 8.0)
-- Academic Demo Dataset for Viva & Placement Demonstration
-- ===================================================================

USE legaltrack;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Roles
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_CLIENT'),
(2, 'ROLE_LAWYER'),
(3, 'ROLE_ADMIN'),
(4, 'ROLE_LEGAL_AID_OFFICER'),
(5, 'ROLE_SUPPORT');

-- 2. Geographic & Court Directory
INSERT INTO states (id, name, code) VALUES
(1, 'Tamil Nadu', 'TN'),
(2, 'Karnataka', 'KA'),
(3, 'Kerala', 'KL');

INSERT INTO districts (id, state_id, name, code) VALUES
(1, 1, 'Salem', 'SLM'),
(2, 1, 'Chennai', 'CHN'),
(3, 1, 'Namakkal', 'NMK'),
(4, 1, 'Erode', 'ERD'),
(5, 1, 'Coimbatore', 'CBE'),
(6, 1, 'Madurai', 'MDU');

INSERT INTO court_complexes (id, district_id, name, address) VALUES
(1, 1, 'Salem District Court Complex', 'Court Road, Hastampatti, Salem, Tamil Nadu 636007'),
(2, 2, 'Madras High Court & City Civil Court Complex', 'High Court Buildings, Parry\'s Corner, Chennai, Tamil Nadu 600104'),
(3, 5, 'Coimbatore Combined Court Complex', 'Arts College Road, Gopalapuram, Coimbatore, Tamil Nadu 641018');

INSERT INTO courts (id, court_complex_id, name, court_type, judge_designation) VALUES
(1, 1, 'Principal District & Sessions Court, Salem', 'DISTRICT_COURT', 'Principal District Judge'),
(2, 1, 'Sub Court, Salem', 'SUB_COURT', 'Subordinate Judge'),
(3, 1, 'Additional District Court (FTC), Salem', 'FAST_TRACK_COURT', 'Additional District Judge'),
(4, 2, 'City Civil Court (Court No. 1), Chennai', 'CITY_CIVIL_COURT', 'Principal Judge, City Civil Court'),
(5, 3, 'Principal District Court, Coimbatore', 'DISTRICT_COURT', 'Principal District Judge');

INSERT INTO police_stations (id, district_id, name, code) VALUES
(1, 1, 'Hasthampatti Police Station', 'SLM-PS-01'),
(2, 1, 'Salem Town Police Station', 'SLM-PS-02'),
(3, 1, 'Suramangalam Police Station', 'SLM-PS-03'),
(4, 2, 'Flower Bazaar Police Station', 'CHN-PS-01'),
(5, 5, 'Race Course Police Station', 'CBE-PS-01');

INSERT INTO case_types (id, name, code, category, description) VALUES
(1, 'Civil Suit (Original)', 'CS', 'CIVIL', 'Civil property & declaration suits'),
(2, 'Original Petition', 'OP', 'CIVIL', 'Succession, probate, arbitration'),
(3, 'Criminal Miscellaneous Petition', 'Crl.MP', 'CRIMINAL', 'Bail & anticipatory bail'),
(4, 'Calendar Case', 'CC', 'CRIMINAL', 'Trials on charge sheets'),
(5, 'Family Court Petition', 'HMOP', 'FAMILY', 'Matrimonial disputes & maintenance'),
(6, 'Motor Accident Claims', 'MCOP', 'MOTOR_ACCIDENT', 'Motor accident compensation claims'),
(7, 'Consumer Complaint', 'CC/DCDRC', 'CONSUMER', 'Consumer disputes');

-- 3. Users (BCrypt encoded password: password123 -> $2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa)
INSERT INTO users (id, name, email, password, mobile, status) VALUES
(1, 'System Administrator', 'admin@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543210', 'ACTIVE'),
(2, 'Legal Aid Officer - SLSA', 'officer@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543211', 'ACTIVE'),
(3, 'Adv. Kumar S.', 'lawyer1@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543212', 'ACTIVE'),
(4, 'Adv. Priya Lakshmi', 'lawyer2@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543213', 'ACTIVE'),
(5, 'Ramesh Babu', 'client@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543214', 'ACTIVE'),
(6, 'Customer Support Agent', 'support@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa', '9876543215', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 3), -- admin
(2, 4), -- officer
(3, 2), -- lawyer1
(4, 2), -- lawyer2
(5, 1), -- client
(6, 5); -- support

-- 4. Profiles
INSERT INTO client_profiles (id, user_id, address, state_id, district_id, pincode, occupation) VALUES
(1, 5, '45/12, Fairlands Main Road, Salem', 1, 1, '636016', 'Small Business Owner');

INSERT INTO lawyer_profiles (id, user_id, bar_registration_number, enrollment_year, experience_years, state_id, district_id, office_address, bio, verified) VALUES
(1, 3, 'MS/1420/2016', 2016, 10, 1, 1, 'Suite 3B, Advocate Chambers, Hasthampatti, Salem', 'Practicing advocate specializing in civil property disputes, land acquisition, succession, and contractual litigation in Salem courts for over 10 years.', TRUE),
(2, 4, 'MS/2890/2018', 2018, 8, 1, 1, '12A, Court Road, Opposite Sub Court, Salem', 'Dedicated advocate with extensive experience in family court matters, consumer protection, and property document verification.', TRUE);

INSERT INTO lawyer_availability (id, lawyer_id, status, available_from, available_until) VALUES
(1, 1, 'ACCEPTING_CLIENTS', '2026-01-01', '2026-12-31'),
(2, 2, 'ACCEPTING_CLIENTS', '2026-01-01', '2026-12-31');

INSERT INTO lawyer_specializations (id, lawyer_id, specialization) VALUES
(1, 1, 'Civil'),
(2, 1, 'Property & Real Estate'),
(3, 1, 'Land Acquisition'),
(4, 2, 'Family & Matrimonial'),
(5, 2, 'Civil'),
(6, 2, 'Consumer Protection');

INSERT INTO lawyer_courts (id, lawyer_id, court_id) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 1),
(4, 2, 2);

INSERT INTO lawyer_languages (id, lawyer_id, language) VALUES
(1, 1, 'Tamil'),
(2, 1, 'English'),
(3, 2, 'Tamil'),
(4, 2, 'English'),
(5, 2, 'Telugu');

-- 5. Legal Aid Eligibility Rules
INSERT INTO legal_aid_eligibility_rules (id, authority, state_id, category, income_limit, effective_from, active) VALUES
(1, 'Tamil Nadu SLSA', 1, 'WOMAN_OR_CHILD', 300000.00, '2025-01-01', TRUE),
(2, 'Tamil Nadu SLSA', 1, 'SCHEDULED_CASTE_OR_TRIBE', 300000.00, '2025-01-01', TRUE),
(3, 'Tamil Nadu SLSA', 1, 'PERSON_WITH_DISABILITY', 300000.00, '2025-01-01', TRUE),
(4, 'Tamil Nadu SLSA', 1, 'INDUSTRIAL_WORKMAN', 300000.00, '2025-01-01', TRUE),
(5, 'Tamil Nadu SLSA', 1, 'PERSON_IN_CUSTODY', 300000.00, '2025-01-01', TRUE),
(6, 'Tamil Nadu SLSA', 1, 'LOW_INCOME_GENERAL', 300000.00, '2025-01-01', TRUE);

-- 6. Demo Cases
INSERT INTO cases (id, internal_reference_id, client_id, lawyer_id, court_id, state_id, district_id, court_complex_id, case_type, case_category, title, cnr_number, case_number, filing_number, filing_date, registration_number, registration_date, act, section, status, stage, matter_description, engagement_type, is_demo_data, next_hearing_date) VALUES
(1, 'REF-DEMO-2026-001', 5, 3, 1, 1, 1, 1, 'Civil Suit (Original)', 'CIVIL', 'DEMO PROPERTY DISPUTE (Ramesh Babu vs K. Sundaram & Ors)', 'DEMO123456', 'CS/123/2026', 'FIL/894/2026', '2026-03-15', 'REG/123/2026', '2026-04-02', 'Transfer of Property Act, 1882', 'Section 54, Specific Relief Act Sec 34', 'PENDING', 'APPEARANCE', 'Suit for declaration of title and permanent injunction regarding ancestral agricultural land in Salem Taluk.', 'PRIVATE_LAWYER', TRUE, '2026-10-14'),
(2, 'REF-DEMO-2026-002', 5, 4, 1, 1, 1, 1, 'Original Petition', 'CIVIL', 'DEMO PARTITION MATTER (Ramesh Babu vs Legal Heirs of Murugesan)', 'DEMO234567', 'OP/45/2026', 'FIL/320/2026', '2026-05-10', 'REG/45/2026', '2026-05-20', 'Hindu Succession Act, 1956', 'Section 6', 'LISTED', 'EVIDENCE', 'Petition for partition of joint family residential property.', 'PRIVATE_LAWYER', TRUE, '2026-10-20');

-- Parties & Advocates
INSERT INTO case_parties (id, case_id, name, party_type, is_primary, contact_info) VALUES
(1, 1, 'Ramesh Babu', 'PLAINTIFF', TRUE, '9876543214'),
(2, 1, 'K. Sundaram', 'DEFENDANT', TRUE, 'Opposite Party'),
(3, 1, 'Village Administrative Officer, Salem', 'RESPONDENT', FALSE, 'Official'),
(4, 2, 'Ramesh Babu', 'PETITIONER', TRUE, '9876543214'),
(5, 2, 'S. Murugesan & 2 Others', 'RESPONDENT', TRUE, 'Salem');

INSERT INTO case_advocates (id, case_id, advocate_name, registration_number, party_represented, role) VALUES
(1, 1, 'Adv. Kumar S.', 'MS/1420/2016', 'Plaintiff (Ramesh Babu)', 'Lead Counsel'),
(2, 1, 'Adv. V. Natarajan', 'MS/889/2010', 'Defendant (K. Sundaram)', 'Opposing Counsel'),
(3, 2, 'Adv. Priya Lakshmi', 'MS/2890/2018', 'Petitioner (Ramesh Babu)', 'Lead Counsel');

-- Timeline Events
INSERT INTO case_events (id, case_id, event_type, event_title, event_description, event_date, source, created_by) VALUES
(1, 1, 'CASE_FILED', 'Case Filed', 'Civil Suit presented before Principal District Court registry.', '2026-03-15 10:30:00', 'MOCK_COURT_SYNC', 1),
(2, 1, 'CASE_REGISTERED', 'Case Registered & Numbered', 'Suit numbered as CS/123/2026.', '2026-04-02 11:00:00', 'MOCK_COURT_SYNC', 1),
(3, 1, 'FIRST_HEARING', 'First Hearing & Summons Issued', 'Summons ordered to defendant.', '2026-05-18 10:45:00', 'MOCK_COURT_SYNC', 1),
(4, 1, 'NOTICE_SERVED', 'Summons Served on Defendant', 'Service report submitted by Court Bailiff.', '2026-07-15 11:30:00', 'MOCK_COURT_SYNC', 1),
(5, 1, 'APPEARANCE', 'Appearance of Parties', 'Defendant appeared through counsel; posted for written statement.', '2026-09-30 12:00:00', 'MOCK_COURT_SYNC', 1);

-- Case Diary Entries
INSERT INTO case_diary_entries (id, case_id, created_by, entry_type, title, description, event_date, visibility) VALUES
(1, 1, 3, 'DOCUMENT_REQUEST', 'Lawyer requested Sale Deed', 'Adv. Kumar requested certified parent sale deed of survey no 142/3.', '2026-09-25 14:00:00', 'SHARED'),
(2, 1, 5, 'DOCUMENT_UPLOADED', 'Client uploaded Sale Deed', 'Ramesh Babu uploaded scanned PDF copy of 1998 registered sale deed.', '2026-09-27 16:30:00', 'SHARED'),
(3, 1, 3, 'HEARING', 'Hearing completed', 'Advocate argued for interim injunction extension; court directed filing of rejoinder.', '2026-09-30 13:00:00', 'SHARED'),
(4, 1, 3, 'HEARING', 'Next hearing scheduled: 14 Oct 2026', 'Matter adjourned to 14 Oct 2026 for framing of issues.', '2026-09-30 13:15:00', 'SHARED'),
(5, 1, 3, 'ORDER', 'Lawyer uploaded Order', 'Uploaded certified copy of Interim Injunction Extension Order.', '2026-10-01 10:00:00', 'SHARED');

-- Hearings & Orders
INSERT INTO case_hearings (id, case_id, hearing_date, hearing_time, court_id, judge_name, purpose, stage, status, notes, next_hearing_date) VALUES
(1, 1, '2026-09-30', '11:00 AM', 1, 'Thiru. K. Rajasekaran, PDJ', 'Filing of Written Statement & Rejoinder', 'Appearance', 'COMPLETED', 'Defendant filed written statement.', '2026-10-14'),
(2, 1, '2026-10-14', '10:30 AM', 1, 'Thiru. K. Rajasekaran, PDJ', 'Framing of Issues & Rejoinder', 'Issues', 'SCHEDULED', 'Counsel to submit proposed issues in property dispute.', NULL),
(3, 2, '2026-10-20', '11:30 AM', 1, 'Thiru. K. Rajasekaran, PDJ', 'Cross-examination of PW-1', 'Evidence', 'SCHEDULED', 'Petitioner evidence continuation.', NULL);

INSERT INTO case_orders (id, case_id, order_date, title, order_type, summary) VALUES
(1, 1, '2026-10-01', 'Interim Injunction Extension Order', 'INTERIM_ORDER', 'Status quo as on date of suit shall be maintained by both parties until next hearing date 14-10-2026.'),
(2, 1, '2026-05-18', 'Summons Issuance Order', 'PROCEDURAL_ORDER', 'Summons ordered to defendant on payment of process fee within 3 days.');

-- Attention Items
INSERT INTO case_attention (id, case_id, type, title, description, severity, action_url, resolved) VALUES
(1, 1, 'HEARING_SOON', 'Hearing in 3 days', 'Upcoming hearing on 14 Oct 2026 requires preparation of issues.', 'ACTION_REQUIRED', '/client/cases/1/hearings', FALSE),
(2, 1, 'DOCUMENT_REQUIRED', 'Document requested by lawyer', 'Adv. Kumar requested survey sketch approved by Revenue Inspector.', 'WARNING', '/client/cases/1/documents', FALSE),
(3, 1, 'NEW_ORDER_AVAILABLE', 'New order available', 'Interim Injunction Extension Order dated 01 Oct 2026 is available for download.', 'INFO', '/client/cases/1/orders', FALSE),
(4, 1, 'SYNC_SUCCESS', 'Latest case information synchronized', 'Court data refreshed with mock court provider repository.', 'INFO', '/client/cases/1', TRUE);

-- Tracked Cases
INSERT INTO tracked_cases (id, user_id, case_id, tracked_at, notifications_enabled, nickname) VALUES
(1, 5, 1, '2026-09-10 10:00:00', TRUE, 'Ancestral Land Dispute - Salem'),
(2, 5, 2, '2026-09-12 11:30:00', FALSE, 'Family Partition Matter');

-- Client Lawyer Relationships & Conversations
INSERT INTO client_lawyer_relationships (id, client_id, lawyer_id, case_id, status) VALUES
(1, 5, 3, 1, 'ACTIVE');

INSERT INTO conversations (id, client_id, lawyer_id, case_id) VALUES
(1, 5, 3, 1);

INSERT INTO messages (id, conversation_id, sender_id, message_type, content, sent_at) VALUES
(1, 1, 3, 'TEXT', 'Hello Mr. Ramesh, please upload the registered parent sale deed document for CS/123/2026 so we can prepare for the 14th Oct hearing.', '2026-09-24 10:00:00'),
(2, 1, 5, 'TEXT', 'I have uploaded the 1998 Sale Deed PDF to the case documents tab.', '2026-09-27 16:35:00'),
(3, 1, 3, 'TEXT', 'Thank you. I reviewed the deed. We are well prepared for the framing of issues.', '2026-09-28 11:00:00');

-- Legal Aid Application
INSERT INTO legal_aid_applications (id, application_number, client_id, court_id, full_name, date_of_birth, gender, phone, email, address, state_id, district_id, case_type, case_stage, matter_description, opponent_information, annual_income, employment_status, selected_category, supporting_information, status) VALUES
(1, 'LA-TN-2026-0001', 5, 1, 'Ramesh Babu', '1985-06-12', 'MALE', '9876543214', 'client@example.com', '45/12, Fairlands Main Road, Salem', 1, 1, 'Civil Suit (Original)', 'Pre-litigation / Filing Stage', 'Seeking legal aid assistance for defense against unlawful encroachment of residential plot.', 'Local builder claiming disputed right of way', 180000.00, 'SELF_EMPLOYED', 'LOW_INCOME_GENERAL', 'Annual family income certificate issued by Tahsildar submitted.', 'UNDER_REVIEW');

-- Notifications & Support
INSERT INTO notifications (id, user_id, type, title, message, reference_type, reference_id, action_url, is_read) VALUES
(1, 5, 'HEARING_REMINDER', 'Hearing in 3 Days', 'Your case CS/123/2026 has a scheduled hearing on 14 Oct 2026 at Principal District Court, Salem.', 'CASE', 1, '/client/cases/1/hearings', FALSE),
(2, 5, 'ORDER_AVAILABLE', 'New Order Uploaded', 'Adv. Kumar uploaded Interim Injunction Extension Order for CS/123/2026.', 'ORDER', 1, '/client/cases/1/orders', FALSE),
(3, 3, 'LAWYER_REQUEST', 'New Client Request', 'Ramesh Babu requested representation for Civil property dispute.', 'REQUEST', 1, '/lawyer/requests', TRUE);

INSERT INTO support_tickets (id, ticket_number, user_id, category, subject, description, priority, status) VALUES
(1, 'TKT-2026-0042', 5, 'CASE_TRACKING', 'Clarification regarding CNR tracking sync frequency', 'How often does the mock court synchronization update hearing records for tracked cases?', 'LOW', 'OPEN');

INSERT INTO support_messages (id, ticket_id, sender_id, message) VALUES
(1, 1, 5, 'Hi, I would like to know if CNR status updates occur daily or automatically upon hearing date changes.');

SET FOREIGN_KEY_CHECKS = 1;
