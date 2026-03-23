import React, { useState } from 'react'
import api from '../api/axios'

const initialForm = {
  category: '',
  contactMethod: 'email',
  subscribeNewsletter: false,
  message: '',
}

export default function Feedback() {
  const [form, setForm] = useState(initialForm)
  const [success, setSuccess] = useState(false)
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
      setSuccess(true)
      setForm(initialForm)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit feedback')
    }
  }

  return (
    <div style={styles.container}>
      <h2>Feedback</h2>
      <p>We'd love to hear from you!</p>

      {success && <p style={styles.success}>Thank you for your feedback!</p>}
      {error && <p style={styles.error}>{error}</p>}

      <form onSubmit={handleSubmit} style={styles.form}>
        <div style={styles.field}>
          <label style={styles.label}>Category</label>
          <select name="category" value={form.category} onChange={handleChange} style={styles.input} required>
            <option value="">-- Select a category --</option>
            <option value="Bug Report">Bug Report</option>
            <option value="Feature Request">Feature Request</option>
            <option value="General">General</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div style={styles.field}>
          <label style={styles.label}>Preferred Contact Method</label>
          <div style={styles.radioGroup}>
            {['email', 'phone', 'none'].map(method => (
              <label key={method} style={styles.radioLabel}>
                <input
                  type="radio"
                  name="contactMethod"
                  value={method}
                  checked={form.contactMethod === method}
                  onChange={handleChange}
                />
                {method.charAt(0).toUpperCase() + method.slice(1)}
              </label>
            ))}
          </div>
        </div>

        <div style={styles.field}>
          <label style={styles.checkboxLabel}>
            <input
              type="checkbox"
              name="subscribeNewsletter"
              checked={form.subscribeNewsletter}
              onChange={handleChange}
            />
            Subscribe to newsletter
          </label>
        </div>

        <div style={styles.field}>
          <label style={styles.label}>Message</label>
          <textarea
            name="message"
            value={form.message}
            onChange={handleChange}
            placeholder="Write your message here..."
            style={{ ...styles.input, height: '120px', resize: 'vertical' }}
            required
            minLength={10}
          />
        </div>

        <button type="submit" style={styles.btn}>Submit Feedback</button>
      </form>
    </div>
  )
}

const styles = {
  container: { padding: '2rem', maxWidth: '560px', margin: '0 auto' },
  form: { display: 'flex', flexDirection: 'column', gap: '1.2rem' },
  field: { display: 'flex', flexDirection: 'column', gap: '0.4rem' },
  label: { fontWeight: 'bold' },
  input: { padding: '0.6rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box' },
  radioGroup: { display: 'flex', gap: '1.5rem' },
  radioLabel: { display: 'flex', alignItems: 'center', gap: '0.4rem' },
  checkboxLabel: { display: 'flex', alignItems: 'center', gap: '0.5rem' },
  btn: { padding: '0.7rem 1.5rem', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '1rem', alignSelf: 'flex-start' },
  success: { color: 'green', fontWeight: 'bold' },
  error: { color: 'red' },
}
