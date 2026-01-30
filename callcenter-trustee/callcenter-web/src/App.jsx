import React, { useState } from 'react';
import OutboundPage from './pages/OutboundPage';
import AuditView from './pages/AuditView';

export default function App() {
  const [activeTab, setActiveTab] = useState('OUTBOUND');

  return (
    <div className="app-shell">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="brand">
          <span>● Countinue CC</span>
        </div>

        <nav className="nav-menu">
          <div
            className={`nav-item ${activeTab === 'OUTBOUND' ? 'active' : ''}`}
            onClick={() => setActiveTab('OUTBOUND')}
          >
            📞 아웃바운드 (발신)
          </div>
          <div
            className={`nav-item ${activeTab === 'AUDIT' ? 'active' : ''}`}
            onClick={() => setActiveTab('AUDIT')}
          >
            📋 감사 로그
          </div>
        </nav>

        <div className="agent-status">
          <div className="status-row">
            <span style={{ fontSize: '14px', fontWeight: 600 }}>Agent_001</span>
            <div className="status-dot"></div>
          </div>
          <span style={{ fontSize: '12px', color: 'var(--text-dim)' }}>대기 중</span>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {activeTab === 'OUTBOUND' && <OutboundPage />}
        {activeTab === 'AUDIT' && <AuditView />}
      </main>
    </div>
  );
}
