import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    if (savedUser && token) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse saved user', e);
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const res = await authService.login(credentials);
    if (res?.success && res.data) {
      setUser(res.data);
      return res.data;
    }
    throw new Error(res?.message || 'Login failed');
  };

  const registerClient = async (data) => {
    const res = await authService.registerClient(data);
    if (res?.success && res.data) {
      setUser(res.data);
      return res.data;
    }
    throw new Error(res?.message || 'Client registration failed');
  };

  const registerLawyer = async (data) => {
    const res = await authService.registerLawyer(data);
    if (res?.success && res.data) {
      setUser(res.data);
      return res.data;
    }
    throw new Error(res?.message || 'Lawyer registration failed');
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  const hasRole = (role) => {
    if (!user || !user.roles) return false;
    return user.roles.includes(role) || user.roles.includes(`ROLE_${role}`);
  };

  const isClient = () => hasRole('CLIENT') || hasRole('ROLE_CLIENT');
  const isLawyer = () => hasRole('LAWYER') || hasRole('ROLE_LAWYER');
  const isOfficer = () => hasRole('LEGAL_AID_OFFICER') || hasRole('ROLE_LEGAL_AID_OFFICER');
  const isAdmin = () => hasRole('ADMIN') || hasRole('ROLE_ADMIN');
  const isSupport = () => hasRole('SUPPORT') || hasRole('ROLE_SUPPORT');

  const value = {
    user,
    loading,
    isAuthenticated: !!user,
    login,
    registerClient,
    registerLawyer,
    logout,
    hasRole,
    isClient,
    isLawyer,
    isOfficer,
    isAdmin,
    isSupport,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
