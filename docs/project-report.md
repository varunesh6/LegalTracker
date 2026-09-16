# LEGALTRACK — Academic Project Report & Design Document

## Title
**LEGALTRACK: Legal Case Tracking, Lawyer Discovery, Legal-Aid Assistance and Client-Lawyer Management Portal**

## Abstract
In the contemporary legal landscape, non-lawyer litigants and citizens encounter substantial obstacles when attempting to monitor multi-stage judicial proceedings across diverse district courts, locate competent legal counsel within their territorial jurisdiction, apply for statutory legal aid under the Legal Services Authorities Act, and maintain an organized, tamper-evident repository of case documents and proceedings. **LEGALTRACK** addresses these systemic challenges through a full-stack, enterprise-architected web application developed using **Java 17, Spring Boot 3, Spring Security (JWT + RBAC), React 18, and MySQL 8**. 

The system delivers unified case tracking via 16-character CNR numbers and FIR cross-referencing, a chronological Case Diary with immutable audit trails, an automated Case Attention rule engine providing proactive hearing alerts, a multi-parameter advocate discovery directory, an interactive legal aid eligibility assessment wizard, and secure 1-to-1 client-lawyer collaboration channels.

---

## 1. Introduction & Motivation
Litigants frequently rely on fragmented physical files, informal messaging, and intermittent court calendar checks to keep track of their ongoing litigation. This opacity leads to missed hearing dates, delayed rejoinder filings, and significant stress. Concurrently, citizens qualifying for statutory legal aid under Section 12 of the Legal Services Authorities Act struggle to understand qualifying parameters or submit documentation efficiently.

**LEGALTRACK** unites these disjointed functions into an integrated digital case workspace centered around active case tracking.

---

## 2. System Architecture & Methodology

### 2.1 Backend Architecture (Layered Hexagonal / N-Tier)
The backend conforms strictly to Spring enterprise architectural best practices:
1. **Controller Layer**: Handles REST endpoints, Swagger OpenAPI metadata, input validation via Jakarta Validation annotations, and HTTP response assembly.
2. **DTO & Mapper Layer**: Segregates persistent database entities from external API contracts, mitigating over-posting vulnerabilities.
3. **Service Layer**: Encapsulates transactional business logic, ownership access verification, and event orchestration.
4. **Repository Layer**: Utilizes Spring Data JPA with derived queries, specifications, and indexed lookups.
5. **Persistence Layer**: Normalized relational schema implemented in MySQL 8 with foreign key cascades and versioning columns for optimistic concurrency control.

### 2.2 Security Architecture
- **Authentication**: Stateless JSON Web Token (JWT) architecture with short-lived access tokens and refresh tokens.
- **Password Protection**: BCrypt key-derivation function with work factor 10.
- **Role-Based Access Control (RBAC)**: Fine-grained authorization utilizing `@PreAuthorize` across five roles (`ROLE_CLIENT`, `ROLE_LAWYER`, `ROLE_ADMIN`, `ROLE_LEGAL_AID_OFFICER`, `ROLE_SUPPORT`).
- **Resource Ownership Verification**: Service-level checks verifying that only authorized case participants (the assigned client or counsel) can access private diary entries, documents, or messaging streams.

---

## 3. Core Modules & Implementation Highlights

### 3.1 Case Tracking & Procedural Timeline
The Case Tracking module supports searching via CNR, Case Number, or FIR Number. It structures litigation into a 9-stage progression pipeline:
`Filing` &rarr; `Registration` &rarr; `Summons/Notice` &rarr; `Appearance` &rarr; `Pleadings` &rarr; `Issues` &rarr; `Evidence` &rarr; `Arguments` &rarr; `Orders/Judgment`.

### 3.2 Chronological Case Diary
Proceedings, document requests, upload receipts, and interim order uploads are automatically logged into the Case Diary with explicit visibility tags (`SHARED`, `LAWYER_ONLY`, `CLIENT_PRIVATE`), providing a clear chronological record of all proceedings.

### 3.3 Case Attention Rule Engine
The attention rule engine evaluates background conditions:
- Flags upcoming hearings at $T-7$, $T-3$, and $T-1$ days with severity ratings (`ACTION_REQUIRED`, `WARNING`, `INFO`).
- Highlights unaddressed document requests from legal counsel.
- Notifies users upon the release of newly synchronized court orders.

### 3.4 Statutory Legal Aid Assistance
An interactive 3-step assessment wizard evaluates applicant criteria against statutory rules (Women, Children, SC/ST, Persons in Custody, PwD, and Income $\le$ ₹3,00,000), facilitating formal application submission, Tahsildar income certificate uploads, and DLSA officer verification.

---

## 4. Verification & Testing
The system was validated using automated JUnit 5 and Mockito test suites:
- **Authentication & RBAC Tests**: Validated token issuance, invalid credentials rejection, and role-based endpoint restriction.
- **Service Layer Tests**: Validated case tracking, advocate discovery filters, and legal aid qualification rules.
- **Isolation Tests**: Verified that cross-tenant document and conversation access attempts correctly return 403 Forbidden status.

---

## 5. Conclusion
**LEGALTRACK** demonstrates a robust, full-stack approach to modernizing legal assistance workflows and case tracking. The application fulfills all academic project criteria, demonstrates industry standard design patterns, and provides an extensible foundation for future legal tech integrations.
