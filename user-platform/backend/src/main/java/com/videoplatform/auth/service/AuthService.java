package com.videoplatform.auth.service;

import com.videoplatform.auth.dto.*;
import com.videoplatform.auth.exception.ApiException;
import com.videoplatform.auth.model.User;
import com.videoplatform.auth.repository.UserRepository;
import com.videoplatform.auth.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final SubscriptionService subscriptions;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt,
                       SubscriptionService subscriptions) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.subscriptions = subscriptions;
    }

    public AuthResponse register(RegisterRequest r) {
        if (!r.password().equals(r.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already registered");
        }
        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        u.setAccountType(r.accountType());
        users.save(u);
        return response(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Incorrect email or password"));
        return response(subscriptions.refresh(u));
    }

    public UserResponse me(String email) {
        User u = users.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
        return UserResponse.from(subscriptions.refresh(u));
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.generateToken(u.getEmail(), u.getRole()), UserResponse.from(u));
    }
}
