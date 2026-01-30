import { Link } from "react-router-dom";
import "../styles/premium.css";

export default function SuccessPage() {
  return (
    <div className="page-container">
      <div className="auth-card" style={{ textAlign: "center" }}>
        <div className="card-header">
          <div className="brand-badge" style={{ background: "rgba(16,185,129,0.1)", color: "#10b981" }}>Success</div>
          <h1 className="main-title">결제 완료!</h1>
          <p className="sub-title">12,300원이 정상적으로 결제되었습니다.</p>
        </div>

        <div style={{ padding: "0 24px 30px" }}>
          <div style={{ width: 80, height: 80, background: "#10b981", borderRadius: "50%", margin: "0 auto 20px", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 32, color: "white", boxShadow: "0 10px 20px rgba(16,185,129,0.3)" }}>
            ✔
          </div>
          <div className="info-list">
            <div className="info-item">
              <span className="info-label">승인 상태</span>
              <span className="info-value" style={{ color: "#10b981" }}>APPROVED</span>
            </div>
            <div className="info-item total-row">
              <span className="info-label total-label">최종 결제 금액</span>
              <span className="info-value total-value">12,300원</span>
            </div>
          </div>
        </div>

        <div className="action-area">
          <Link to="/" style={{ textDecoration: 'none' }}>
            <button className="btn btn-primary" style={{ background: "#191f28" }}>
              홈으로 돌아가기
            </button>
          </Link>
        </div>
      </div>
    </div>
  );
}
