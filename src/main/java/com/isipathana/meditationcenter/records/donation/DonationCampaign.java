package com.isipathana.meditationcenter.records.donation;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record DonationCampaign(
    Long campaignId,
    String campaignName,
    String description,
    BigDecimal targetAmount,
    BigDecimal currentAmount,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
