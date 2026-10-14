package com.videoplatform.auth.service;

import com.videoplatform.auth.model.User;
import com.videoplatform.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class SubscriptionService {
    public static final int PREMIUM_DAYS = 30;
    private final UserRepository users;

    public SubscriptionService(UserRepository users) {
        this.users = users;
    }

    public boolean isPremiumActive(User u) {
        return "PREMIUM".equals(u.getSubscriptionType())
                && u.getSubscriptionExpiry() != null
                && u.getSubscriptionExpiry().isAfter(Instant.now());
    }

    /** Premium expire ho gaya ho to wapas FREE kar deta h. */
    public User refresh(User u) {
        if ("PREMIUM".equals(u.getSubscriptionType()) && !isPremiumActive(u)) {
            u.setSubscriptionType("FREE");
            u.setSubscriptionExpiry(null);
            users.save(u);
        }
        return u;
    }

    public User activatePremium(User u) {
        u.setSubscriptionType("PREMIUM");
        u.setSubscriptionExpiry(Instant.now().plus(Duration.ofDays(PREMIUM_DAYS)));
        return users.save(u);
    }
}
