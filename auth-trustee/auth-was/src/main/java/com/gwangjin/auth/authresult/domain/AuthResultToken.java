package com.gwangjin.auth.authresult.domain;

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
@Table(name = "auth_result_token", indexes = {
        @Index(name = "idx_auth_result_token_value", columnList = "tokenValue"),
        @Index(name = "idx_auth_result_expires_at", columnList = "expiresAt"),
        @Index(name = "idx_auth_result_subscriber_id", columnList = "subscriberId")
})
public class AuthResultToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenValue;

    @Column(nullable = false)
    private UUID subscriberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AuthResultTokenStatus status;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
