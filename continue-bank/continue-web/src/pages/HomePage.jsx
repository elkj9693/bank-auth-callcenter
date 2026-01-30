import { useNavigate } from "react-router-dom";
import "../styles/premium.css";

export default function HomePage() {
  const nav = useNavigate();

  return (
    <div className="page-container">
      <div className="auth-card" style={{ textAlign: "center", paddingBottom: 32 }}>
        <div className="card-header">
          <div className="brand-badge">● Countinue Card</div>
          <h1 className="main-title">안녕하세요!</h1>
          <p className="sub-title">Countinue Card의 프리미엄 결제 경험을<br />지금 바로 만나보세요.</p>
        </div>

        <div style={{ padding: "10px 24px 30px" }}>
          <div style={{ width: 120, height: 120, background: "#f2f4f6", borderRadius: "50%", margin: "0 auto 24px", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 48 }}>
            💳
          </div>
          {/* Marketing Banner */}
          <div
            onClick={() => nav("/counseling")}
            style={{
              background: "linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)",
              padding: "32px",
              borderRadius: "16px",
              color: "white",
              cursor: "pointer",
              marginBottom: "24px",
              boxShadow: "0 10px 15px -3px rgba(37, 99, 235, 0.2)"
            }}
          >
            <h2 style={{ margin: "0 0 8px 0" }}>맞춤 금융 상담 신청</h2>
            <p style={{ margin: 0, opacity: 0.9 }}>지금 신청하고 특별한 혜택을 확인하세요.</p>
          </div>
          <p style={{ color: "#444", lineHeight: 1.6, fontSize: 15 }}>
            <strong>결제 시작하기 버튼</strong>을 눌러서  <strong> 계속하세요.</strong><br />

          </p>
        </div>

        <div className="action-area" style={{ borderTop: "none" }}>
          <button
            className="btn btn-primary"
            onClick={() => nav("/payment")}
          >
            결제 시작하기
          </button>
        </div>
      </div>
    </div>
  );
}
