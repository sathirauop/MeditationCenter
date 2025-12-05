package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model representing a schedule override for a specific date.
 * When an override exists for a date, the entire day's schedule
 * comes from override_activity table instead of the template.
 *
 * @author Sathira Basnayake
 */
@Builder
public record ScheduleOverride(
    Long overrideId,
    LocalDate overrideDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
