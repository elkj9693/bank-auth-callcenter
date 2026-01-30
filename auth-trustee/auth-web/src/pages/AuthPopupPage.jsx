import { useMemo, useState, useEffect } from 'react'
import { api } from '../api.js'
import '../styles/auth.css'

function getQueryParam(key) {
  const params = new URLSearchParams(window.location.search)
  return params.get(key) || ''
}

export default function AuthPopupPage() {
  const openerOrigin = useMemo(() => getQueryParam('openerOrigin'), [])

  // Pre-fill Logic
  const preFilledName = useMemo(() => getQueryParam('name'), [])
  const preFilledBirthDate = useMemo(() => getQueryParam('birthDate'), [])
  const preFilledPhoneNumber = useMemo(() => getQueryParam('phoneNumber'), [])
  const isPreFilled = !!(preFilledName && preFilledBirthDate && preFilledPhoneNumber)

  const [step, setStep] = useState('IDENTITY') // IDENTITY -> OTP -> DONE
  const [form, setForm] = useState({
    name: preFilledName,
    birthDate: preFilledBirthDate,
    phoneNumber: preFilledPhoneNumber
  })

  const [otpCode, setOtpCode] = useState('')
  const [otpRequestId, setOtpRequestId] = useState('')
  const [otpPreview, setOtpPreview] = useState(null)
  const [msg, setMsg] = useState('')
  const [loading, setLoading] = useState(false)

  const canPostToOpener = !!window.opener && !!openerOrigin

  const postFail = (reason) => {
    if (!canPostToOpener) return
    window.opener.postMessage({ type: 'AUTH_FAIL', reason }, openerOrigin)
  }

  // Update form if not pre-filled (though initially set by useState)
  useEffect(() => {
    if (isPreFilled) {
      setForm({
        name: preFilledName,
        birthDate: preFilledBirthDate,
        phoneNumber: preFilledPhoneNumber
      })
    }
  }, [isPreFilled, preFilledName, preFilledBirthDate, preFilledPhoneNumber])


  const onChange = (e) => {
    if (isPreFilled) return; // Prevent editing if pre-filled
    setForm((p) => ({ ...p, [e.target.name]: e.target.value }))
  }

  const handleIdentityVerifyAndRequestOtp = async () => {
    setMsg('')
    setLoading(true)
    try {
      const idResp = await api.identityVerify({
        name: form.name.trim(),
        birthDate: form.birthDate.trim(),
        phoneNumber: form.phoneNumber.trim(),
      })

      if (!idResp.verified) {
        // IDENTITY_NOT_FOUND or mismatched
        setMsg(`본인 확인 실패: ${idResp.resultCode}`)
        // Only close/fail completely if it's strictly not found or final error
        // But per request: "창 닫지 말고 ... 경고 메세지만 띄우는 쪽으로 바꿔줘"
        // "DB에 조회가 안되어서 IDENTITY_NOT_FOUND 일때만 팝업 창을 닫아줘"

        if (idResp.resultCode === 'IDENTITY_NOT_FOUND') {
          postFail(`IDENTITY_${idResp.resultCode}`)
          setTimeout(() => window.close(), 1500);
        }
        return
      }

      const otpResp = await api.otpRequest({
        verificationToken: idResp.verificationToken,
      })

      if (!otpResp.requestId) {
        setMsg('OTP 요청 실패(토큰 만료/재사용 등)')
        return
      }

      setOtpRequestId(otpResp.requestId)
      setOtpPreview(otpResp.otpPreview ?? null)
      setStep('OTP')
      setMsg('인증번호가 발송되었습니다.')
    } catch (e) {
      // General API exceptions (Network, etc)
      setMsg(`오류: ${e.message}`)
      // Don't close window here, just show error
    } finally {
      setLoading(false)
    }
  }

  const handleOtpVerify = async () => {
    setMsg('')
    setLoading(true)
    try {
      const vResp = await api.otpVerify({
        requestId: otpRequestId,
        otpCode: otpCode.trim(),
      })

      if (!vResp.success) {
        setMsg(`OTP 검증 실패: ${vResp.resultCode}`)
        return
      }

      const authResultToken = vResp.authResultToken
      setStep('DONE')
      setMsg('인증 성공! 결과 토큰을 전달합니다.')

      if (canPostToOpener) {
        window.opener.postMessage(
          { type: 'AUTH_SUCCESS', authResultToken },
          openerOrigin,
        )
      }

      setTimeout(() => window.close(), 350)
    } catch (e) {
      setMsg(`오류: ${e.message}`)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrap">
      <div className="auth-card">
        <div className="auth-header">
          <h1 className="auth-title">수탁사 본인 인증</h1>
          <p className="auth-sub">
            {isPreFilled ? "전달된 정보로 본인을 확인합니다." : "입력 정보 확인 후 OTP로 본인을 검증합니다."}
          </p>
        </div>

        <div className="auth-body">
          <div className="row">
            <span className="pill">보안 연결</span>
            <span className="helper">
              {openerOrigin ? new URL(openerOrigin).host : 'Local'}
            </span>
          </div>

          {step === 'IDENTITY' && (
            <>
              <div className="field">
                <div className="label">이름</div>
                <input
                  className={`input ${isPreFilled ? 'disabled-input' : ''}`}
                  name="name"
                  placeholder="예: 홍길동"
                  value={form.name}
                  onChange={onChange}
                  autoComplete="off"
                  readOnly={isPreFilled}
                />
              </div>

              <div className="field">
                <div className="label">생년월일</div>
                <input
                  className={`input ${isPreFilled ? 'disabled-input' : ''}`}
                  name="birthDate"
                  placeholder="YYMMDD (예: 900101)"
                  value={form.birthDate}
                  onChange={onChange}
                  inputMode="numeric"
                  autoComplete="off"
                  readOnly={isPreFilled}
                />
              </div>

              <div className="field">
                <div className="label">휴대폰 번호</div>
                <input
                  className={`input ${isPreFilled ? 'disabled-input' : ''}`}
                  name="phoneNumber"
                  placeholder="01012345678"
                  value={form.phoneNumber}
                  onChange={onChange}
                  inputMode="numeric"
                  autoComplete="off"
                  readOnly={isPreFilled}
                />
              </div>

              <button
                className="btn"
                onClick={handleIdentityVerifyAndRequestOtp}
                disabled={loading}
              >
                {loading ? '처리 중...' : '인증번호 요청'}
              </button>
            </>
          )}

          {step === 'OTP' && (
            <>
              <div className="field">
                <div className="label">요청 ID</div>
                <div className="codebox">{otpRequestId}</div>
              </div>

              {otpPreview && (
                <div className="field">
                  <div className="label">dev otpPreview</div>
                  <div className="codebox">{otpPreview}</div>
                </div>
              )}

              <div className="field">
                <div className="label">OTP 6자리</div>
                <input
                  className="input"
                  placeholder="예: 123456"
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value)}
                  inputMode="numeric"
                  autoComplete="off"
                />
              </div>

              <button
                className="btn"
                onClick={handleOtpVerify}
                disabled={loading}
              >
                {loading ? '검증 중...' : 'OTP 검증'}
              </button>
            </>
          )}

          {step === 'DONE' && (
            <div className="helper">
              완료 처리 중입니다. 잠시 후 창이 닫힙니다.
            </div>
          )}

          {msg && <div className="msg" style={{ marginTop: 20, color: '#f04452', fontWeight: 600 }}>{msg}</div>}
        </div>
      </div>

      <style>{`
        .disabled-input {
          background-color: #f3f4f6;
          color: #9ca3af;
          cursor: not-allowed;
          border-color: #e5e7eb;
        }
      `}</style>
    </div>
  )
}
