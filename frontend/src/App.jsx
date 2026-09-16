import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { NotificationProvider } from './context/NotificationContext';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { ProtectedRoute } from './components/ProtectedRoute';

// Public Pages
import { Home } from './pages/Home';
import { TrackCasePage } from './pages/TrackCasePage';
import { FindLawyer } from './pages/FindLawyer';
import { LawyerProfilePage } from './pages/LawyerProfilePage';
import { LegalAidPage } from './pages/LegalAidPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { AboutPage } from './pages/AboutPage';

// Authenticated Pages
import { ClientDashboard } from './pages/ClientDashboard';
import { LawyerDashboard } from './pages/LawyerDashboard';
import { OfficerDashboard } from './pages/OfficerDashboard';
import { AdminDashboard } from './pages/AdminDashboard';
import { CaseWorkspacePage } from './pages/CaseWorkspacePage';
import { ChatPage } from './pages/ChatPage';
import { SupportPage } from './pages/SupportPage';
import { ProfilePage } from './pages/ProfilePage';
import { MyCasesPage } from './pages/MyCasesPage';
import { TrackedCasesPage } from './pages/TrackedCasesPage';
import { LawyerRequestsPage } from './pages/LawyerRequestsPage';
import { LegalAidApplicationsPage } from './pages/LegalAidApplicationsPage';

export const App = () => {
  return (
    <AuthProvider>
      <NotificationProvider>
        <div className="app-container">
          <Navbar />
          <main style={{ flex: 1 }}>
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<Home />} />
              <Route path="/track" element={<TrackCasePage />} />
              <Route path="/lawyers" element={<FindLawyer />} />
              <Route path="/lawyers/:id" element={<LawyerProfilePage />} />
              <Route path="/legal-aid" element={<LegalAidPage />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Shared Authenticated Routes */}
              <Route element={<ProtectedRoute />}>
                <Route path="/cases/:id" element={<CaseWorkspacePage />} />
                <Route path="/chat" element={<ChatPage />} />
                <Route path="/support" element={<SupportPage />} />
                <Route path="/profile" element={<ProfilePage />} />
              </Route>

              {/* Client Routes */}
              <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                <Route path="/client/dashboard" element={<ClientDashboard />} />
                <Route path="/client/cases" element={<MyCasesPage />} />
                <Route path="/client/tracked" element={<TrackedCasesPage />} />
                <Route path="/client/requests" element={<LawyerRequestsPage />} />
                <Route path="/client/legal-aid" element={<LegalAidApplicationsPage />} />
              </Route>

              {/* Lawyer Routes */}
              <Route element={<ProtectedRoute allowedRoles={['LAWYER']} />}>
                <Route path="/lawyer/dashboard" element={<LawyerDashboard />} />
                <Route path="/lawyer/cases" element={<MyCasesPage />} />
                <Route path="/lawyer/requests" element={<LawyerRequestsPage />} />
                <Route path="/lawyer/calendar" element={<MyCasesPage />} />
              </Route>

              {/* Officer Routes */}
              <Route element={<ProtectedRoute allowedRoles={['LEGAL_AID_OFFICER']} />}>
                <Route path="/officer/dashboard" element={<OfficerDashboard />} />
                <Route path="/officer/applications" element={<OfficerDashboard />} />
                <Route path="/officer/assignments" element={<OfficerDashboard />} />
              </Route>

              {/* Admin Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
                <Route path="/admin/users" element={<AdminDashboard />} />
                <Route path="/admin/verification" element={<AdminDashboard />} />
                <Route path="/admin/sync-logs" element={<AdminDashboard />} />
                <Route path="/admin/audit-logs" element={<AdminDashboard />} />
              </Route>

              {/* 404 Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </NotificationProvider>
    </AuthProvider>
  );
};
