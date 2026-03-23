import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await api.post('/auth/login', form)
      login(res.data.token, res.data.role)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials')
    }
  }

  return (
    <div style={styles.container}>
      <form onSubmit={handleSubmit} style={styles.form}>
        <h2>Login</h2>
        {error && <p style={styles.error}>{error}</p>}
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          style={styles.input}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          style={styles.input}
          required
        />
        <button type="submit" style={styles.button}>Login</button>
        <p style={{ textAlign: 'center' }}>
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </form>
    </div>
  )
}

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '80vh',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
    width: '320px',
    padding: '2rem',
    border: '1px solid #ddd',
    borderRadius: '8px',
  },
  input: {
    padding: '0.6rem',
    fontSize: '1rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
  },
  button: {
    padding: '0.7rem',
    backgroundColor: '#1a1a2e',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '1rem',
  },
  error: {
    color: 'red',
    margin: 0,
  },
}
