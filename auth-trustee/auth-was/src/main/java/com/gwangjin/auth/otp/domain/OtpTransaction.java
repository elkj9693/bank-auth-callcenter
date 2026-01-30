package com.gwangjin.auth.otp.domain;

/*
DB 테이블 설계(Entity)

이 요청을 식별하는 ID (requestId)
subscriberId (가입자 레퍼런스 DB의 식별자)
OTP(원문이 아니라 해시)
상태(REQUESTED/VERIFIED/EXPIRED/LOCKED)
시도횟수
만료시간
생성시간

※ 15단계부터는 OTP 요청이 verificationToken 기반이므로,
   OTP 트랜잭션은 phoneHash 대신 subscriberId로 연결하는 것이 실무형 구조에 더 적합합니다.
*/

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "otp_transaction", indexes = {
        @Index(name = "idx_otp_tx_subscriber_id", columnList = "subscriberId"),
        @Index(name = "idx_otp_tx_expires_at", columnList = "expiresAt")
})
public class OtpTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    // 가입자 레퍼런스 DB(Subscriber)의 PK
    @Column(nullable = false)
    private UUID subscriberId;

    @Column(nullable = false, length = 128)
    private String otpHash;     // OTP 원문 저장 X → 해시만 저장

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OtpStatus status;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
