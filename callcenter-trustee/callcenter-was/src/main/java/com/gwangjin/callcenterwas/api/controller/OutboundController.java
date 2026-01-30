package com.gwangjin.callcenterwas.api.controller;

import com.gwangjin.callcenterwas.infrastructure.client.IssuerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/outbound")
public class OutboundController {

    private final IssuerClient issuerClient;

    @GetMapping("/targets")
    public List<Map<String, Object>> getTargets() {
        // Step C2: Fetch from Continue Bank
        List<Map<String, Object>> leads = issuerClient.fetchLeads();

        // Step C2: Masking (Simulated)
        return leads.stream().map(lead -> {
            lead.put("maskedName", "***");
            lead.put("maskedPhone", "010-****-****");
            return lead;
        }).collect(Collectors.toList());
    }

    @PostMapping("/result")
    public void submitResult(@RequestBody Map<String, String> req) {
        String leadId = req.get("leadId");
        // Step C5: Save result meta (simulated) and send back to Continue Bank
        issuerClient.updateLeadResult(leadId, req);

        // Step C3: Log Access / Action
        issuerClient.sendAuditEvent(Map.of(
                "eventType", "OUTBOUND_CALL",
                "resultCode", "SUCCESS",
                "operatorId", req.get("operatorId"),
                "notes", req.get("outcome")));
    }
}
