package com.videoplatform.auth.dto;

public record CreateOrderResponse(String orderId, int amount, String currency, String keyId, boolean demo) {}
