const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8082'

async function postJson(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
    body: JSON.stringify(body),
  })

  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    const msg = data?.message || `HTTP ${res.status}`
    throw new Error(msg)
  }
  return data
}

export const api = {
  identityVerify: (payload) => postJson('/api/v1/identity/verify', payload),
  otpRequest: (payload) => postJson('/api/v1/otp/request', payload),
  otpVerify: (payload) => postJson('/api/v1/otp/verify', payload),
}
