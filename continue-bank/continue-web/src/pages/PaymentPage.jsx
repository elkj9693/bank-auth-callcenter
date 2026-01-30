import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/premium.css";

const AUTH_ORIGIN = "http://localhost:3000";
const CARD_API_BASE = "http://localhost:8090";

export default function PaymentPage() {
  const nav = useNavigate();
  const popupRef = useRef(null);

  // States
  const [authStatus, setAuthStatus] = useState("NOT_STARTED"); // NOT_STARTED, IN_PROGRESS, VERIFIED, FAILED
  const [authResultToken, setAuthResultToken] = useState("");
  const [toast, setToast] = useState("");

  // User Input State (for pre-filling)
  const [form, setForm] = useState({
    name: "홍길동",
    birthDate: "900101",
    phoneNumber: "01012345678"
  });

  // Toast Timer
  useEffect(() => {
    if (!toast) return;
    const t = setTimeout(() => setToast(""), 3000);
    return () => clearTimeout(t);
  }, [toast]);

  // Window Message Listener
  useEffect(() => {
    function onMessage(e) {
      if (e.origin !== AUTH_ORIGIN) return;

      const data = e.data;
      if (!data || typeof data !== "object") return;

      if (data.type === "AUTH_SUCCESS" && data.authResultToken) {
        setAuthStatus("VERIFIED");
        setAuthResultToken(data.authResultToken);
        setToast("본인 인증이 완료되었습니다.");
        try { popupRef.current?.close(); } catch { }

      } else if (data.type === "AUTH_FAIL") {
        setAuthStatus("FAILED");
        setAuthResultToken("");
        setToast(`본인 인증 실패: ${data.reason || "알 수 없는 오류"}`);
        try { popupRef.current?.close(); } catch { }
      }
    }

    window.addEventListener("message", onMessage);
    return () => window.removeEventListener("message", onMessage);
  }, []);

  const onInputChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  // 1. Open Auth Popup with Query Params
  function openAuthPopup() {
    // Validate inputs briefly
    if (!form.name || !form.birthDate || !form.phoneNumber) {
      setToast("결제 정보를 모두 입력해주세요.");
      return;
    }

    setAuthStatus("IN_PROGRESS");
    setAuthResultToken("");

    const openerOrigin = window.location.origin;

    // Pass user info as query params
    const query = new URLSearchParams({
      openerOrigin: openerOrigin,
      name: form.name,
      birthDate: form.birthDate,
      phoneNumber: form.phoneNumber
    }).toString();

    const url = `${AUTH_ORIGIN}/auth-popup?${query}`;

    const w = 420;
    const h = 720;
    const left = window.screenX + (window.outerWidth - w) / 2;
    const top = window.screenY + (window.outerHeight - h) / 2;

    popupRef.current = window.open(
      url,
      "authPopup",
      `width=${w},height=${h},left=${left},top=${top}`
    );

    if (!popupRef.current) {
      setAuthStatus("FAILED");
      setToast("팝업 차단을 해제해주세요.");
    }
  }

  // 2. Confirm Payment
  async function confirmPayment() {
    if (!authResultToken) {
      setToast("본인 인증이 먼저 필요합니다.");
      return;
    }

    try {
      setToast("결제 승인 요청 중...");
      const res = await fetch(`${CARD_API_BASE}/api/v1/payment/confirm`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ authResultToken }),
      });

      const data = await res.json();
      if (data.success) {
        setToast("성공적으로 결제되었습니다!");
        nav("/success");
      } else {
        setToast(`결제 실패: ${data.resultCode || "오류 발생"}`);
      }
    } catch (e) {
      setToast("서버 통신 중 오류가 발생했습니다.");
    }
  }

  const isVerified = authStatus === "VERIFIED";
  const stepIndex = authStatus === "NOT_STARTED" ? 0
    : authStatus === "IN_PROGRESS" ? 1
      : 2;

  return (
    <div className="page-container">
      <div className="auth-card">
        {/* Header */}
        <div className="card-header">
          <div className="brand-badge">● Countinue Card</div>
          <h1 className="main-title">결제 요청</h1>
          <p className="sub-title">안전한 결제를 위해<br />본인 인증을 진행해주세요.</p>
        </div>

        {/* Visual Card */}
        <div className="virtual-card-area">
          <div className="virtual-card">
            <div className="vc-chip"></div>
            <div className="vc-num">4289 1010 **** 9876</div>
            <div className="vc-info">
              <span>{form.name ? form.name.toUpperCase() : "NAME"}</span>
              <span>12/28</span>
            </div>
          </div>
        </div>

        {/* Status Dots */}
        <div className="status-steps">
          <div className={`step-dot ${stepIndex >= 0 ? 'active' : ''}`}></div>
          <div className={`step-dot ${stepIndex >= 1 ? 'active' : ''}`}></div>
          <div className={`step-dot ${isVerified ? 'active' : ''}`}></div>
        </div>

        {/* Input Form Area (Read-only if verified) */}
        {!isVerified && (
          <div className="info-list" style={{ paddingBottom: 0 }}>
            <div className="info-input-group">
              <label>이름</label>
              <input
                type="text"
                name="name"
                value={form.name}
                onChange={onInputChange}
                placeholder="실명 입력"
              />
            </div>
            <div className="info-input-group">
              <label>생년월일</label>
              <input
                type="text"
                name="birthDate"
                value={form.birthDate}
                onChange={onInputChange}
                placeholder="YYMMDD"
                maxLength={6}
              />
            </div>
            <div className="info-input-group">
              <label>휴대폰 트</label>
              <input
                type="text"
                name="phoneNumber"
                value={form.phoneNumber}
                onChange={onInputChange}
                placeholder="01012345678"
                maxLength={11}
              />
            </div>
          </div>
        )}

        {/* Payment Info */}
        <div className="info-list">
          <div className="info-item total-row">
            <span className="info-label total-label">결제 금액</span>
            <span className="info-value total-value">12,300원</span>
          </div>
        </div>

        {/* Actions */}
        <div className="action-area">
          {!isVerified ? (
            <button
              className="btn btn-primary"
              onClick={openAuthPopup}
            >
              본인 인증하기
            </button>
          ) : (
            <button
              className="btn btn-primary"
              onClick={confirmPayment}
            >
              결제 승인
            </button>
          )}

          <div className="status-text">
            {authStatus === "IN_PROGRESS" && "인증 팝업에서 인증을 완료해주세요."}
            {authStatus === "FAILED" && <strong>인증이 취소되었거나 실패했습니다.</strong>}
          </div>
        </div>
      </div>

      {toast && <div className="toast-msg">{toast}</div>}
    </div>
  );
}
