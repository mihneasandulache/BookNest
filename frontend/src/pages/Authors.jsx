import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import ConfirmModal from '../components/ConfirmModal'

const emptyForm = { firstName: '', lastName: '', bio: '', nationality: '' }

export default function Authors() {
  const { role } = useAuth()
  const isAdmin = role === 'ADMIN'

  const [authors, setAuthors] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [deleteId, setDeleteId] = useState(null)
  const [error, setError] = useState('')

  const fetchAuthors = async () => {
    try {
      const res = await api.get('/authors')
      setAuthors(res.data)
    } catch {
      setError('Failed to load authors')
    }
  }

  useEffect(() => { fetchAuthors() }, [])

  const openAdd = () => {
    setForm(emptyForm)
    setEditingId(null)
    setShowForm(true)
  }

  const openEdit = (author) => {
    setForm({
      firstName: author.firstName,
      lastName: author.lastName,
      bio: author.bio || '',
      nationality: author.nationality || '',
    })
    setEditingId(author.id)
    setShowForm(true)
  }

  const handleFormChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      if (editingId) {
        await api.put(`/authors/${editingId}`, form)
      } else {
        await api.post('/authors', form)
      }
      setShowForm(false)
      fetchAuthors()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save author')
    }
  }

  const handleDelete = async () => {
    try {
      await api.delete(`/authors/${deleteId}`)
      setDeleteId(null)
      fetchAuthors()
    } catch {
      setError('Failed to delete author')
      setDeleteId(null)
    }
  }

  return (
    <div style={styles.container}>
      <h2>Authors</h2>

      {isAdmin && (
        <div style={{ marginBottom: '1rem' }}>
          <button style={styles.btn} onClick={openAdd}>+ Add Author</button>
        </div>
      )}

      {error && <p style={styles.error}>{error}</p>}

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>First Name</th>
            <th style={styles.th}>Last Name</th>
            <th style={styles.th}>Nationality</th>
            <th style={styles.th}>Bio</th>
            {isAdmin && <th style={styles.th}>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {authors.map(author => (
            <tr key={author.id}>
              <td style={styles.td}>{author.firstName}</td>
              <td style={styles.td}>{author.lastName}</td>
              <td style={styles.td}>{author.nationality || '—'}</td>
              <td style={styles.td}>{author.bio ? author.bio.substring(0, 80) + (author.bio.length > 80 ? '...' : '') : '—'}</td>
              {isAdmin && (
                <td style={styles.td}>
                  <button style={styles.btnSmall} onClick={() => openEdit(author)}>Edit</button>
                  <button style={{ ...styles.btnSmall, ...styles.btnDanger }} onClick={() => setDeleteId(author.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
          {authors.length === 0 && (
            <tr><td colSpan={isAdmin ? 5 : 4} style={{ textAlign: 'center', padding: '1rem' }}>No authors found.</td></tr>
          )}
        </tbody>
      </table>

      {showForm && (
        <div style={styles.overlay}>
          <div style={styles.modal}>
            <h3>{editingId ? 'Edit Author' : 'Add Author'}</h3>
            <form onSubmit={handleSubmit} style={styles.formGrid}>
              <input name="firstName" placeholder="First Name" value={form.firstName} onChange={handleFormChange} style={styles.input} required />
              <input name="lastName" placeholder="Last Name" value={form.lastName} onChange={handleFormChange} style={styles.input} required />
              <input name="nationality" placeholder="Nationality" value={form.nationality} onChange={handleFormChange} style={styles.input} />
              <textarea name="bio" placeholder="Bio" value={form.bio} onChange={handleFormChange} style={{ ...styles.input, gridColumn: '1 / -1', height: '80px' }} />

              {error && <p style={{ ...styles.error, gridColumn: '1 / -1' }}>{error}</p>}

              <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" style={styles.btnSecondary} onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" style={styles.btn}>Save</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {deleteId && (
        <ConfirmModal
          message="Are you sure you want to delete this author?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  )
}

const styles = {
  container: { padding: '2rem' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { padding: '0.75rem', backgroundColor: '#1a1a2e', color: 'white', textAlign: 'left' },
  td: { padding: '0.75rem', borderBottom: '1px solid #eee' },
  btn: { padding: '0.5rem 1rem', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
  btnSecondary: { padding: '0.5rem 1rem', backgroundColor: 'white', color: '#1a1a2e', border: '1px solid #1a1a2e', borderRadius: '4px', cursor: 'pointer' },
  btnSmall: { padding: '0.3rem 0.6rem', marginRight: '0.4rem', border: 'none', borderRadius: '4px', cursor: 'pointer', backgroundColor: '#2980b9', color: 'white' },
  btnDanger: { backgroundColor: '#c0392b' },
  error: { color: 'red' },
  overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 },
  modal: { backgroundColor: 'white', borderRadius: '8px', padding: '2rem', width: '480px' },
  formGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' },
  input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box' },
}
