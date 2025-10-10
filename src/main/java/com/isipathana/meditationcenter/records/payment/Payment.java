package com.isipathana.meditationcenter.records.payment;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record Payment(
    Long paymentId,
    Long bookingId,
    BigDecimal amount,
    PaymentStatus status,
    PaymentMethod paymentMethod,
    String transactionId,
    LocalDateTime paymentDate,
    String paymentGatewayResponse,
    BigDecimal refundAmount,
    LocalDateTime refundDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
