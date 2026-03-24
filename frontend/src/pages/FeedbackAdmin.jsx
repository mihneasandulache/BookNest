import React, { useEffect, useState } from 'react'
import api from '../api/axios'
import ConfirmModal from '../components/ConfirmModal'

export default function FeedbackAdmin() {
  const [feedbacks, setFeedbacks] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [page, setPage] = useState(0)
  const [deleteId, setDeleteId] = useState(null)
  const [error, setError] = useState('')

  const fetchFeedbacks = async () => {
    try {
      const res = await api.get('/feedback', { params: { page, size: 10 } })
      setFeedbacks(res.data.content)
      setTotalPages(res.data.totalPages)
    } catch {
      setError('Failed to load feedback')
    }
  }

  useEffect(() => { fetchFeedbacks() }, [page])

  const handleDelete = async () => {
    try {
      await api.delete(`/feedback/${deleteId}`)
      setDeleteId(null)
      fetchFeedbacks()
    } catch {
      setError('Failed to delete feedback')
      setDeleteId(null)
    }
  }

  return (
    <div style={styles.container}>
      <h2>User Feedback</h2>

      {error && <p style={styles.error}>{error}</p>}

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>User</th>
            <th style={styles.th}>Category</th>
            <th style={styles.th}>Contact Method</th>
            <th style={styles.th}>Newsletter</th>
            <th style={styles.th}>Message</th>
            <th style={styles.th}>Submitted</th>
            <th style={styles.th}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {feedbacks.map(fb => (
            <tr key={fb.id}>
              <td style={styles.td}>{fb.username || '—'}</td>
              <td style={styles.td}>{fb.category}</td>
              <td style={styles.td}>{fb.contactMethod}</td>
              <td style={styles.td}>{fb.subscribeNewsletter ? 'Yes' : 'No'}</td>
              <td style={styles.td}>{fb.message.length > 60 ? fb.message.substring(0, 60) + '...' : fb.message}</td>
              <td style={styles.td}>{new Date(fb.submittedAt).toLocaleDateString()}</td>
              <td style={styles.td}>
                <button style={{ ...styles.btnSmall, ...styles.btnDanger }} onClick={() => setDeleteId(fb.id)}>Delete</button>
              </td>
            </tr>
          ))}
          {feedbacks.length === 0 && (
            <tr><td colSpan={7} style={{ textAlign: 'center', padding: '1rem' }}>No feedback submitted yet.</td></tr>
          )}
        </tbody>
      </table>

      <div style={styles.pagination}>
        <button style={styles.btnSecondary} disabled={page === 0} onClick={() => setPage(p => p - 1)}>Previous</button>
        <span>Page {page + 1} of {totalPages || 1}</span>
        <button style={styles.btnSecondary} disabled={page + 1 >= totalPages} onClick={() => setPage(p => p + 1)}>Next</button>
      </div>

      {deleteId && (
        <ConfirmModal
          message="Are you sure you want to delete this feedback?"
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
  pagination: { display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '1rem' },
  btnSecondary: { padding: '0.5rem 1rem', backgroundColor: 'white', color: '#1a1a2e', border: '1px solid #1a1a2e', borderRadius: '4px', cursor: 'pointer' },
  btnSmall: { padding: '0.3rem 0.6rem', border: 'none', borderRadius: '4px', cursor: 'pointer', color: 'white' },
  btnDanger: { backgroundColor: '#c0392b' },
  error: { color: 'red' },
}
