package com.gwangjin.issuerwas.domain.service;

import com.gwangjin.issuerwas.domain.entity.Card;
import com.gwangjin.issuerwas.domain.entity.Customer;
import com.gwangjin.issuerwas.domain.repository.CardRepository;
import com.gwangjin.issuerwas.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IssuerArsService {

    private final com.gwangjin.issuerwas.common.security.RsaKeyProvider rsaKeyProvider;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;

    // 1. Identify Customer by Phone (Simulated ANI)
    public Map<String, Object> identifyCustomer(String phoneNumber) {
        Optional<Customer> customerOpt = customerRepository.findByPhone(phoneNumber);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return Map.of(
                    "found", true,
                    "name", customer.getName(),
                    "customerRef", customer.getCustomerRef());
        } else {
            return Map.of("found", false);
        }
    }

    // 2. Verify PIN and Get Cards
    public Map<String, Object> verifyPinAndGetCards(Map<String, Object> request) {
        String customerRef = (String) request.get("customerRef");
        String inputKid = (String) request.get("kid");
        String ciphertext = (String) request.get("ciphertext");

        // Validate Key ID
        if (inputKid == null || !inputKid.equals(rsaKeyProvider.getKid())) {
            return Map.of("success", false, "status", "LOCKED", "message", "Invalid Key ID");
        }

        // Decrypt PIN (OAEP)
        String pin;
        try {
            pin = decrypt(ciphertext);
        } catch (Exception e) {
            return Map.of("success", false, "status", "FAIL", "message", "Decryption Failed");
        }

        String inputHash = hash(pin);
        List<Card> cards = cardRepository.findAll().stream()
                .filter(c -> c.getCustomerRef().toString().equals(customerRef))
                .collect(Collectors.toList());

        if (cards.isEmpty()) {
            return Map.of("success", false, "status", "FAIL", "message", "No cards found");
        }

        boolean pinMatched = cards.stream()
                .anyMatch(c -> inputHash.equals(c.getPinHash()));

        if (pinMatched) {
            List<Map<String, String>> cardList = cards.stream()
                    .map(c -> Map.of(
                            "cardRef", c.getCardRef().toString(),
                            "cardNo", c.getCardNo(),
                            "status", c.getStatus()))
                    .collect(Collectors.toList());

            return Map.of(
                    "success", true,
                    "status", "SUCCESS",
                    "cards", cardList);
        } else {
            return Map.of("success", false, "status", "FAIL", "message", "Invalid PIN");
        }
    }

    private String decrypt(String ciphertext) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, rsaKeyProvider.getPrivateKey());
        return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
