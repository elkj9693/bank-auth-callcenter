import { Routes, Route, Link, useNavigate, Navigate } from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import PaymentPage from "./pages/PaymentPage.jsx";
import SuccessPage from "./pages/SuccessPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import CounselingPage from "./pages/CounselingPage.jsx";

function ProtectedRoute({ children }) {
  const user = localStorage.getItem("user");
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  return (
    <div style={{ fontFamily: "system-ui, -apple-system, Segoe UI, Roboto" }}>
      <div style={{ maxWidth: 960, margin: "0 auto", padding: 16 }}>
        <header
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            marginBottom: 16,
          }}
        >
          <Link to="/" style={{ textDecoration: "none", color: "#111" }}>
            <h2 style={{ margin: 0 }}>Countinue Card</h2>
          </Link>
          <nav style={{ display: "flex", gap: 12 }}>
            <Link to="/" style={{ color: "#111" }}>
              홈
            </Link>
            {/* Debug/UX: Logout to verify Auth Protection */}
            <button
              onClick={() => {
                localStorage.removeItem("user");
                window.location.href = "/login"; // Force refresh to clear state
              }}
              style={{
                background: "none",
                border: "none",
                cursor: "pointer",
                fontSize: "16px",
                textDecoration: "underline"
              }}
            >
              로그아웃 (Test)
            </button>
          </nav>
        </header>

        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <HomePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/counseling"
            element={
              <ProtectedRoute>
                <CounselingPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/payment"
            element={
              <ProtectedRoute>
                <PaymentPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/success"
            element={
              <ProtectedRoute>
                <SuccessPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </div>
  );
}
