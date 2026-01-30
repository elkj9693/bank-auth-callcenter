package com.gwangjin.issuerwas.domain.repository;

import com.gwangjin.issuerwas.domain.entity.Lead;
import com.gwangjin.issuerwas.domain.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    List<Lead> findByStatusAndConsentOutboundCall(LeadStatus status, String consentOutboundCall);
}
