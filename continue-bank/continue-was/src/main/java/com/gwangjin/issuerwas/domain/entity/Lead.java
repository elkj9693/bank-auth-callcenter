package com.gwangjin.issuerwas.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbound_leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue
    private UUID leadId;

    @Column(nullable = false)
    private UUID userRef;

    private String consentMarketing; // Y/N
    private String consentOutboundCall; // Y/N
    private LocalDateTime consentTimestamp;

    private String requestedProductType;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    private LocalDateTime retentionUntil;
}
