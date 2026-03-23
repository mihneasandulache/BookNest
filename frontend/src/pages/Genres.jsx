import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import ConfirmModal from '../components/ConfirmModal'

const emptyForm = { name: '', description: '' }

export default function Genres() {
  const { role } = useAuth()
  const isAdmin = role === 'ROLE_ADMIN'

  const [genres, setGenres] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [deleteId, setDeleteId] = useState(null)
  const [error, setError] = useState('')

  const fetchGenres = async () => {
    try {
      const res = await api.get('/genres')
      setGenres(res.data)
    } catch {
      setError('Failed to load genres')
    }
  }

  useEffect(() => { fetchGenres() }, [])

  const openAdd = () => {
    setForm(emptyForm)
    setEditingId(null)
    setShowForm(true)
  }

  const openEdit = (genre) => {
    setForm({ name: genre.name, description: genre.description || '' })
    setEditingId(genre.id)
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
        await api.put(`/genres/${editingId}`, form)
      } else {
        await api.post('/genres', form)
      }
      setShowForm(false)
      fetchGenres()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save genre')
    }
  }

  const handleDelete = async () => {
    try {
      await api.delete(`/genres/${deleteId}`)
      setDeleteId(null)
      fetchGenres()
    } catch {
      setError('Failed to delete genre')
      setDeleteId(null)
    }
  }

  return (
    <div style={styles.container}>
      <h2>Genres</h2>

      {isAdmin && (
        <div style={{ marginBottom: '1rem' }}>
          <button style={styles.btn} onClick={openAdd}>+ Add Genre</button>
        </div>
      )}

      {error && <p style={styles.error}>{error}</p>}

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Name</th>
            <th style={styles.th}>Description</th>
            {isAdmin && <th style={styles.th}>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {genres.map(genre => (
            <tr key={genre.id}>
              <td style={styles.td}>{genre.name}</td>
              <td style={styles.td}>{genre.description ? genre.description.substring(0, 100) + (genre.description.length > 100 ? '...' : '') : '—'}</td>
              {isAdmin && (
                <td style={styles.td}>
                  <button style={styles.btnSmall} onClick={() => openEdit(genre)}>Edit</button>
                  <button style={{ ...styles.btnSmall, ...styles.btnDanger }} onClick={() => setDeleteId(genre.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
          {genres.length === 0 && (
            <tr><td colSpan={isAdmin ? 3 : 2} style={{ textAlign: 'center', padding: '1rem' }}>No genres found.</td></tr>
          )}
        </tbody>
      </table>

      {showForm && (
        <div style={styles.overlay}>
          <div style={styles.modal}>
            <h3>{editingId ? 'Edit Genre' : 'Add Genre'}</h3>
            <form onSubmit={handleSubmit} style={styles.formGrid}>
              <input name="name" placeholder="Name" value={form.name} onChange={handleFormChange} style={{ ...styles.input, gridColumn: '1 / -1' }} required />
              <textarea name="description" placeholder="Description" value={form.description} onChange={handleFormChange} style={{ ...styles.input, gridColumn: '1 / -1', height: '80px' }} />

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
          message="Are you sure you want to delete this genre?"
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
  modal: { backgroundColor: 'white', borderRadius: '8px', padding: '2rem', width: '420px' },
  formGrid: { display: 'grid', gridTemplateColumns: '1fr', gap: '0.75rem' },
  input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box' },
}
