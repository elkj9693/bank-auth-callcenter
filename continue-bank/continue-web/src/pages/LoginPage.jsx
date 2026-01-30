import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/premium.css";

const API_BASE = "http://localhost:8081";
const AUTH_ORIGIN = "http://localhost:3000"; // Auth Trustee Frontend

export default function LoginPage() {
  const nav = useNavigate();
  const popupRef = useRef(null);

  const [step, setStep] = useState(1); // 1: ID/PW, 2: Popup Auth
  const [form, setForm] = useState({
    username: "",
    password: "",
  });

  // Data from Step 1 (Partial Login)
  const [customerRef, setCustomerRef] = useState("");
  const [userInfo, setUserInfo] = useState({ name: "", birthDate: "", phoneNumber: "" });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const onInputChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  // Step 1: ID/PW Login
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await fetch(`${API_BASE}/api/v1/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: form.username, password: form.password }),
      });
      const data = await res.json();

      if (data.status === "PARTIAL_SESSION") {
        setCustomerRef(data.customerRef);
        setUserInfo({
          name: data.name,
          birthDate: data.birthDate,
          phoneNumber: data.phoneNumber
        });
        setStep(2);
      } else {
        setError("로그인에 실패했습니다. 아이디 또는 비밀번호를 확인하세요.");
      }
    } catch (err) {
      setError("서버 연결 실패");
    } finally {
      setLoading(false);
    }
  };

  // Listen for Popup Message
  useEffect(() => {
    const onMessage = async (e) => {
      if (e.origin !== AUTH_ORIGIN) return;
      const data = e.data;

      if (data.type === "AUTH_SUCCESS" && data.authResultToken) {
        try { popupRef.current?.close(); } catch { }
        await handleFinalVerify(data.authResultToken);
      } else if (data.type === "AUTH_FAIL") {
        setError(`인증 실패: ${data.reason}`);
        try { popupRef.current?.close(); } catch { }
      }
    };
    window.addEventListener("message", onMessage);
    return () => window.removeEventListener("message", onMessage);
  }, [customerRef]); // Need customerRef for final verify

  // Step 2: Open Popup
  const openAuthPopup = () => {
    setError("");
    const openerOrigin = window.location.origin;
    const query = new URLSearchParams({
      openerOrigin: openerOrigin,
      name: userInfo.name,
      birthDate: userInfo.birthDate,
      phoneNumber: userInfo.phoneNumber
    }).toString();

    const url = `${AUTH_ORIGIN}/auth-popup?${query}`;
    const w = 420;
    const h = 720;
    const left = window.screenX + (window.outerWidth - w) / 2;
    const top = window.screenY + (window.outerHeight - h) / 2;

    popupRef.current = window.open(url, "authPopup", `width=${w},height=${h},left=${left},top=${top}`);
  };

  // Step 3: Final Verification (Server-to-Server Token Check)
  const handleFinalVerify = async (token) => {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/api/v1/login/verify`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          authResultToken: token,
          customerRef
        }),
      });
      const data = await res.json();

      if (data.status === "FULL_LOGIN") {
        localStorage.setItem("user", JSON.stringify(data));
        nav("/");
      } else {
        setError(data.message || "인증 토큰 검증 실패");
      }
    } catch (err) {
      setError("최종 로그인 처리 중 오류 발생");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <div className="auth-card">
        <div className="card-header">
          <div className="brand-badge">● Countinue Bank</div>
          <h1 className="main-title">{step === 1 ? "로그인" : "본인 인증"}</h1>
          <p className="sub-title">
            {step === 1
              ? "아이디와 비밀번호를 입력해주세요."
              : "안전한 로그인을 위해 본인 인증을 진행합니다."}
          </p>
        </div>

        {step === 1 ? (
          <form className="info-list" onSubmit={handleLogin}>
            <div className="info-input-group">
              <label>사용자 이름</label>
              <input
                type="text"
                name="username"
                value={form.username}
                onChange={onInputChange}
                placeholder="이름 (예: hong)"
                required
              />
            </div>
            <div className="info-input-group">
              <label>비밀번호</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={onInputChange}
                placeholder="비밀번호"
                required
              />
            </div>
            <div className="action-area" style={{ marginTop: 24 }}>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? "확인 중..." : "다음"}
              </button>
            </div>
          </form>
        ) : (
          <div className="info-list">
            <div className="info-item">
              <span className="info-label">이름</span>
              <span className="info-value">{userInfo.name}</span>
            </div>
            <div className="info-item">
              <span className="info-label">휴대폰</span>
              <span className="info-value">{userInfo.phoneNumber}</span>
            </div>

            <div className="action-area" style={{ marginTop: 24 }}>
              <button type="button" className="btn btn-primary" onClick={openAuthPopup} disabled={loading}>
                {loading ? "로그인 처리 중..." : "본인 인증하기"}
              </button>
              <button
                type="button"
                className="btn"
                style={{ background: "#eee", color: "#333", marginTop: 8 }}
                onClick={() => setStep(1)}
              >
                뒤로 가기
              </button>
            </div>
          </div>
        )}

        {error && <p style={{ color: "red", textAlign: "center", marginTop: 16 }}>{error}</p>}
      </div>
    </div>
  );
}
