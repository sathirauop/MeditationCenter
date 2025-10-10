package com.isipathana.meditationcenter.records.booking;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record Booking(
    Long bookingId,
    Long meditationProgramId,
    Long pricingId,
    Long userId,
    BookingType bookingType,
    LocalDate bookingDate,
    BookingStatus status,
    Integer participantCount,
    BigDecimal amount,
    String specialRequirements,
    String cancellationReason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
