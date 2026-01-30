import { Routes, Route, Navigate } from 'react-router-dom'
import AuthPopupPage from './pages/AuthPopupPage.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/auth-popup" element={<AuthPopupPage />} />
      <Route path="*" element={<Navigate to="/auth-popup" replace />} />
    </Routes>
  )
}
