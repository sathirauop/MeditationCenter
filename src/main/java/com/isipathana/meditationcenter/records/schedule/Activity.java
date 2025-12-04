package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Domain model representing a reusable activity definition.
 * Activities can be used in both schedule templates and overrides.
 *
 * @author Sathira Basnayake
 */
@Builder
public record Activity(
    Long activityId,
    String title,
    String description,
    String mediaUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
