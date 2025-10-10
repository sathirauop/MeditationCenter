package com.isipathana.meditationcenter.records.schedule;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Activity(
    Long activityId,
    String title,
    String description,
    String location,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
