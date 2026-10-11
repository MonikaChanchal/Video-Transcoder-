package com.videoplatform.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;          // BCrypt hash, plain password kabhi nahi
    private AccountType accountType;
    private String role = "USER";     // baad me ADMIN add ho sakta h
    private String subscriptionType = "FREE"; // subscription feature isse update karega
    private Instant subscriptionExpiry;
    private Instant createdAt = Instant.now();
}
