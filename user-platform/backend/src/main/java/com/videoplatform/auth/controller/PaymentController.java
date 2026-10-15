package com.videoplatform.auth.controller;

import com.videoplatform.auth.dto.CreateOrderResponse;
import com.videoplatform.auth.dto.UserResponse;
import com.videoplatform.auth.dto.VerifyPaymentRequest;
import com.videoplatform.auth.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService payments;

    public PaymentController(PaymentService payments) {
        this.payments = payments;
    }

    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(Authentication auth) {
        return payments.createOrder(auth.getName());
    }

    @PostMapping("/verify")
    public UserResponse verify(Authentication auth, @Valid @RequestBody VerifyPaymentRequest request) {
        return payments.verify(auth.getName(), request);
    }

    @PostMapping("/demo-confirm")
    public UserResponse demoConfirm(Authentication auth) {
        return payments.confirmDemo(auth.getName());
    }
}
