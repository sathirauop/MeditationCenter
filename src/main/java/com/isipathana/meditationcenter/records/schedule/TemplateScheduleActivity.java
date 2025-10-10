package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record TemplateScheduleActivity(
    Long id,
    Long templateId,
    Long activityId,
    LocalTime startTime,
    LocalTime endTime,
    Integer dayOfWeek,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
