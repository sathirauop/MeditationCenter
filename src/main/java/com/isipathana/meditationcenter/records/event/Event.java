package com.isipathana.meditationcenter.records.event;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record Event(
    Long eventId,
    String name,
    String description,
    LocalDate eventDate,
    LocalTime startTime,
    LocalTime endTime,
    String location,
    String images,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
