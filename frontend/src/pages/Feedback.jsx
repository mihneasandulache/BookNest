import React, { useState } from 'react'
import api from '../api/axios'

const initialForm = {
  category: '',
  contactMethod: '',
  subscribeNewsletter: false,
  message: '',
}

export default function Feedback() {
  const [form, setForm] = useState(initialForm)
  const [submitted, setSubmitted] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await api.post('/feedback', form)
      setSubmitted(true)
      setForm(initialForm)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit feedback')
    }
  }

  if (submitted) {
    return (
      <div style={styles.container}>
        <div style={styles.card}>
          <h2>Thank you!</h2>
          <p>Your feedback has been submitted.</p>
          <button style={styles.btn} onClick={() => setSubmitted(false)}>Submit another</button>
        </div>
      </div>
    )
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2>Feedback</h2>
        <p style={{ color: '#555', marginBottom: '1.5rem' }}>We'd love to hear from you!</p>

        <form onSubmit={handleSubmit} style={styles.form}>

          {/* SELECT */}
          <div style={styles.field}>
            <label style={styles.label}>Category</label>
            <select name="category" value={form.category} onChange={handleChange} style={styles.input} required>
              <option value="">-- Select a category --</option>
              <option value="BUG">Bug Report</option>
              <option value="FEATURE">Feature Request</option>
              <option value="CONTENT">Content Issue</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          {/* RADIO */}
          <div style={styles.field}>
            <label style={styles.label}>Preferred contact method</label>
            <div style={styles.radioGroup}>
              {['EMAIL', 'PHONE', 'NONE'].map(option => (
                <label key={option} style={styles.radioLabel}>
                  <input
                    type="radio"
                    name="contactMethod"
                    value={option}
                    checked={form.contactMethod === option}
                    onChange={handleChange}
                    required
                  />
                  {option.charAt(0) + option.slice(1).toLowerCase()}
                </label>
              ))}
            </div>
          </div>

          {/* CHECKBOX */}
          <div style={styles.field}>
            <label style={styles.checkboxLabel}>
              <input
                type="checkbox"
                name="subscribeNewsletter"
                checked={form.subscribeNewsletter}
                onChange={handleChange}
              />
              Subscribe to newsletter for updates
            </label>
          </div>

          {/* TEXTAREA */}
          <div style={styles.field}>
            <label style={styles.label}>Message</label>
            <textarea
              name="message"
              placeholder="Write your feedback here..."
              value={form.message}
              onChange={handleChange}
              style={{ ...styles.input, height: '120px', resize: 'vertical' }}
              required
            />
          </div>

          {error && <p style={styles.error}>{error}</p>}

          <button type="submit" style={styles.btn}>Submit Feedback</button>
        </form>
      </div>
    </div>
  )
}

const styles = {
  container: { display: 'flex', justifyContent: 'center', padding: '2rem' },
  card: { width: '520px', padding: '2rem', border: '1px solid #ddd', borderRadius: '8px' },
  form: { display: 'flex', flexDirection: 'column', gap: '1.25rem' },
  field: { display: 'flex', flexDirection: 'column', gap: '0.4rem' },
  label: { fontWeight: 'bold', fontSize: '0.95rem' },
  input: { padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box' },
  radioGroup: { display: 'flex', gap: '1.5rem' },
  radioLabel: { display: 'flex', alignItems: 'center', gap: '0.4rem', cursor: 'pointer' },
  checkboxLabel: { display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' },
  btn: { padding: '0.7rem', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem' },
  error: { color: 'red', margin: 0 },
}
