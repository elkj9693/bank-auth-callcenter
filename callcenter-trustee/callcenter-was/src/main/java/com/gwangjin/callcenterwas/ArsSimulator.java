package com.gwangjin.callcenterwas;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class ArsSimulator {

    // Pointing to Call Center (Trustee) - Port 8080
    private static final String API_BASE = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    // private static final Scanner scanner = new Scanner(System.in); // Moved to
    // main

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out), true,
                    java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Squelch
        }
        Scanner scanner = new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8);

        System.out.println("==========================================");
        System.out.println("         \uCE74\uB4DC \uBD84\uC2E4 \uC2E0\uACE0 ARS \uC2DC\uC2A4\uD15C        "); // 카드 분실
                                                                                                              // 신고 ARS
                                                                                                              // 시스템
        System.out.println("==========================================");

        try {
            // 1. ANI Detection
            System.out.println(
                    "[ARS] Countinue Card \uBD84\uC2E4 \uC2E0\uACE0 ARS \uC2DC\uC2A4\uD15C\uC785\uB2C8\uB2E4. \uACE0\uAC1D \uC815\uBCF4\uB97C \uD655\uC778 \uC911\uC785\uB2C8\uB2E4."); // 분실
                                                                                                                                                                                        // 신고
                                                                                                                                                                                        // ARS
                                                                                                                                                                                        // 시스템입니다.
                                                                                                                                                                                        // 고객
                                                                                                                                                                                        // 정보를
                                                                                                                                                                                        // 확인
                                                                                                                                                                                        // 중입니다.
            System.out.print(
                    "\uD734\uB300\uD3F0 \uBC88\uD638\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694 (\uC608: 01012345678): \n> "); // 휴대폰
                                                                                                                                // 번호를
                                                                                                                                // 입력해
                                                                                                                                // 주세요
                                                                                                                                // (예:
                                                                                                                                // 01012345678):
            String phone = scanner.nextLine().trim();

            Map<String, String> idReq = new HashMap<>();
            idReq.put("phoneNumber", phone);

            Map<String, Object> idRes = post("/callcenter/ars/identify", idReq);

            if (idRes == null || !Boolean.TRUE.equals(idRes.get("found"))) {
                System.out.println(
                        "[ARS] \uC8C4\uC1A1\uD569\uB2C8\uB2E4. \uBC88\uD638\uB97C \uC2DD\uBCC4\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4. \uC0C1\uB2F4\uC6D0 \uC5F0\uACB0\uC744 \uC6D0\uD558\uC2DC\uBA74 0\uBC88\uC744 \uB20C\uB7EC\uC8FC\uC138\uC694."); // 죄송합니다.
                                                                                                                                                                                                                                                      // 번호를
                                                                                                                                                                                                                                                      // 식별할
                                                                                                                                                                                                                                                      // 수
                                                                                                                                                                                                                                                      // 없습니다.
                                                                                                                                                                                                                                                      // 상담원
                                                                                                                                                                                                                                                      // 연결을
                                                                                                                                                                                                                                                      // 원하시면
                                                                                                                                                                                                                                                      // 0번을
                                                                                                                                                                                                                                                      // 눌러주세요.
                return;
            }

            String name = (String) idRes.get("name");
            String customerRef = (String) idRes.get("customerRef");

            // 2. Identity Confirmation
            System.out.println("\n[ARS] " + name + " \uACE0\uAC1D\uB2D8, \uC548\uB155\uD558\uC138\uC694."); // 고객님,
                                                                                                            // 안녕하세요.
            System.out.print(
                    "[ARS] \uBCF8\uC778\uC774 \uB9DE\uC73C\uC2DC\uBA74 1\uBC88, \uC544\uB2C8\uBA74 2\uBC88\uC744 \uB20C\uB7EC\uC8FC\uC138\uC694.\n> "); // 본인이
                                                                                                                                                        // 맞으시면
                                                                                                                                                        // 1번,
                                                                                                                                                        // 아니면
                                                                                                                                                        // 2번을
                                                                                                                                                        // 눌러주세요.
            String confirm = scanner.nextLine().trim();

            if (!"1".equals(confirm)) {
                System.out.println(
                        "[ARS] \uC0C1\uB2F4\uC6D0\uC744 \uC5F0\uACB0\uD574 \uB4DC\uB9AC\uACA0\uC2B5\uB2C8\uB2E4..."); // 상담원을
                                                                                                                      // 연결해
                                                                                                                      // 드리겠습니다...
                return;
            }

            // 3. PIN Verification (Plain DTMF -> Server Encrypts)
            System.out.print(
                    "\n[ARS] \uCE74\uB4DC \uBE44\uBC00\uBC88\uD638 4\uC790\uB9AC\uB97C \uC785\uB825\uD574 \uC8FC\uC138\uC694.\n> "); // 카드
                                                                                                                                     // 비밀번호
                                                                                                                                     // 4자리를
                                                                                                                                     // 입력해
                                                                                                                                     // 주세요.
            String pin = scanner.nextLine().trim();

            Map<String, String> pinReq = new HashMap<>();
            pinReq.put("customerRef", customerRef);
            pinReq.put("pin", pin); // Sending Plain PIN (DTMF)

            Map<String, Object> pinRes = post("/callcenter/ars/verify-pin", pinReq);

            if (pinRes == null || !Boolean.TRUE.equals(pinRes.get("success"))) {
                System.out.println(
                        "[ARS] \uBE44\uBC00\uBC88\uD638 \uC785\uB825\uC5D0 \uC2E4\uD328\uD588\uC2B5\uB2C8\uB2E4. \uC0C1\uB2F4\uC6D0\uC744 \uC5F0\uACB0\uD569\uB2C8\uB2E4..."); // 비밀번호
                                                                                                                                                                               // 입력에
                                                                                                                                                                               // 실패했습니다.
                                                                                                                                                                               // 상담원을
                                                                                                                                                                               // 연결합니다...
                return;
            }

            List<Map<String, Object>> cards = (List<Map<String, Object>>) pinRes.get("cards");
            System.out.println("\n[ARS] \uBCF8\uC778 \uD655\uC778\uC774 \uC644\uB8CC\uB418\uC5C8\uC2B5\uB2C8\uB2E4."); // 본인
                                                                                                                       // 확인이
                                                                                                                       // 완료되었습니다.
            System.out.println("[ARS] \uD604\uC7AC \uC0AC\uC6A9 \uC911\uC778 \uCE74\uB4DC\uAC00 " + cards.size()
                    + "\uC7A5 \uC788\uC2B5\uB2C8\uB2E4."); // 현재 사용 중인 카드가 ...장 있습니다.

            // 4. Card Selection
            System.out.println("------------------------------------------");
            for (int i = 0; i < cards.size(); i++) {
                Map<String, Object> card = cards.get(i);
                String status = (String) card.get("status");
                String statusDesc = "ACTIVE".equals(status) ? "\uC815\uC0C1 \uC0AC\uC6A9 \uC911"
                        : "\uBD84\uC2E4 \uC2E0\uACE0 \uC644\uB8CC"; // 정상 사용 중 : 분실 신고 완료
                System.out.println((i + 1) + "\uBC88. " + card.get("cardNo") + " (" + statusDesc + ")"); // 번.
            }
            System.out.println("------------------------------------------");

            System.out.println(
                    "[ARS] \uBD84\uC2E4 \uC2E0\uACE0\uD560 \uCE74\uB4DC \uBC88\uD638\uB97C \uC120\uD0DD\uD574 \uC8FC\uC138\uC694."); // 분실
                                                                                                                                     // 신고할
                                                                                                                                     // 카드
                                                                                                                                     // 번호를
                                                                                                                                     // 선택해
                                                                                                                                     // 주세요.
            System.out.print(
                    "[ARS] \uC885\uB8CC\uB97C \uC6D0\uD558\uC2DC\uBA74 0\uBC88\uC744 \uB20C\uB7EC\uC8FC\uC138\uC694.\n> "); // 종료를
                                                                                                                            // 원하시면
                                                                                                                            // 0번을
                                                                                                                            // 눌러주세요.
            int limit = cards.size();
            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
            }

            if (choice < 1 || choice > limit) {
                System.out.println("[ARS] \uC774\uC6A9\uD574 \uC8FC\uC154\uC11C \uAC10\uC0AC\uD569\uB2C8\uB2E4."); // 이용해
                                                                                                                   // 주셔서
                                                                                                                   // 감사합니다.
                return;
            }

            Map<String, Object> selectedCard = cards.get(choice - 1);
            String cardRef = (String) selectedCard.get("cardRef");

            // 5. Report Loss
            System.out.println(
                    "\n[ARS] \uC120\uD0DD\uD558\uC2E0 \uCE74\uB4DC\uC5D0 \uB300\uD574 \uBD84\uC2E4 \uC2E0\uACE0\uB97C \uC9C4\uD589\uD569\uB2C8\uB2E4."); // 선택하신
                                                                                                                                                         // 카드에
                                                                                                                                                         // 대해
                                                                                                                                                         // 분실
                                                                                                                                                         // 신고를
                                                                                                                                                         // 진행합니다.
            System.out.println("[ARS] \uC7A0\uC2DC\uB9CC \uAE30\uB2E4\uB824 \uC8FC\uC138\uC694..."); // 잠시만 기다려 주세요...

            Map<String, Object> lossReq = new HashMap<>();
            lossReq.put("customerRef", customerRef);
            lossReq.put("selectedCardRefs", List.of(cardRef));
            lossReq.put("lossType", "ARS_LOSS");

            Map<String, Object> lossRes = post("/callcenter/ars/report-loss", lossReq);

            if (lossRes != null && lossRes.get("lossCaseId") != null) {
                System.out.println(
                        "\n[ARS] \uBD84\uC2E4 \uC2E0\uACE0\uAC00 \uC815\uC0C1\uC801\uC73C\uB85C \uC811\uC218\uB418\uC5C8\uC2B5\uB2C8\uB2E4."); // 분실
                                                                                                                                               // 신고가
                                                                                                                                               // 정상적으로
                                                                                                                                               // 접수되었습니다.
                System.out.println(
                        "[ARS] \uC811\uC218 \uBC88\uD638\uB294 " + lossRes.get("lossCaseId") + " \uC785\uB2C8\uB2E4."); // 접수
                                                                                                                        // 번호는
                                                                                                                        // ...
                                                                                                                        // 입니다.
                System.out.println("[ARS] \uC774\uC6A9\uD574 \uC8FC\uC154\uC11C \uAC10\uC0AC\uD569\uB2C8\uB2E4."); // 이용해
                                                                                                                   // 주셔서
                                                                                                                   // 감사합니다.
            } else {
                System.out.println(
                        "\n[ARS] \uC2E4\uD328\uD588\uC2B5\uB2C8\uB2E4. \uC2DC\uC2A4\uD15C \uC624\uB958\uAC00 \uBC1C\uC0DD\uD588\uC2B5\uB2C8\uB2E4."); // 실패했습니다.
                                                                                                                                                      // 시스템
                                                                                                                                                      // 오류가
                                                                                                                                                      // 발생했습니다.
            }

        } catch (Exception e) {
            // Squelch
        }
    }

    private static Map<String, Object> post(String uri, Map<String, ?> body) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + uri))
                .header("Content-Type", "application/json")
                .header("X-Service-Token", "local-dev-token") // Matches application.properties default
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }
        return mapper.readValue(response.body(), Map.class);
    }
}
