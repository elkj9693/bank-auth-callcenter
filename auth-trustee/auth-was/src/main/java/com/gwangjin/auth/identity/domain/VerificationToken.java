package com.gwangjin.auth.identity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "verification_tokens",
        indexes = {
                @Index(name = "idx_vtoken_value", columnList = "tokenValue", unique = true),
                @Index(name = "idx_vtoken_subscriber_id", columnList = "subscriberId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 64, unique = true)
    private String tokenValue; // 랜덤 토큰(해시 아님). 유출 대비해 만료를 짧게.

    @Column(nullable = false)
    private UUID subscriberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = TokenStatus.ISSUED;
    }
}
