-- ===================================================================
-- LEGALTRACK DATABASE SCHEMA (MySQL 8.0)
-- Academic Legal Case Tracking, Lawyer Discovery & Legal-Aid Portal
-- ===================================================================

CREATE DATABASE IF NOT EXISTS legaltrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE legaltrack;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Authentication & Users
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_status (status)
) ENGINE=InnoDB;

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2. Geographic & Court Directory
DROP TABLE IF EXISTS police_stations;
DROP TABLE IF EXISTS courts;
DROP TABLE IF EXISTS court_complexes;
DROP TABLE IF EXISTS districts;
DROP TABLE IF EXISTS states;
DROP TABLE IF EXISTS case_types;

CREATE TABLE states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE districts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    CONSTRAINT fk_dist_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE CASCADE,
    UNIQUE KEY uk_state_district (state_id, name)
) ENGINE=InnoDB;

CREATE TABLE court_complexes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    address TEXT,
    CONSTRAINT fk_cc_district FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE courts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    court_complex_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    court_type VARCHAR(100) NOT NULL,
    judge_designation VARCHAR(150),
    CONSTRAINT fk_court_complex FOREIGN KEY (court_complex_id) REFERENCES court_complexes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE police_stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    district_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50),
    CONSTRAINT fk_ps_district FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE case_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    code VARCHAR(30) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT
) ENGINE=InnoDB;

-- 3. Profiles & Advocate Discovery
DROP TABLE IF EXISTS client_profiles;
DROP TABLE IF EXISTS lawyer_languages;
DROP TABLE IF EXISTS lawyer_courts;
DROP TABLE IF EXISTS lawyer_specializations;
DROP TABLE IF EXISTS lawyer_availability;
DROP TABLE IF EXISTS lawyer_profiles;

CREATE TABLE client_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    address TEXT,
    state_id BIGINT,
    district_id BIGINT,
    pincode VARCHAR(10),
    occupation VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL,
    CONSTRAINT fk_cp_district FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE lawyer_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    bar_registration_number VARCHAR(100) NOT NULL UNIQUE,
    enrollment_year INT NOT NULL,
    experience_years INT NOT NULL,
    state_id BIGINT,
    district_id BIGINT,
    office_address TEXT,
    bio TEXT,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_lp_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL,
    CONSTRAINT fk_lp_district FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE SET NULL,
    INDEX idx_lp_bar_reg (bar_registration_number),
    INDEX idx_lp_verified (verified)
) ENGINE=InnoDB;

CREATE TABLE lawyer_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lawyer_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACCEPTING_CLIENTS',
    available_from DATE,
    available_until DATE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_la_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer_profiles(id) ON DELETE CASCADE,
    INDEX idx_la_status (status)
) ENGINE=InnoDB;

CREATE TABLE lawyer_specializations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lawyer_id BIGINT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    CONSTRAINT fk_ls_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lawyer_courts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lawyer_id BIGINT NOT NULL,
    court_id BIGINT NOT NULL,
    CONSTRAINT fk_lc_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_lc_court FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lawyer_languages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lawyer_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    CONSTRAINT fk_ll_lawyer FOREIGN KEY (lawyer_id) REFERENCES lawyer_profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Case Files & Domain Sub-Entities
DROP TABLE IF EXISTS case_attention;
DROP TABLE IF EXISTS case_notes;
DROP TABLE IF EXISTS case_documents;
DROP TABLE IF EXISTS case_orders;
DROP TABLE IF EXISTS case_hearings;
DROP TABLE IF EXISTS case_diary_entries;
DROP TABLE IF EXISTS case_events;
DROP TABLE IF EXISTS case_advocates;
DROP TABLE IF EXISTS case_parties;
DROP TABLE IF EXISTS cases;

