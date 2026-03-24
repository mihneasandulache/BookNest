import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function NavBar() {
  const { isAuthenticated, logout, role } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.brand}>BookNest</Link>
      <div style={styles.links}>
        {isAuthenticated ? (
          <>
            <Link to="/books" style={styles.link}>Books</Link>
            <Link to="/authors" style={styles.link}>Authors</Link>
            <Link to="/genres" style={styles.link}>Genres</Link>
            {role === 'ADMIN'
              ? <Link to="/admin/feedback" style={styles.link}>Feedback</Link>
              : <Link to="/feedback" style={styles.link}>Feedback</Link>
            }
            <button onClick={handleLogout} style={styles.button}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" style={styles.link}>Login</Link>
            <Link to="/register" style={styles.link}>Register</Link>
          </>
        )}
      </div>
    </nav>
  )
}

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '1rem 2rem',
    backgroundColor: '#1a1a2e',
    color: 'white',
  },
  brand: {
    color: 'white',
    textDecoration: 'none',
    fontSize: '1.4rem',
    fontWeight: 'bold',
  },
  links: {
    display: 'flex',
    gap: '1.5rem',
    alignItems: 'center',
  },
  link: {
    color: 'white',
    textDecoration: 'none',
  },
  button: {
    background: 'none',
    border: '1px solid white',
    color: 'white',
    padding: '0.3rem 0.8rem',
    cursor: 'pointer',
    borderRadius: '4px',
  },
}
