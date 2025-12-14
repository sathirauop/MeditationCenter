package com.isipathana.meditationcenter.records.user;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Record representing aggregated user statistics.
 * Contains counts and totals for user's activity across the system.
 *
 * @author Sathira Basnayake
 */
@Builder
public record UserStatistics(
        Integer totalBookings,
        Integer activeBookings,
        BigDecimal totalDonations,
        Integer eventRegistrations
) {
    /**
     * Creates empty statistics (all zeros).
     */
    public static UserStatistics empty() {
        return UserStatistics.builder()
                .totalBookings(0)
                .activeBookings(0)
                .totalDonations(BigDecimal.ZERO)
                .eventRegistrations(0)
                .build();
    }
}
