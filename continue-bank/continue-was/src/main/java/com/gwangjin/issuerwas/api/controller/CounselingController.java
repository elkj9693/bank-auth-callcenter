package com.gwangjin.issuerwas.api.controller;

import com.gwangjin.issuerwas.domain.entity.Lead;
import com.gwangjin.issuerwas.domain.entity.LeadStatus;
import com.gwangjin.issuerwas.domain.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leads")
public class CounselingController {

    private final LeadRepository leadRepository;

    @PostMapping("/apply")
    public Lead apply(@RequestBody Map<String, String> req) {
        Lead lead = Lead.builder()
                .userRef(UUID.fromString(req.get("userRef")))
                .consentMarketing(req.get("consentMarketing"))
                .consentOutboundCall(req.get("consentOutboundCall"))
                .consentTimestamp(LocalDateTime.now())
                .requestedProductType(req.get("productType"))
                .status(LeadStatus.NEW)
                .retentionUntil(LocalDateTime.now().plusMonths(3))
                .build();
        return leadRepository.save(lead);
    }

    @GetMapping("/eligible")
    public List<Lead> getEligible() {
        // Step C2: Fetch targets (no PII here, only userRef)
        return leadRepository.findByStatusAndConsentOutboundCall(LeadStatus.NEW, "Y");
    }

    @PostMapping("/{leadId}/result")
    public void updateResult(@PathVariable UUID leadId, @RequestBody Map<String, String> res) {
        Lead lead = leadRepository.findById(leadId).orElseThrow();
        lead.setStatus(LeadStatus.valueOf(res.get("outcome")));
        leadRepository.save(lead);
    }
}
