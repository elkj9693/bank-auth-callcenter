package com.gwangjin.auth.subscriber.repository;

import com.gwangjin.auth.subscriber.domain.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    Optional<Subscriber> findByPhoneHash(String phoneHash);
}
