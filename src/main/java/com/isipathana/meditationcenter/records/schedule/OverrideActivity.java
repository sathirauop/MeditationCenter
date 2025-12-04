package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Domain model representing an activity scheduled for a specific override date.
 * When an override exists for a date, all activities for that day come from this table.
 * Activities can be marked as cancelled via the isCancelled flag.
 *
 * @author Sathira Basnayake
 */
@Builder
public record OverrideActivity(
    Long id,
    Long overrideId,
    Long activityId,
    LocalTime startTime,
    LocalTime endTime,
    String notes,
    Boolean isCancelled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
