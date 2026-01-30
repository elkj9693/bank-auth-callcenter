package com.gwangjin.auth.identity.repository;

import com.gwangjin.auth.identity.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByTokenValue(String tokenValue);
}
