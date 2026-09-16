# LEGALTRACK — REST API Specification

All API endpoints are prefixed with `/api`. Protected endpoints require an `Authorization: Bearer <JWT_TOKEN>` header.

---

## 1. Authentication Endpoints (`/api/auth`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user and receive JWT access + refresh tokens | Public |
| `POST` | `/api/auth/register/client` | Register new citizen client account | Public |
| `POST` | `/api/auth/register/lawyer` | Register new advocate account with bar enrollment | Public |
| `POST` | `/api/auth/refresh` | Rotate expired access token using refresh token | Public |
| `POST` | `/api/auth/logout` | Invalidate active refresh token | Authenticated |
| `GET` | `/api/auth/me` | Fetch profile details of current user | Authenticated |
| `POST` | `/api/auth/change-password` | Change user account password | Authenticated |

---

## 2. Case Tracking & Workspace (`/api/cases`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/cases/track/cnr` | Search case by 16-digit standard CNR number | Public |
| `POST` | `/api/cases/track/case-number`| Search case by Case Number, Court, and Filing Year | Public |
| `POST` | `/api/cases/track/fir` | Search case by Police Station & FIR Number | Public |
| `GET` | `/api/cases/{caseId}/details` | Get comprehensive case details, hearings, and diary | Client/Lawyer/Admin |
| `GET` | `/api/cases/{caseId}/timeline` | Get procedural stage progress and event chronology | Client/Lawyer/Admin |
| `POST` | `/api/cases/search` | Search case files with multi-parameter filter criteria | Authenticated |
| `GET` | `/api/cases/my` | Get paginated case files for the authenticated user | Authenticated |
| `POST` | `/api/cases` | Create a new case file workspace | Lawyer/Admin |
| `PUT` | `/api/cases/{caseId}` | Update case status, procedural stage, or details | Lawyer/Admin |
| `POST` | `/api/cases/{caseId}/diary` | Record a chronological case diary entry | Lawyer/Client/Admin |
| `POST` | `/api/cases/{caseId}/hearings` | Schedule next court hearing | Lawyer/Admin |
| `POST` | `/api/cases/{caseId}/orders` | Record court order or final decree | Lawyer/Admin |
| `POST` | `/api/cases/{caseId}/notes` | Add private research or strategy note | Lawyer/Client/Admin |

---

## 3. Tracked Cases (`/api/tracked-cases`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/tracked-cases` | Get paginated list of tracked cases for user | Authenticated |
| `POST` | `/api/tracked-cases` | Start tracking a case with custom nickname | Authenticated |
| `PUT` | `/api/tracked-cases/{id}` | Update tracked case nickname or notifications | Authenticated |
| `DELETE`| `/api/tracked-cases/{id}` | Remove case from tracking list | Authenticated |
| `PATCH`| `/api/tracked-cases/{id}/notifications` | Toggle hearing reminder notifications | Authenticated |

---

## 4. Advocate Directory (`/api/lawyers`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/lawyers/search` | Filter advocates by district, court, specialization, exp | Public |
| `GET` | `/api/lawyers/{id}` | Get advocate profile, verified badge, and courts | Public |
| `PUT` | `/api/lawyers/availability` | Update lawyer consultation availability status | Lawyer |

---

## 5. Legal Aid Assistance (`/api/legal-aid`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/legal-aid/pre-check` | Instant statutory Section 12 eligibility pre-check | Public |
| `POST` | `/api/legal-aid/apply` | Formal legal aid application submission | Authenticated |
| `GET` | `/api/legal-aid/my-applications` | Get applications submitted by current client | Client |
| `GET` | `/api/legal-aid/applications` | Review applications queue across districts | Officer/Admin |
| `POST` | `/api/legal-aid/applications/{id}/review` | Record statutory eligibility decision (Approve/Reject) | Officer/Admin |
| `POST` | `/api/legal-aid/applications/{id}/assign-lawyer` | Assign panel advocate and create case file | Officer/Admin |

---

## 6. Case Documents (`/api/documents`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/documents/cases/{caseId}/upload` | Upload versioned case document (`multipart/form-data`) | Client/Lawyer/Admin |
| `GET` | `/api/documents/{documentId}/download` | Download document with authorization checks | Authorized Users |
| `GET` | `/api/documents/{documentId}/versions` | View version lineage history | Authorized Users |
| `DELETE`| `/api/documents/{documentId}` | Delete document from workspace | Lawyer/Admin |

---

## 7. Real-Time Chat & Messages (`/api/chat`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/chat/conversations` | Get active 1-to-1 conversation threads | Client/Lawyer |
| `POST` | `/api/chat/conversations` | Initialize conversation with advocate | Client/Lawyer |
| `GET` | `/api/chat/conversations/{id}/messages` | Get chronological message thread | Client/Lawyer |
| `POST` | `/api/chat/conversations/{id}/messages` | Send message in conversation | Client/Lawyer |
| `PUT` | `/api/chat/conversations/{id}/read` | Mark all unread messages as read | Client/Lawyer |

---

## 8. Notifications & Alerts (`/api/notifications`)

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notifications` | Get paginated alerts for authenticated user | Authenticated |
| `GET` | `/api/notifications/unread-count` | Get unread notification counter badge | Authenticated |
| `PUT` | `/api/notifications/{id}/read` | Mark individual notification as read | Authenticated |
| `PUT` | `/api/notifications/read-all` | Mark all notifications as read | Authenticated |
