package com.isipathana.meditationcenter.records.pricing;

import com.isipathana.meditationcenter.records.booking.BookingType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record Pricing(
    Long pricingId,
    BookingType bookingType,
    BigDecimal price,
    LocalDate effectiveFromDate,
    LocalDate effectiveToDate,
    Boolean isActive,
    LocalDateTime createdAt
) {}