CREATE TABLE cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    internal_reference_id VARCHAR(100) NOT NULL UNIQUE,
    client_id BIGINT,
    lawyer_id BIGINT,
    court_id BIGINT NOT NULL,
    state_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    court_complex_id BIGINT,
    case_type VARCHAR(100) NOT NULL,
    case_category VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    cnr_number VARCHAR(50) UNIQUE,
    case_number VARCHAR(100),
    filing_number VARCHAR(100),
    filing_date DATE,
    registration_number VARCHAR(100),
    registration_date DATE,
    fir_number VARCHAR(100),
    fir_year INT,
    police_station_id BIGINT,
    act VARCHAR(200),
    section VARCHAR(200),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    stage VARCHAR(50) NOT NULL DEFAULT 'FILING',
    matter_description TEXT,
    engagement_type VARCHAR(50) NOT NULL DEFAULT 'PRIVATE_LAWYER',
    is_demo_data BOOLEAN DEFAULT TRUE,
    next_hearing_date DATE,
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_case_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_case_lawyer FOREIGN KEY (lawyer_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_case_court FOREIGN KEY (court_id) REFERENCES courts(id),
    CONSTRAINT fk_case_state FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT fk_case_district FOREIGN KEY (district_id) REFERENCES districts(id),
    CONSTRAINT fk_case_complex FOREIGN KEY (court_complex_id) REFERENCES court_complexes(id) ON DELETE SET NULL,
    CONSTRAINT fk_case_ps FOREIGN KEY (police_station_id) REFERENCES police_stations(id) ON DELETE SET NULL,
    INDEX idx_case_cnr (cnr_number),
    INDEX idx_case_number (case_number),
    INDEX idx_case_filing (filing_number),
    INDEX idx_case_fir (fir_number),
    INDEX idx_case_status (status),
    INDEX idx_case_next_hearing (next_hearing_date)
) ENGINE=InnoDB;

CREATE TABLE case_parties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    party_type VARCHAR(50) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    contact_info VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cp_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE case_advocates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    advocate_name VARCHAR(150) NOT NULL,
    registration_number VARCHAR(100),
    party_represented VARCHAR(200) NOT NULL,
    role VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ca_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE case_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_title VARCHAR(200) NOT NULL,
    event_description TEXT,
    event_date TIMESTAMP NOT NULL,
    source VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ce_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_ce_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE case_diary_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    entry_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    visibility VARCHAR(50) NOT NULL DEFAULT 'SHARED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cde_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_cde_user FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE case_hearings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    hearing_date DATE NOT NULL,
    hearing_time VARCHAR(20),
    court_id BIGINT,
    judge_name VARCHAR(150),
    purpose VARCHAR(255),
    stage VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    next_hearing_date DATE,
    source VARCHAR(50) DEFAULT 'MANUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ch_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_ch_court FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE SET NULL,
    INDEX idx_ch_hearing_date (hearing_date)
) ENGINE=InnoDB;

CREATE TABLE case_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    title VARCHAR(255) NOT NULL,
    order_type VARCHAR(50) NOT NULL,
    summary TEXT,
    document_id BIGINT,
    source VARCHAR(50) DEFAULT 'MANUAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_co_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE case_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    checksum VARCHAR(100),
    visibility VARCHAR(50) NOT NULL DEFAULT 'CLIENT_AND_LAWYER',
    version INT NOT NULL DEFAULT 1,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cdoc_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_cdoc_user FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE case_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    visibility VARCHAR(50) NOT NULL DEFAULT 'SHARED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cn_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_cn_user FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE case_attention (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'WARNING',
    action_url VARCHAR(255),
    resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_ca_att_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tracked Cases (Citizen Case Monitoring)
DROP TABLE IF EXISTS tracked_cases;
CREATE TABLE tracked_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    case_id BIGINT NOT NULL,
    tracked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notifications_enabled BOOLEAN DEFAULT TRUE,
    nickname VARCHAR(150),
    last_viewed_at TIMESTAMP NULL,
    UNIQUE KEY uk_user_case (user_id, case_id),
    CONSTRAINT fk_tc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE,
    INDEX idx_tc_user (user_id)
) ENGINE=InnoDB;

