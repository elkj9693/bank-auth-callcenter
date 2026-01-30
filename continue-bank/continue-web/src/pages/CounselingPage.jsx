import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/premium.css";

const API_BASE = "http://localhost:8081";

export default function CounselingPage() {
    const nav = useNavigate();
    const [user, setUser] = useState(null);
    const [form, setForm] = useState({
        productType: "대출",
        consentMarketing: "N",
        consentOutboundCall: "N",
        consentSuksacksa: "N"
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const savedUser = localStorage.getItem("user");
        if (!savedUser) {
            alert("로그인이 필요합니다.");
            nav("/login");
        } else {
            setUser(JSON.parse(savedUser));
        }
    }, [nav]);

    const onHandleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === "checkbox" ? (checked ? "Y" : "N") : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (form.consentSuksacksa !== "Y") {
            alert("개인정보 처리위하에 대한 안내 확인이 필요합니다.");
            return;
        }
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/api/v1/leads/apply`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userRef: user.customerRef,
                    productType: form.productType,
                    consentMarketing: form.consentMarketing,
                    consentOutboundCall: form.consentOutboundCall
                }),
            });
            if (res.ok) {
                alert("상담 신청이 완료되었습니다.");
                nav("/");
            }
        } catch (err) {
            alert("오류 발생");
        } finally {
            setLoading(false);
        }
    };

    if (!user) return null;

    return (
        <div className="page-container">
            <div className="auth-card">
                <div className="card-header">
                    <h1 className="main-title">상담 신청</h1>
                    <p className="sub-title">전문 상담원이 곧 연락드릴 예정입니다.</p>
                </div>

                <form className="info-list" onSubmit={handleSubmit}>
                    <div className="info-input-group">
                        <label>상담 희망 상품</label>
                        <select name="productType" value={form.productType} onChange={onHandleChange} className="input">
                            <option value="신용카드">신용카드</option>
                            <option value="대출">대출</option>
                            <option value="보험">보험</option>
                        </select>
                    </div>

                    <div style={{ marginTop: 24, fontSize: "14px", color: "#666" }}>
                        <div style={{ marginBottom: 12, display: "flex", alignItems: "center", gap: 8 }}>
                            <input
                                type="checkbox"
                                name="consentSuksacksa"
                                checked={form.consentSuksacksa === "Y"}
                                onChange={onHandleChange}
                            />
                            <span>(필수) 개인정보 처리위탁에 대한 안내 확인(콜센터 위탁)</span>
                        </div>
                        <div style={{ marginBottom: 12, display: "flex", alignItems: "center", gap: 8 }}>
                            <input
                                type="checkbox"
                                name="consentMarketing"
                                checked={form.consentMarketing === "Y"}
                                onChange={onHandleChange}
                            />
                            <span>(선택) 마케팅 활용 동의</span>
                        </div>
                        <div style={{ marginBottom: 12, display: "flex", alignItems: "center", gap: 8 }}>
                            <input
                                type="checkbox"
                                name="consentOutboundCall"
                                checked={form.consentOutboundCall === "Y"}
                                onChange={onHandleChange}
                            />
                            <span>(선택) 전화 상담 연락 동의(아웃바운드 연락)</span>
                        </div>
                    </div>

                    <div className="action-area" style={{ marginTop: 32 }}>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? "전송 중..." : "상담 신청하기"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
