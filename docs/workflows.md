# LEGALTRACK — Core System Workflows & User Journeys

This document outlines the end-to-end procedural workflows implemented in **LEGALTRACK**.

---

## 1. Universal Case Tracking Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Frontend as React Portal
    participant CaseCtrl as CaseController
    participant CaseSvc as CaseService
    participant MockCourt as MockCourtDataProvider
    participant DB as MySQL Database

    Client->>Frontend: Enter 16-Digit CNR / Case No / FIR
    Frontend->>CaseCtrl: POST /api/cases/track/cnr { cnrNumber: "DEMO123456" }
    CaseCtrl->>CaseSvc: searchAndTrackByCnr("DEMO123456")
    alt Case exists in Local DB
        CaseSvc->>DB: Query CaseFile by CNR
        DB-->>CaseSvc: Return Case Record
    else Query External Provider
        CaseSvc->>MockCourt: searchByCnr("DEMO123456")
        MockCourt-->>CaseSvc: Return Mock Court Case Payload
        CaseSvc->>DB: Persist Synced Case Record
    end
    CaseSvc-->>CaseCtrl: CaseResponse DTO
    CaseCtrl-->>Frontend: 200 OK + Case Details
    Frontend-->>Client: Display Procedural Stage Stepper, Events, Hearings, Orders
```

---

## 2. Advocate Discovery & Engagement Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Frontend as React Portal
    participant LawyerCtrl as LawyerController
    participant LawyerSvc as LawyerService
    participant ReqSvc as LawyerRequestService
    actor Lawyer

    Client->>Frontend: Select District, Court Complex, Case Type & Experience
    Frontend->>LawyerCtrl: GET /api/lawyers/search?districtId=1&specialization=Civil
    LawyerCtrl->>LawyerSvc: searchLawyers(spec, pageable)
    LawyerSvc-->>Frontend: Paginated Lawyer Cards with Availability Badges
    Client->>Frontend: Click "Request Representation" & submit case summary
    Frontend->>ReqSvc: POST /api/lawyer-requests { lawyerId, caseType, message }
    ReqSvc-->>Lawyer: In-App Notification: "New Representation Request"
    Lawyer->>Frontend: Review Request & click "Accept"
    Frontend->>ReqSvc: PATCH /api/lawyer-requests/{id}/accept
    ReqSvc->>ReqSvc: Create ClientLawyerRelationship + Conversation
    ReqSvc-->>Client: Notification: "Advocate Accepted Request"
```

---

## 3. Statutory Legal Aid Application Workflow

```mermaid
sequenceDiagram
    autonumber
    actor Citizen
    participant Wizard as Legal Aid Pre-Check Wizard
    participant LegalAidSvc as LegalAidService
    actor Officer as Legal Aid Officer (DLSA)
    actor PanelLawyer as Assigned Panel Advocate

    Citizen->>Wizard: Complete 3-Step Eligibility Check (Category / Income / Case)
    Wizard->>LegalAidSvc: POST /api/legal-aid/pre-check
    LegalAidSvc-->>Wizard: Potentially Eligible (Sec 12 LSA Act)
    Citizen->>Wizard: Fill Full Application & Upload Income / Identity Proofs
    Wizard->>LegalAidSvc: POST /api/legal-aid/applications
    LegalAidSvc-->>Officer: Application Assigned to District Queue (UNDER_REVIEW)
    Officer->>LegalAidSvc: Review Application & Verify Certificates
    Officer->>LegalAidSvc: POST /api/legal-aid/applications/{id}/assign { lawyerId }
    LegalAidSvc->>LegalAidSvc: Transition Status to LAWYER_ASSIGNED & Create Case Workspace
    LegalAidSvc-->>Citizen: Notification: "Panel Advocate Assigned"
    LegalAidSvc-->>PanelLawyer: Notification: "New Legal Aid Matter Assigned"
```

---

## 4. Case Attention Rule Engine & Sync Flow

```mermaid
sequenceDiagram
    autonumber
    participant Scheduler as CaseSyncScheduler
    participant SyncSvc as CaseSyncService
    participant AttentionSvc as CaseAttentionService
    participant DiarySvc as CaseDiaryService
    participant NotifSvc as NotificationService

    Scheduler->>SyncSvc: Trigger Scheduled Sync Job
    SyncSvc->>SyncSvc: Compare Hearing Dates & Court Order Timestamps
    alt Change Detected (Hearing Rescheduled / Order Uploaded)
        SyncSvc->>DiarySvc: Create CaseDiaryEntry (SYSTEM_UPDATE)
        SyncSvc->>AttentionSvc: Evaluate Rules & Generate CaseAttention Item
        SyncSvc->>NotifSvc: Dispatch Notification to Client & Lawyer
    end
```
