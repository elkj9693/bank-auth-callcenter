import { useState, useEffect } from "react";
import "../App.css";

const API_BASE = "http://localhost:8080";

export default function OutboundPage() {
    const [targets, setTargets] = useState([]);
    const [selectedLead, setSelectedLead] = useState(null);
    const [loading, setLoading] = useState(false);
    const [callStatus, setCallStatus] = useState("IDLE"); // IDLE, CALLING, CONNECTED
    const [outcome, setOutcome] = useState("COMPLETED");

    useEffect(() => {
        fetchTargets();
    }, []);

    const fetchTargets = async () => {
        setLoading(true);
        try {
            const res = await fetch(`${API_BASE}/api/v1/outbound/targets`);
            const data = await res.json();
            setTargets(data);
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    const startCall = (lead) => {
        setSelectedLead(lead);
        setCallStatus("CALLING");
        // Simulate connection
        setTimeout(() => setCallStatus("CONNECTED"), 2000);
    };

    const submitResult = async () => {
        setLoading(true);
        try {
            await fetch(`${API_BASE}/api/v1/outbound/result`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    leadId: selectedLead.leadId,
                    operatorId: "AGENT_001",
                    outcome: outcome,
                    notes: "상담 완료"
                }),
            });
            alert("결과가 저장되었습니다.");
            setCallStatus("IDLE");
            setSelectedLead(null);
            fetchTargets();
        } catch (e) {
            alert("오류 발생");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="view-card">
            <h1>아웃바운드 상담 대상</h1>
            {loading && <p>로딩 중...</p>}

            {!selectedLead ? (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>이름</th>
                            <th>전화번호</th>
                            <th>상품</th>
                            <th>작업</th>
                        </tr>
                    </thead>
                    <tbody>
                        {targets.map(t => (
                            <tr key={t.leadId}>
                                <td style={{ fontSize: "12px", fontFamily: "monospace" }}>{t.leadId.substring(0, 8)}</td>
                                <td>{t.maskedName}</td>
                                <td>{t.maskedPhone}</td>
                                <td><span className="tag">[{t.requestedProductType}]</span></td>
                                <td>
                                    <button className="btn-primary" onClick={() => startCall(t)}>발신</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <div style={{ padding: "24px", border: "1px solid #eee", borderRadius: "12px" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 24 }}>
                        <h2>전화 통화 중: {selectedLead.maskedName} ({selectedLead.maskedPhone})</h2>
                        <div className={`tag ${callStatus}`}>{callStatus}</div>
                    </div>

                    {callStatus === "CONNECTED" && (
                        <div>
                            <div style={{ background: "#f8f9fa", padding: "16px", borderRadius: "8px", marginBottom: 24 }}>
                                <p><strong>[스크립트]</strong> 안녕하세요. Countinue Bank입니다... 녹취에 동의하십니까?</p>
                            </div>

                            <div className="info-input-group">
                                <label>상담 결과</label>
                                <select value={outcome} onChange={e => setOutcome(e.target.value)} className="input">
                                    <option value="COMPLETED">상담 완료</option>
                                    <option value="REJECTED">거부</option>
                                    <option value="NO_ANSWER">부재</option>
                                </select>
                            </div>

                            <button className="btn-primary" style={{ marginTop: "24px", width: "100%" }} onClick={submitResult}>상담 종료 및 저장</button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
