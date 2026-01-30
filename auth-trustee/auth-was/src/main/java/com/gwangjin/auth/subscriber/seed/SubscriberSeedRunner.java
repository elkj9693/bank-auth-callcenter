package com.gwangjin.auth.subscriber.seed;

import com.gwangjin.auth.common.crypto.Sha256Util;
import com.gwangjin.auth.subscriber.domain.Subscriber;
import com.gwangjin.auth.subscriber.domain.SubscriberStatus;
import com.gwangjin.auth.subscriber.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class SubscriberSeedRunner implements CommandLineRunner {

    private final SubscriberRepository subscriberRepository;

    @Override
    public void run(String... args) {
        // Upsert Logic: 기존 데이터가 있어도 덮어쓰기 (개발 편의성)
        seedOne("01012345678", "홍길동", "800101");
        seedOne("01011112222", "김철수", "20001231");
        seedOne("01099998888", "이영희", "19850707");

        System.out.println("[SEED] subscribers synced");
    }

    private void seedOne(String phoneNumber, String name, String birthDateYYYYMMDD) {
        String phoneHash = Sha256Util.sha256(phoneNumber);
        String nameHash = Sha256Util.sha256(name);
        String birthHash = Sha256Util.sha256(birthDateYYYYMMDD);

        Subscriber s = subscriberRepository.findByPhoneHash(phoneHash)
                .map(existing -> {
                    // Update existing
                    existing.setNameHash(nameHash);
                    existing.setBirthDateHash(birthHash);
                    return existing;
                })
                .orElseGet(() -> Subscriber.builder()
                        .phoneHash(phoneHash)
                        .nameHash(nameHash)
                        .birthDateHash(birthHash)
                        .status(SubscriberStatus.ACTIVE)
                        .createdAt(Instant.now())
                        .build());

        subscriberRepository.save(s);
    }
}
