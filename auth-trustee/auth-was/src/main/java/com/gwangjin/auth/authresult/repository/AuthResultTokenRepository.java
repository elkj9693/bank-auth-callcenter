package com.gwangjin.auth.authresult.repository;

import com.gwangjin.auth.authresult.domain.AuthResultToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthResultTokenRepository extends JpaRepository<AuthResultToken, UUID> {
    Optional<AuthResultToken> findByTokenValue(String tokenValue);
}
