# LEGALTRACK — Architecture Specification

## 1. Architectural Overview

LEGALTRACK is designed as a layered, modular, and secure enterprise application adhering to clean architecture principles.

```
[ Frontend Client (React 18 + Vite) ]
                |
                | (REST APIs + WebSocket STOMP)
                v
[ Spring Security Layer (JWT Bearer Token Filter + RBAC) ]
                |
                v
[ REST Controller Layer (13 Controllers) ]
                |
                v
[ Service Layer & Rule Engine (17 Services + Case Attention Evaluator) ]
                |
                +---> [ File Storage Provider (MD5 Versioned Local Disk) ]
                |
                v
[ Data Access Layer (40 Spring Data JPA Repositories) ]
                |
                v
[ Persistence Layer (PostgreSQL / Embedded H2 / MySQL with 41 JPA Entities + Hibernate DDL) ]
```

---

## 2. Key Architectural Components

### A. Case Workspace & Chronological Diary
The Case Workspace acts as the aggregate root for all legal operations. Every procedural step produces an event recorded in `CaseEvent` and `CaseDiaryEntry`, establishing an immutable historical audit trail.

### B. Case Attention Rule Engine
The attention engine periodically inspects all active case files against configured operational rules:
1. **Upcoming Hearing Reminders**: Evaluated at $T-7$, $T-3$, and $T-1$ days before the scheduled date.
2. **Missing Court Orders**: Flags cases where hearing dates have passed but no corresponding `CaseOrder` has been recorded.
3. **Unassigned Legal Aid Matters**: Identifies approved applications awaiting panel lawyer assignment.

### C. Security & Authorization Matrix
Role-Based Access Control (RBAC) is enforced across 5 roles:
* `ROLE_CLIENT`: Access to personal cases, tracked cases, consult requests, and legal aid applications.
* `ROLE_LAWYER`: Access to client rosters, hearing cause lists, case diaries, and confidential strategy notes.
* `ROLE_LEGAL_AID_OFFICER`: Review queues, income certificate validation, and lawyer assignment authority.
* `ROLE_ADMIN`: Platform-wide analytics, user status controls, lawyer credential verification, and compliance audit logs.
* `ROLE_SUPPORT`: Help desk dispute handling and ticket resolution.

### D. File Storage & Versioning Architecture
Documents uploaded to the repository undergo MIME type validation, file size checks (&le; 15MB), and MD5 hash generation. Successive uploads for the same document family automatically increment the `versionNumber` while preserving the historical parent linkage.
