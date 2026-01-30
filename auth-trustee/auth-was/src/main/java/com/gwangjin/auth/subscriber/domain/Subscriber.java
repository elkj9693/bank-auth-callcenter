package com.gwangjin.auth.subscriber.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "subscribers",
        indexes = {
                @Index(name = "idx_subscriber_phone_hash", columnList = "phoneHash", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscriber {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 64, unique = true)
    private String phoneHash;

    @Column(nullable = false, length = 64)
    private String nameHash;

    @Column(nullable = false, length = 64)
    private String birthDateHash; // 권장 포맷: YYYYMMDD 를 해시

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriberStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = SubscriberStatus.ACTIVE;
    }
}
