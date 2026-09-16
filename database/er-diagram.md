# LEGALTRACK — Database Entity Relationship (ER) Diagram

This document models the relational schema for the **LEGALTRACK** legal management and case tracking portal.

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : assigned
    USERS ||--o| CLIENT_PROFILES : owns
    USERS ||--o| LAWYER_PROFILES : owns
    USERS ||--o{ TRACKED_CASES : tracks
    USERS ||--o{ LAWYER_REQUESTS : initiates
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ SUPPORT_TICKETS : creates

    STATES ||--o{ DISTRICTS : contains
    DISTRICTS ||--o{ COURT_COMPLEXES : houses
    COURT_COMPLEXES ||--o{ COURTS : contains
    DISTRICTS ||--o{ POLICE_STATIONS : oversees

    LAWYER_PROFILES ||--o| LAWYER_AVAILABILITY : maintains
    LAWYER_PROFILES ||--o{ LAWYER_SPECIALIZATIONS : holds
    LAWYER_PROFILES ||--o{ LAWYER_COURTS : practices_in
    LAWYER_PROFILES ||--o{ LAWYER_LANGUAGES : speaks

    COURTS ||--o{ CASES : adjudicates
    USERS ||--o{ CASES : represented_as_client
    USERS ||--o{ CASES : represented_by_lawyer

    CASES ||--o{ CASE_PARTIES : contains
    CASES ||--o{ CASE_ADVOCATES : records
    CASES ||--o{ CASE_EVENTS : sequences
    CASES ||--o{ CASE_DIARY_ENTRIES : logs
    CASES ||--o{ CASE_HEARINGS : schedules
    CASES ||--o{ CASE_ORDERS : issues
    CASES ||--o{ CASE_DOCUMENTS : stores
    CASES ||--o{ CASE_ATTENTION : triggers
    CASES ||--o{ CASE_NOTES : contains
    CASES ||--o{ TRACKED_CASES : monitored_in

    LEGAL_AID_APPLICATIONS ||--o{ LEGAL_AID_DOCUMENTS : submits
    LEGAL_AID_APPLICATIONS ||--o{ LEGAL_AID_STATUS_HISTORY : transitions
    LEGAL_AID_APPLICATIONS ||--o| LEGAL_AID_ASSIGNMENTS : delegates
    LEGAL_AID_ELIGIBILITY_RULES ||--o{ LEGAL_AID_APPLICATIONS : governs

    CONVERSATIONS ||--o{ MESSAGES : exchanges
    SUPPORT_TICKETS ||--o{ SUPPORT_MESSAGES : updates
```

## Relational Invariants & Integrity Constraints

1. **User Isolation**: A user must have exactly one specific role profile (`ClientProfile` or `LawyerProfile`).
2. **Case Uniqueness**: A `(user_id, case_id)` pair in `tracked_cases` is unique to prevent duplicate case tracking entries.
3. **Optimistic Locking**: Key mutable entities (`CaseFile`, `LegalAidApplication`, `LawyerRequest`, `CaseDocument`) utilize version counters to prevent concurrent update anomalies.
4. **Visibility Level Isolation**: `CaseDiaryEntry`, `CaseDocument`, and `CaseNote` utilize explicit visibility enums (`SHARED`, `LAWYER_ONLY`, `CLIENT_PRIVATE`, `ADMIN_ONLY`) enforced at service and query boundaries.
