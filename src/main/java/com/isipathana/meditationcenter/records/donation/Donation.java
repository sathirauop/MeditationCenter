package com.isipathana.meditationcenter.records.donation;

import com.isipathana.meditationcenter.records.payment.PaymentMethod;
import com.isipathana.meditationcenter.records.payment.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record Donation(
    Long donationId,
    Long campaignId,
    Long userId,
    BigDecimal donationAmount,
    PaymentMethod paymentMethod,
    String transactionId,
    PaymentStatus paymentStatus,
    String paymentGatewayResponse,
    Boolean isAnonymous,
    String donorMessage,
    LocalDateTime createdAt
) {}
