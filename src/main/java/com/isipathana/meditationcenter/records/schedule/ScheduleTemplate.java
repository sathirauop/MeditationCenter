package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduleTemplate(
    Long templateId,
    String name,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
