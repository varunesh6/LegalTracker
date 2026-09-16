# LEGALTRACK

### *"One place to discover legal assistance, manage case information and track a case."*

**LEGALTRACK** is an academic full-stack legal portal built to solve citizen case tracking challenges, facilitate advocate discovery across district courts, streamline statutory legal-aid applications, and provide structured digital case workspaces with chronological case diaries and rule-engine attention alerts.

---

## 🌟 Key Features

1. **Universal Case Tracking**
   * Multi-mode search via **16-Character CNR Number**, **Case Number & Court**, or **Police Station & FIR Number**.
   * Interactive **Procedural Stage Progression Stepper** (Filing &rarr; Registration &rarr; Summons/Notice &rarr; Appearance &rarr; Pleadings &rarr; Issues &rarr; Evidence &rarr; Arguments &rarr; Orders/Judgment).
   * Visual Case Event Chronology & Hearing History.

2. **Chronological Case Diary**
   * Immutable proceeding entries with creator role tracking.
   * Granular visibility levels (`SHARED`, `LAWYER_ONLY`, `CLIENT_PRIVATE`).
   * Automated system milestones recorded on case status and hearing updates.

3. **Case Attention Rule Engine**
   * Automated background evaluators flagging:
     * Upcoming hearings within 7, 3, and 1 days.
     * Overdue past hearings missing updated court orders.
     * Unassigned legal aid grants and pending client inquiries.

4. **Advocate Discovery Directory**
   * Search and filter verified advocates by **District**, **Court Complex**, **Specialization**, **Languages**, and **Experience**.
   * Real-time availability indicator (`ACCEPTING_CLIENTS`, `BUSY`, `CONSULTATION_ONLY`, `ON_LEAVE`).
   * Direct consultation inquiry and booking modal.

5. **Statutory Legal Aid Portal (NALSA / TNSLSA)**
   * Interactive 3-step **Eligibility Calculator Wizard** evaluating Section 12 criteria (Women, Children, SC/ST, PwD, Custody, Low Income &le; ₹3,00,000).
   * Document upload for revenue income certificates and community proofs.
   * Dedicated **Legal Aid Officer Desk** to review applications and assign panel lawyers.

6. **Versioned Document Management**
   * Category-based document categorization (*Plaints, Written Statements, Affidavits, Vakalatnamas, Court Orders*).
   * MD5 checksum validation, file extension filtering, and automatic version numbering (`v1`, `v2`, `v3`).

7. **1-to-1 Client-Lawyer Chat**
   * Integrated direct messaging with unread notification badges and case context linkage.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.2.4, Spring Data JPA Repositories, Spring Security (JWT + RBAC), Jakarta Validation, Swagger OpenAPI, WebSocket STOMP |
| **Frontend** | React 18, Vite, Axios, React Router v6, Lucide React, Pure Vanilla CSS (Glassmorphism design system) |
| **Database & Persistence** | PostgreSQL / Embedded H2 / MySQL + Spring Data JPA Repositories + Programmatic `DataInitializer` seed engine |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |

---

## 👥 Demo User Credentials (Viva & Demonstration)

All seeded accounts have the default password: `password123`

| Role | Email | Name / Practice Area | Description |
| :--- | :--- | :--- | :--- |
| **Citizen (Client)** | `client@example.com` | Ramesh Babu | Track cases, consult lawyers, apply for legal aid. |
| **Senior Advocate** | `lawyer1@example.com` | Adv. Kumar S. (Bar: `MS/1024/2012`) | Active case management, hearing scheduling, diary logging. |
| **Civil Advocate** | `lawyer2@example.com` | Adv. Priya Lakshmi (Bar: `MS/2048/2016`) | Legal aid assignments, client chat, availability toggle. |
| **Legal Aid Officer** | `officer@example.com` | K. Sundaram (DLSA Officer) | Application review, certificate verification, lawyer assignment. |
| **System Admin** | `admin@example.com` | Administrator | System metrics, user management, court sync logs, audit trail. |

---

## 🚀 Getting Started Locally

### 1. Prerequisites
* **Java 17 JDK** installed and configured in `JAVA_HOME`.
* **Node.js (v18+)** and `npm`.
* **PostgreSQL** (or zero-config embedded persistence mode).

### 2. Database Configuration
Spring Data JPA automatically creates and maintains all 41 entity tables and relationships via Hibernate `ddl-auto=update`.

To run with **PostgreSQL**:
```properties
# in application.properties or .env
DB_URL=jdbc:postgresql://localhost:5432/legaltrack
DB_USERNAME=postgres
DB_PASSWORD=postgres
```
Or start with the dedicated profile:
```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

### 3. Running the Backend
```bash
cd backend
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```
* Backend API starts on `http://localhost:8080`
* Swagger UI documentation: `http://localhost:8080/swagger-ui.html`

### 4. Running the Frontend
```bash
cd frontend
npm install
npm run dev
```
* Frontend starts on `http://localhost:5173` (with `/api` proxying to `http://localhost:8080`).

### 5. Running Automated Tests
```bash
cd backend
mvnw.cmd test
```

---

## 📁 Repository Structure

```
d:/Varun/
├── backend/
│   ├── src/main/java/com/legaltrack/
│   │   ├── config/             # Security, CORS, WebSocket, Swagger configs
│   │   ├── controller/         # 13 REST Controllers
│   │   ├── dto/                # Request & Response Data Transfer Objects
│   │   ├── entity/             # 41 JPA Entities
│   │   ├── enums/              # 26 Domain Enums
│   │   ├── exception/          # Global Exception Handling
│   │   ├── integration/        # Court Data Provider abstraction
│   │   ├── mapper/             # Entity-DTO Mappers
│   │   ├── repository/         # 40 Spring Data Repositories
│   │   ├── scheduler/          # Hearing Reminder & Case Sync Schedulers
│   │   ├── security/           # JWT Token Provider & UserPrincipal
│   │   ├── service/            # 17 Business Service Interfaces & Impls
│   │   └── storage/            # Local File Storage Provider
│   └── src/test/java/          # Unit & Integration Tests
├── frontend/
│   ├── src/
│   │   ├── components/         # Navbar, Footer, Sidebar, Timeline, Modals, Badges
│   │   ├── context/            # AuthContext & NotificationContext
│   │   ├── pages/              # Public, Client, Lawyer, Officer, and Admin Pages
│   │   ├── services/           # Axios HTTP API services
│   │   └── styles/             # Glassmorphic Design System (index.css)
└── docs/                       # Architecture, Workflows, and API Specifications
```

---

## 📚 Academic Demonstration & Viva Points

1. **Why is the Case Diary structured chronologically?**
   * *Answer*: Legal proceedings in Indian courts require a strict temporal audit trail of pleadings, evidence, arguments, and interim orders. Event-sourcing principles guarantee accountability and prevent unauthorized retroactive edits.

2. **How does Case Attention rule evaluation work?**
   * *Answer*: Automated background routines continuously compare system dates with scheduled hearing dates, flagging upcoming hearings (7, 3, 1 days) and highlighting unattended matters.

3. **How does the system ensure data isolation between Client and Lawyer?**
   * *Answer*: Spring Security method-level annotations (`@PreAuthorize`) paired with ownership verification checks in service implementations validate that clients only access their authorized case documents and conversations.

4. **Why is Legal Aid eligibility calculated in 3 steps?**
   * *Answer*: Under Section 12 of the Legal Services Authorities Act, certain categories (women, children, SC/ST, custody) are categorically exempt from income caps, while general applicants must satisfy annual income thresholds (&le; ₹3,00,000). The pre-check wizard provides instantaneous transparency before formal submission.
