package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Domain model representing a reusable schedule template.
 * Templates define the default daily schedule pattern.
 * Only one template should be active at a time.
 *
 * @author Sathira Basnayake
 */
@Builder
public record ScheduleTemplate(
    Long templateId,
    String name,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
