package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record ScheduleOverride(
    Long overrideId,
    Long templateScheduleActivityId,
    LocalDate overrideDate,
    LocalTime newStartTime,
    LocalTime newEndTime,
    Boolean isCancelled,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
