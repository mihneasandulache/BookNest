import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import ConfirmModal from '../components/ConfirmModal'

const emptyForm = {
  title: '',
  isbn: '',
  publishedYear: '',
  description: '',
  coverImageUrl: '',
  authorIds: [],
  genreIds: [],
}

export default function Books() {
  const { role } = useAuth()
  const isAdmin = role === 'ADMIN'

  const [books, setBooks] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')

  const [authors, setAuthors] = useState([])
  const [genres, setGenres] = useState([])

  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)

  const [deleteId, setDeleteId] = useState(null)
  const [error, setError] = useState('')

  const fetchBooks = async () => {
    try {
      const res = await api.get('/books', { params: { page, size: 10, search: search || undefined } })
      setBooks(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch {
      setError('Failed to load books')
    }
  }

  useEffect(() => { fetchBooks() }, [page, search])

  useEffect(() => {
    api.get('/authors').then(r => setAuthors(r.data)).catch(() => {})
    api.get('/genres').then(r => setGenres(r.data)).catch(() => {})
  }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    setPage(0)
    setSearch(searchInput)
  }

  const openAdd = () => {
    setForm(emptyForm)
    setEditingId(null)
    setShowForm(true)
  }

  const openEdit = (book) => {
    setForm({
      title: book.title,
      isbn: book.isbn || '',
      publishedYear: book.publishedYear || '',
      description: book.description || '',
      coverImageUrl: book.coverImageUrl || '',
      authorIds: book.authors.map(a => a.id),
      genreIds: book.genres.map(g => g.id),
    })
    setEditingId(book.id)
    setShowForm(true)
  }

  const handleFormChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const toggleMultiSelect = (field, id) => {
    const ids = form[field]
    setForm({ ...form, [field]: ids.includes(id) ? ids.filter(i => i !== id) : [...ids, id] })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const payload = { ...form, publishedYear: form.publishedYear ? Number(form.publishedYear) : null }
      if (editingId) {
        await api.put(`/books/${editingId}`, payload)
      } else {
        await api.post('/books', payload)
      }
      setShowForm(false)
      fetchBooks()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save book')
    }
  }

  const handleDelete = async () => {
    try {
      await api.delete(`/books/${deleteId}`)
      setDeleteId(null)
      fetchBooks()
    } catch {
      setError('Failed to delete book')
      setDeleteId(null)
    }
  }

  return (
    <div style={styles.container}>
      <h2>Books</h2>

      <div style={styles.toolbar}>
        <form onSubmit={handleSearch} style={styles.searchForm}>
          <input
            placeholder="Search by title..."
            value={searchInput}
            onChange={e => setSearchInput(e.target.value)}
            style={styles.searchInput}
          />
          <button type="submit" style={styles.btn}>Search</button>
          {search && (
            <button type="button" style={styles.btnSecondary} onClick={() => { setSearch(''); setSearchInput(''); setPage(0) }}>
              Clear
            </button>
          )}
        </form>
        {isAdmin && <button style={styles.btn} onClick={openAdd}>+ Add Book</button>}
      </div>

      {error && <p style={styles.error}>{error}</p>}

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Title</th>
            <th style={styles.th}>ISBN</th>
            <th style={styles.th}>Year</th>
            <th style={styles.th}>Authors</th>
            <th style={styles.th}>Genres</th>
            <th style={styles.th}>Rating</th>
            {isAdmin && <th style={styles.th}>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {books.map(book => (
            <tr key={book.id}>
              <td style={styles.td}>{book.title}</td>
              <td style={styles.td}>{book.isbn || '—'}</td>
              <td style={styles.td}>{book.publishedYear || '—'}</td>
              <td style={styles.td}>{book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ') || '—'}</td>
              <td style={styles.td}>{book.genres.map(g => g.name).join(', ') || '—'}</td>
              <td style={styles.td}>{book.averageRating ? book.averageRating.toFixed(1) : '—'}</td>
              {isAdmin && (
                <td style={styles.td}>
                  <button style={styles.btnSmall} onClick={() => openEdit(book)}>Edit</button>
                  <button style={{ ...styles.btnSmall, ...styles.btnDanger }} onClick={() => setDeleteId(book.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
          {books.length === 0 && (
            <tr><td colSpan={isAdmin ? 7 : 6} style={{ textAlign: 'center', padding: '1rem' }}>No books found.</td></tr>
          )}
        </tbody>
      </table>

      <div style={styles.pagination}>
        <button style={styles.btnSecondary} disabled={page === 0} onClick={() => setPage(p => p - 1)}>Previous</button>
        <span>Page {page + 1} of {totalPages || 1}</span>
        <button style={styles.btnSecondary} disabled={page + 1 >= totalPages} onClick={() => setPage(p => p + 1)}>Next</button>
      </div>

      {showForm && (
        <div style={styles.overlay}>
          <div style={styles.modal}>
            <h3>{editingId ? 'Edit Book' : 'Add Book'}</h3>
            <form onSubmit={handleSubmit} style={styles.formGrid}>
              <input name="title" placeholder="Title" value={form.title} onChange={handleFormChange} style={styles.input} required />
              <input name="isbn" placeholder="ISBN" value={form.isbn} onChange={handleFormChange} style={styles.input} />
              <input name="publishedYear" placeholder="Published Year" type="number" value={form.publishedYear} onChange={handleFormChange} style={styles.input} />
              <input name="coverImageUrl" placeholder="Cover Image URL" value={form.coverImageUrl} onChange={handleFormChange} style={styles.input} />
              <textarea name="description" placeholder="Description" value={form.description} onChange={handleFormChange} style={{ ...styles.input, gridColumn: '1 / -1', height: '80px' }} />

              <div style={{ gridColumn: '1 / -1' }}>
                <label style={styles.label}>Authors</label>
                <div style={styles.checkboxGroup}>
                  {authors.map(a => (
                    <label key={a.id} style={styles.checkboxLabel}>
                      <input type="checkbox" checked={form.authorIds.includes(a.id)} onChange={() => toggleMultiSelect('authorIds', a.id)} />
                      {a.firstName} {a.lastName}
                    </label>
                  ))}
                </div>
              </div>

              <div style={{ gridColumn: '1 / -1' }}>
                <label style={styles.label}>Genres</label>
                <div style={styles.checkboxGroup}>
                  {genres.map(g => (
                    <label key={g.id} style={styles.checkboxLabel}>
                      <input type="checkbox" checked={form.genreIds.includes(g.id)} onChange={() => toggleMultiSelect('genreIds', g.id)} />
                      {g.name}
                    </label>
                  ))}
                </div>
              </div>

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
          message="Are you sure you want to delete this book?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  )
}

const styles = {
  container: { padding: '2rem' },
  toolbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', gap: '1rem' },
  searchForm: { display: 'flex', gap: '0.5rem' },
  searchInput: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc', width: '220px' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { padding: '0.75rem', backgroundColor: '#1a1a2e', color: 'white', textAlign: 'left' },
  td: { padding: '0.75rem', borderBottom: '1px solid #eee' },
  pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '1rem' },
  btn: { padding: '0.5rem 1rem', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
  btnSecondary: { padding: '0.5rem 1rem', backgroundColor: 'white', color: '#1a1a2e', border: '1px solid #1a1a2e', borderRadius: '4px', cursor: 'pointer' },
  btnSmall: { padding: '0.3rem 0.6rem', marginRight: '0.4rem', border: 'none', borderRadius: '4px', cursor: 'pointer', backgroundColor: '#2980b9', color: 'white' },
  btnDanger: { backgroundColor: '#c0392b' },
  error: { color: 'red' },
  overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 },
  modal: { backgroundColor: 'white', borderRadius: '8px', padding: '2rem', width: '560px', maxHeight: '90vh', overflowY: 'auto' },
  formGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' },
  input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box' },
  label: { fontWeight: 'bold', display: 'block', marginBottom: '0.4rem' },
  checkboxGroup: { display: 'flex', flexWrap: 'wrap', gap: '0.5rem' },
  checkboxLabel: { display: 'flex', alignItems: 'center', gap: '0.3rem' },
}
