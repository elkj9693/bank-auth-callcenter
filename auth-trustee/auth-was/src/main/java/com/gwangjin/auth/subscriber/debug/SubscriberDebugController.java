package com.gwangjin.auth.subscriber.debug;

import com.gwangjin.auth.common.crypto.Sha256Util;
import com.gwangjin.auth.subscriber.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
public class SubscriberDebugController {

    private final SubscriberRepository subscriberRepository;

    @GetMapping("/debug/subscriber/exists")
    public boolean exists(@RequestParam String phoneNumber) {
        String phoneHash = Sha256Util.sha256(phoneNumber);
        return subscriberRepository.findByPhoneHash(phoneHash).isPresent();
    }
}
