package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Domain model representing an activity scheduled within a template.
 * Links activities to templates with specific start and end times.
 *
 * @author Sathira Basnayake
 */
@Builder
public record TemplateScheduleActivity(
    Long id,
    Long templateId,
    Long activityId,
    LocalTime startTime,
    LocalTime endTime,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
