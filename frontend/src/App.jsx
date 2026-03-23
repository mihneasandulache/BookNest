import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import NavBar from './components/NavBar'
import Login from './pages/Login'
import Register from './pages/Register'
import Books from './pages/Books'
import Authors from './pages/Authors'
import Genres from './pages/Genres'

function Home() {
  return (
    <div style={{ textAlign: 'center', marginTop: '4rem', fontFamily: 'sans-serif' }}>
      <h1>Welcome to BookNest</h1>
      <p>Browse and review your favourite books.</p>
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <NavBar />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
          <Route path="/books" element={<ProtectedRoute><Books /></ProtectedRoute>} />
          <Route path="/authors" element={<ProtectedRoute><Authors /></ProtectedRoute>} />
          <Route path="/genres" element={<ProtectedRoute><Genres /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
