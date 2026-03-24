import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import ConfirmModal from '../components/ConfirmModal'

export default function BookDetail() {
  const { id } = useParams()
  const { isAuthenticated, role } = useAuth()
  const isAdmin = role === 'ROLE_ADMIN' || role === 'ADMIN'

  const [book, setBook] = useState(null)
  const [reviews, setReviews] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [page, setPage] = useState(0)

  const [form, setForm] = useState({ rating: 5, content: '' })
  const [editingReview, setEditingReview] = useState(null)
  const [deleteId, setDeleteId] = useState(null)
  const [error, setError] = useState('')
  const [submitError, setSubmitError] = useState('')

  const fetchBook = async () => {
    try {
      const res = await api.get(`/books/${id}`)
      setBook(res.data)
    } catch {
      setError('Failed to load book')
    }
  }

  const fetchReviews = async () => {
    try {
      const res = await api.get(`/reviews/book/${id}`, { params: { page, size: 5 } })
      setReviews(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch {
      setError('Failed to load reviews')
    }
  }

  useEffect(() => { fetchBook() }, [id])
  useEffect(() => { fetchReviews() }, [id, page])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitError('')
    try {
      if (editingReview) {
        await api.put(`/reviews/${editingReview.id}`, { bookId: Number(id), ...form })
        setEditingReview(null)
      } else {
        await api.post('/reviews', { bookId: Number(id), ...form })
      }
      setForm({ rating: 5, content: '' })
      fetchReviews()
      fetchBook()
    } catch (err) {
      setSubmitError(err.response?.data?.message || 'Failed to submit review')
    }
  }

  const handleEdit = (review) => {
    setEditingReview(review)
    setForm({ rating: review.rating, content: review.content || '' })
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })
  }

  const handleDelete = async () => {
    try {
      await api.delete(`/reviews/${deleteId}`)
      setDeleteId(null)
      fetchReviews()
      fetchBook()
    } catch {
      setError('Failed to delete review')
      setDeleteId(null)
    }
  }

  if (!book) return <p style={{ padding: '2rem' }}>Loading...</p>

  return (
    <div style={styles.container}>
      <div style={styles.bookCard}>
        <h2>{book.title}</h2>
        <p style={styles.meta}>
          {book.publishedYear && <span>Year: {book.publishedYear} &nbsp;|&nbsp;</span>}
          <span>Authors: {book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ') || '—'}</span>
          &nbsp;|&nbsp;
          <span>Genres: {book.genres.map(g => g.name).join(', ') || '—'}</span>
        </p>
        {book.averageRating && (
          <p style={styles.rating}>Average Rating: {'★'.repeat(Math.round(book.averageRating))}{'☆'.repeat(5 - Math.round(book.averageRating))} ({book.averageRating.toFixed(1)})</p>
        )}
        {book.description && <p style={styles.description}>{book.description}</p>}
      </div>

      <h3>Reviews ({book.reviewCount || 0})</h3>

      {error && <p style={styles.error}>{error}</p>}

      {reviews.length === 0 && <p>No reviews yet. Be the first!</p>}

      {reviews.map(review => (
        <div key={review.id} style={styles.reviewCard}>
          <div style={styles.reviewHeader}>
            <strong>{review.username}</strong>
            <span style={styles.stars}>{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span>
            <span style={styles.date}>{new Date(review.createdAt).toLocaleDateString()}</span>
          </div>
          {review.content && <p style={styles.reviewContent}>{review.content}</p>}
          {isAuthenticated && (
            <div style={styles.reviewActions}>
              <button style={styles.btnSmall} onClick={() => handleEdit(review)}>Edit</button>
              <button style={{ ...styles.btnSmall, ...styles.btnDanger }} onClick={() => setDeleteId(review.id)}>Delete</button>
            </div>
          )}
        </div>
      ))}

      {totalPages > 1 && (
        <div style={styles.pagination}>
          <button style={styles.btnSecondary} disabled={page === 0} onClick={() => setPage(p => p - 1)}>Previous</button>
          <span>Page {page + 1} of {totalPages}</span>
          <button style={styles.btnSecondary} disabled={page + 1 >= totalPages} onClick={() => setPage(p => p + 1)}>Next</button>
        </div>
      )}

      {isAuthenticated && (
        <div style={styles.formSection}>
          <h3>{editingReview ? 'Edit Review' : 'Leave a Review'}</h3>
          {submitError && <p style={styles.error}>{submitError}</p>}
          <form onSubmit={handleSubmit} style={styles.form}>
            <label style={styles.label}>Rating</label>
            <div style={styles.starPicker}>
              {[1, 2, 3, 4, 5].map(n => (
                <span
                  key={n}
                  style={{ ...styles.star, color: n <= form.rating ? '#f39c12' : '#ccc' }}
                  onClick={() => setForm({ ...form, rating: n })}
                >★</span>
              ))}
            </div>
            <textarea
              placeholder="Write your review... (optional)"
              value={form.content}
              onChange={e => setForm({ ...form, content: e.target.value })}
              style={styles.textarea}
            />
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button type="submit" style={styles.btn}>{editingReview ? 'Update' : 'Submit'}</button>
              {editingReview && (
                <button type="button" style={styles.btnSecondary} onClick={() => { setEditingReview(null); setForm({ rating: 5, content: '' }) }}>Cancel</button>
              )}
            </div>
          </form>
        </div>
      )}

      {deleteId && (
        <ConfirmModal
          message="Are you sure you want to delete this review?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  )
}

const styles = {
  container: { padding: '2rem', maxWidth: '800px', margin: '0 auto' },
  bookCard: { backgroundColor: '#f8f9fa', padding: '1.5rem', borderRadius: '8px', marginBottom: '2rem' },
  meta: { color: '#555', marginBottom: '0.5rem' },
  rating: { color: '#f39c12', fontWeight: 'bold' },
  description: { marginTop: '1rem', lineHeight: '1.6' },
  reviewCard: { border: '1px solid #eee', borderRadius: '8px', padding: '1rem', marginBottom: '1rem' },
  reviewHeader: { display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '0.5rem' },
  stars: { color: '#f39c12' },
  date: { color: '#999', fontSize: '0.85rem', marginLeft: 'auto' },
  reviewContent: { margin: '0.5rem 0', lineHeight: '1.5' },
  reviewActions: { display: 'flex', gap: '0.5rem', marginTop: '0.5rem' },
  pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', margin: '1rem 0' },
  formSection: { marginTop: '2rem', borderTop: '1px solid #eee', paddingTop: '1.5rem' },
  form: { display: 'flex', flexDirection: 'column', gap: '1rem' },
  label: { fontWeight: 'bold' },
  starPicker: { display: 'flex', gap: '0.3rem', cursor: 'pointer' },
  star: { fontSize: '2rem', cursor: 'pointer' },
  textarea: { padding: '0.6rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', height: '100px', resize: 'vertical' },
  btn: { padding: '0.5rem 1.2rem', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' },
  btnSecondary: { padding: '0.5rem 1rem', backgroundColor: 'white', color: '#1a1a2e', border: '1px solid #1a1a2e', borderRadius: '4px', cursor: 'pointer' },
  btnSmall: { padding: '0.3rem 0.6rem', border: 'none', borderRadius: '4px', cursor: 'pointer', backgroundColor: '#2980b9', color: 'white' },
  btnDanger: { backgroundColor: '#c0392b' },
  error: { color: 'red' },
}
