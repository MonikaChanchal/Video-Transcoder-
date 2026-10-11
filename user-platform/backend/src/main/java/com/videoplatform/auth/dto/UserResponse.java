package com.videoplatform.auth.dto;

import com.videoplatform.auth.model.AccountType;
import com.videoplatform.auth.model.User;

import java.time.Instant;

public record UserResponse(String id, String name, String email, AccountType accountType,
                           String role, String subscriptionType, Instant subscriptionExpiry) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getAccountType(),
                u.getRole(), u.getSubscriptionType(), u.getSubscriptionExpiry());
    }
}
