# Premium Bank & Trustee Project Guide

이 프로젝트는 위탁사(Bank), 본인인증사(Auth Trustee), 콜센터(Call Center Trustee)의 통합 시나리오를 시뮬레이션합니다.

## 📁 프로젝트 구조

- `continue-bank`: 위탁사 (은행/카드사) - PII 원천 보유
- `auth-trustee`: 수탁사 1 (본인인증사) - OTP 및 본인 확인 대행
- `callcenter-trustee`: 수탁사 2 (콜센터) - 아웃바운드 상담 및 인바운드 민원 처리

---

## 🚀 실행 방법

### 1. 데이터베이스 기동 (Docker)
루트 폴더에서 다음 명령어를 실행하여 PostgreSQL을 기동합니다.
```bash
docker-compose -p premium-bank up -d
```
> [!IMPORTANT]
> `init-db.sql`에 의해 `issuerdb`와 `authdb`가 자동으로 생성됩니다.

### 2. 백엔드(WAS) 실행
각 WAS 폴더에서 Gradle로 기동합니다. (Java 21 필요)

- **Continue WAS (Port 8081)**
  ```bash
  cd continue-bank/continue-was
  ./gradlew bootRun
  ```
- **Auth WAS (Port 8082)**
  ```bash
  cd auth-trustee/auth-was
  ./gradlew bootRun
  ```
- **CallCenter WAS (Port 8080)**
  ```bash
  cd callcenter-trustee/callcenter-was
  ./gradlew bootRun
  ```

### 3. 프론트엔드(Web) 실행
각 Web 폴더에서 npm으로 기동합니다.

- **Continue Web (Port 3001)**
  ```bash
  cd continue-bank/continue-web
  npm run dev
  ```
- **Auth Web (Port 3000)**
  ```bash
  cd auth-trustee/auth-web
  npm run dev
  ```
- **CallCenter Web (Port 5175)**
  ```bash
  cd callcenter-trustee/callcenter-web
  npm run dev
  ```

### 4. ARS 시뮬레이터 (Inbound)
전화 분실 신고 시뮬레이션을 위해 별도의 CLI 프로그램을 실행합니다.
```bash
# Windows
./run-ars.bat
```

---

## 🛠️ 주요 포트 정보

| 서비스 | WAS Port | Web Port | 비고 |
| :--- | :--- | :--- | :--- |
| **Continue Bank** | 8081 | 3001 | 위탁사 |
| **Auth Trustee** | 8082 | 3000 | 수탁사 1 |
| **CallCenter** | 8080 | 5175 | 수탁사 2 |
| **PostgreSQL** | 5432 | - | DB (Docker) |

---

## 🧪 테스트 시나리오 가이드

### 🔐 계정 정보 (Demo Data)
| 구분 | 이름 | ID (Username) | PW (Password) | 전화번호 | 비고 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **User A** | **hong** | **hong** | **1234** | `010-1234-5678` | 메인 테스트 계정 |
| **User B** | **kim** | **kim** | **1234** | `010-9876-5432` | 예비 계정 |

### 1️⃣ 시나리오 1: 웹 로그인 및 2차 인증 (2FA)
1.  **[Continue Bank Web](http://localhost:3001)** 접속
2.  로그인 시도: `hong` / `1234`
3.  **2단계 인증(OTP)** 화면 진입
4.  **Auth WAS 로그 확인**:
    *   `auth-trustee/auth-was` 터미널(또는 콘솔)에서 `[OTP REQUESTED] ... OTP=XXXXXX` 로그 확인
5.  OTP 6자리 입력 -> 로그인 완료
6.  **"결제하기"** 버튼 클릭 -> 가상 카드 확인 -> 결제 승인

### 2️⃣ 시나리오 2: 아웃바운드 상담 (Lead -> Call)
1.  **[Continue Bank Web](http://localhost:3001)** 메인 하단 **"상담 신청"** 배너 클릭
2.  상품(대출/카드 등) 선택 및 필수 동의 체크 -> 신청 완료 (Lead 생성)
3.  **[Call Center Web](http://localhost:5175)** 접속
4.  **"아웃바운드"** 탭 클릭 -> 방금 신청한 홍길동 고객 확인
5.  **"발신"** 버튼 클릭 -> 통화 연결 시뮬레이션 -> 상담 결과(완료) 저장

### 3️⃣ 시나리오 3: 인바운드 분실 신고 (ARS)
1.  **ARS 시뮬레이터** 실행 (위 가이드 참고)
2.  **환영 메시지**: "안녕하세요, Premium Bank ARS입니다."
3.  **본인 확인**:
    *   전화번호 입력: `01012345678`
    *   비밀번호(PIN) 입력: `1234`
4.  **서비스 선택**:
    *   `[1] 잔액 조회` / `[2] 분실 신고` 중 **2번** 선택
5.  **카드 선택 및 정지**:
    *   보유 카드 목록 청취 (TTS 시뮬레이션)
    *   분실된 카드 번호 선택
    *   **"정지되었습니다."** 메시지 확인