-- 6. Lawyer Requests & Relationships
DROP TABLE IF EXISTS client_lawyer_relationships;
DROP TABLE IF EXISTS lawyer_requests;

CREATE TABLE lawyer_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    lawyer_id BIGINT NOT NULL,
    case_type VARCHAR(100) NOT NULL,
    court_id BIGINT,
    message TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lr_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_lr_lawyer FOREIGN KEY (lawyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_lr_court FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE client_lawyer_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    lawyer_id BIGINT NOT NULL,
    case_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    CONSTRAINT fk_clr_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_clr_lawyer FOREIGN KEY (lawyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_clr_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 7. Legal Aid System
DROP TABLE IF EXISTS legal_aid_assignments;
DROP TABLE IF EXISTS legal_aid_status_history;
DROP TABLE IF EXISTS legal_aid_documents;
DROP TABLE IF EXISTS legal_aid_applications;
DROP TABLE IF EXISTS legal_aid_eligibility_rules;

CREATE TABLE legal_aid_eligibility_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    authority VARCHAR(150) NOT NULL,
    state_id BIGINT,
    category VARCHAR(100) NOT NULL,
    income_limit DECIMAL(12,2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_lar_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE legal_aid_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(100) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    court_id BIGINT,
    full_name VARCHAR(150) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    address TEXT NOT NULL,
    state_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    case_type VARCHAR(100) NOT NULL,
    case_stage VARCHAR(100),
    matter_description TEXT NOT NULL,
    opponent_information TEXT,
    annual_income DECIMAL(12,2) NOT NULL,
    employment_status VARCHAR(50) NOT NULL,
    selected_category VARCHAR(100) NOT NULL,
    supporting_information TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    officer_remarks TEXT,
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_laa_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_laa_court FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE SET NULL,
    CONSTRAINT fk_laa_state FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT fk_laa_district FOREIGN KEY (district_id) REFERENCES districts(id)
) ENGINE=InnoDB;

CREATE TABLE legal_aid_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    remarks TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP NULL,
    CONSTRAINT fk_lad_app FOREIGN KEY (application_id) REFERENCES legal_aid_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE legal_aid_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    changed_by BIGINT,
    remarks TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lash_app FOREIGN KEY (application_id) REFERENCES legal_aid_applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_lash_user FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE legal_aid_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL UNIQUE,
    lawyer_id BIGINT NOT NULL,
    assigned_by BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    case_id BIGINT,
    CONSTRAINT fk_laas_app FOREIGN KEY (application_id) REFERENCES legal_aid_applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_laas_lawyer FOREIGN KEY (lawyer_id) REFERENCES users(id),
    CONSTRAINT fk_laas_officer FOREIGN KEY (assigned_by) REFERENCES users(id),
    CONSTRAINT fk_laas_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 8. Messaging & Conversations
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;

CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    lawyer_id BIGINT NOT NULL,
    case_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conv_client FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conv_lawyer FOREIGN KEY (lawyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conv_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message_type VARCHAR(30) NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    attachment_id BIGINT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- 9. Notifications, Support & Audit
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS support_messages;
DROP TABLE IF EXISTS support_tickets;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS case_sync_logs;

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    action_url VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user_read (user_id, is_read)
) ENGINE=InnoDB;

CREATE TABLE support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_st_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE support_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sm_ticket FOREIGN KEY (ticket_id) REFERENCES support_tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_sm_sender FOREIGN KEY (sender_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_action (action),
    INDEX idx_audit_timestamp (timestamp)
) ENGINE=InnoDB;

CREATE TABLE case_sync_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    status VARCHAR(50) NOT NULL,
    records_updated INT DEFAULT 0,
    error_message TEXT,
    source VARCHAR(50) NOT NULL,
    CONSTRAINT fk_csl_case FOREIGN KEY (case_id) REFERENCES cases(id) ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
