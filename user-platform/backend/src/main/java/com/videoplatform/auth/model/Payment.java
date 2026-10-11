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
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String userId;
    @Indexed(unique = true)
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private int amountPaise;
    private String status = "CREATED";   // CREATED -> PAID
    private Instant createdAt = Instant.now();
}
