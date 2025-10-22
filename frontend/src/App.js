// src/App.js
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { AuthProvider } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';

// Import components from the new directory structure
import StartPage from './components/StartPage';
import LoginPage from './auth/LoginPage';
import SignupPage from './auth/SignupPage';
import Dashboard from './components/Dashboard';
import FinancesPage from './components/FinancesPage';
import InvestmentsPage from './components/InvestmentsPage';

import './index.css';

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<StartPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          
          {/* Protected Routes - Require Authentication */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          <Route path="/dashboard/finances" element={
            <ProtectedRoute>
              <FinancesPage />
            </ProtectedRoute>
          } />
          <Route path="/dashboard/investments" element={
            <ProtectedRoute>
              <InvestmentsPage />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}