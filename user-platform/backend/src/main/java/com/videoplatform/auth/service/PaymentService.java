package com.videoplatform.auth.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.videoplatform.auth.dto.CreateOrderResponse;
import com.videoplatform.auth.dto.UserResponse;
import com.videoplatform.auth.dto.VerifyPaymentRequest;
import com.videoplatform.auth.exception.ApiException;
import com.videoplatform.auth.model.Payment;
import com.videoplatform.auth.model.User;
import com.videoplatform.auth.repository.PaymentRepository;
import com.videoplatform.auth.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class PaymentService {
    public static final int PREMIUM_PRICE_PAISE = 49900;   // Rs 499

    private final UserRepository users;
    private final PaymentRepository payments;
    private final SubscriptionService subscriptions;
    private final String keyId;
    private final String keySecret;
    private final boolean demoMode;

    public PaymentService(UserRepository users, PaymentRepository payments, SubscriptionService subscriptions,
                          @Value("${razorpay.key-id}") String keyId,
                          @Value("${razorpay.key-secret}") String keySecret,
                          @Value("${payment.demo-mode:false}") boolean demoMode) {
        this.users = users;
        this.payments = payments;
        this.subscriptions = subscriptions;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.demoMode = demoMode;
    }

    public CreateOrderResponse createOrder(String email) {
        if (demoMode) {
            if (subscriptions.isPremiumActive(findUser(email))) {
                throw new ApiException(HttpStatus.CONFLICT, "You already have an active Premium plan");
            }
            return new CreateOrderResponse("demo", PREMIUM_PRICE_PAISE, "INR", "", true);
        }
        if (keyId.isBlank() || keySecret.isBlank()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Payments are not set up yet. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET and restart the backend.");
        }
        User u = findUser(email);
        if (subscriptions.isPremiumActive(u)) {
            throw new ApiException(HttpStatus.CONFLICT, "You already have an active Premium plan");
        }
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject req = new JSONObject();
            req.put("amount", PREMIUM_PRICE_PAISE);
            req.put("currency", "INR");
            req.put("receipt", "rcpt_" + System.currentTimeMillis());
            Order order = client.orders.create(req);
            String orderId = order.get("id");

            Payment p = new Payment();
            p.setUserId(u.getId());
            p.setRazorpayOrderId(orderId);
            p.setAmountPaise(PREMIUM_PRICE_PAISE);
            payments.save(p);
            return new CreateOrderResponse(orderId, PREMIUM_PRICE_PAISE, "INR", keyId, false);
        } catch (RazorpayException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Could not start the payment. Check your Razorpay keys.");
        }
    }

    /** Signature server par verify hoti h. Frontend ke bharose Premium kabhi activate nahi hota. */
    public UserResponse verify(String email, VerifyPaymentRequest r) {
        User u = findUser(email);
        Payment p = payments.findByRazorpayOrderId(r.razorpayOrderId())
                .filter(x -> x.getUserId().equals(u.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown payment order"));
        if ("PAID".equals(p.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "This payment was already processed");
        }
        String expected = hmacHex(r.razorpayOrderId() + "|" + r.razorpayPaymentId(), keySecret);
        boolean ok = MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                r.razorpaySignature().getBytes(StandardCharsets.UTF_8));
        if (!ok) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment could not be verified");
        }
        p.setStatus("PAID");
        p.setRazorpayPaymentId(r.razorpayPaymentId());
        payments.save(p);
        return UserResponse.from(subscriptions.activatePremium(u));
    }

    /** Sirf demo-mode me chalta h: Razorpay ke bina Premium activate (presentation ke liye). */
    public UserResponse confirmDemo(String email) {
        if (!demoMode) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Demo payments are turned off");
        }
        User u = findUser(email);
        if (subscriptions.isPremiumActive(u)) {
            throw new ApiException(HttpStatus.CONFLICT, "You already have an active Premium plan");
        }
        Payment p = new Payment();
        p.setUserId(u.getId());
        p.setRazorpayOrderId("demo_" + System.currentTimeMillis());
        p.setRazorpayPaymentId("demo");
        p.setAmountPaise(PREMIUM_PRICE_PAISE);
        p.setStatus("PAID");
        payments.save(p);
        return UserResponse.from(subscriptions.activatePremium(u));
    }

    private User findUser(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private static String hmacHex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
